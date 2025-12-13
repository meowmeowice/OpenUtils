package org.afterlike.openutils.module.impl.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.afterlike.openutils.event.api.EventPhase;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.GameTickEvent;
import org.afterlike.openutils.event.impl.RenderWorldEvent;
import org.afterlike.openutils.event.impl.WorldLoadEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.api.setting.impl.ModeSetting;
import org.afterlike.openutils.module.api.setting.impl.NumberSetting;
import org.afterlike.openutils.platform.mixin.minecraft.client.renderer.EntityRendererAccessor;
import org.afterlike.openutils.util.client.ClientUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

// TODO: clean up this code lol
public class DamageTagsModule extends Module {
	private final BooleanSetting showHealing;
	private final BooleanSetting showDamage;
	private final ModeSetting mode;
	private final ModeSetting colorMode;
	private final NumberSetting scale;
	private final NumberSetting duration;
	private final NumberSetting yOffset;
	private final Map<String, Float> playerHealth = new HashMap<>();
	private final List<Map<String, Object>> objects = new ArrayList<>();
	private static final int GREEN = new Color(0, 255, 0).getRGB();
	private static final int RED = new Color(255, 0, 0).getRGB();
	private static final double FADE_DISTANCE = 2.0;
	private static final double MIN_DISTANCE = 0.5;
	private static final long FADE_OUT_TIME = 150;
	private static final FloatBuffer MODEL_VIEW = BufferUtils.createFloatBuffer(16);
	private static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer(16);
	private static final IntBuffer VIEWPORT = BufferUtils.createIntBuffer(16);
	private static final FloatBuffer OBJECT_COORDS = BufferUtils.createFloatBuffer(3);
	public DamageTagsModule() {
		super("Damage Tags", ModuleCategory.RENDER);
		showHealing = this.registerSetting(new BooleanSetting("Show Healing", true));
		showDamage = this.registerSetting(new BooleanSetting("Show Damage", true));
		mode = this.registerSetting(new ModeSetting("Mode", "Hearts", "Hearts", "Health Points"));
		colorMode = this.registerSetting(new ModeSetting("Color", "RAG", "RAG", "Team"));
		scale = this.registerSetting(new NumberSetting("Scale", 5.0, 0.0, 10.0, 0.5));
		duration = this.registerSetting(new NumberSetting("Duration", 1.5, 0.0, 5.0, 0.1));
		yOffset = this.registerSetting(new NumberSetting("Y Offset", 1.8, -2.0, 3.0, 0.1));
	}

	@EventHandler
	private void onTick(final GameTickEvent event) {
		if (event.getPhase() != EventPhase.POST)
			return;
		if (!ClientUtil.notNull())
			return;
		if (mc.theWorld == null)
			return;
		final Vec3 me = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		final long now = System.currentTimeMillis();
		final int modeValue = mode.getIndex();
		final int colorModeValue = colorMode.getIndex();
		final boolean showHealingValue = showHealing.getValue();
		final boolean showDamageValue = showDamage.getValue();
		final double yOffsetValue = yOffset.getValue();
		for (final Entity entity : mc.theWorld.loadedEntityList) {
			if (!(entity instanceof EntityPlayer))
				continue;
			final EntityPlayer player = (EntityPlayer) entity;
			if (player.isDead)
				continue;
			final String entityId = String.valueOf(player.getEntityId()) + player.getUniqueID();
			final float hp = player.getHealth() + player.getAbsorptionAmount();
			final float health = hp / (modeValue == 0 ? 2.0f : 1.0f);
			final float lastHp = playerHealth.getOrDefault(entityId, hp);
			final float lastHealth = lastHp / (modeValue == 0 ? 2.0f : 1.0f);
			playerHealth.put(entityId, hp);
			if (player.ticksExisted < 2 || health == lastHealth
					|| (!showDamageValue && health < lastHealth)
					|| (!showHealingValue && health > lastHealth))
				continue;
			final float difference = health - lastHealth;
			String renderHealth;
			final int color = health > lastHealth ? GREEN : RED;
			if (colorModeValue == 1) {
				final String displayName = player.getDisplayName().getFormattedText();
				final String teamColorCode = displayName.length() >= 2
						? displayName.substring(0, 2)
						: "§r";
				renderHealth = teamColorCode + formatDoubleStr(round(difference, 1));
			} else {
				renderHealth = formatDoubleStr(round(Math.abs(difference), 1));
			}
			if (renderHealth.endsWith("0"))
				continue;
			final Vec3 position = new Vec3(player.posX, player.posY + yOffsetValue, player.posZ);
			final Map<String, Object> object = new HashMap<>();
			object.put("position", position);
			object.put("time", now);
			object.put("color", color);
			object.put("health", renderHealth);
			object.put("distance", position.distanceTo(me));
			object.put("lastdistance", position.distanceTo(me));
			objects.add(object);
		}
		if (!objects.isEmpty()) {
			for (final Map<String, Object> object : objects) {
				object.put("lastdistance", object.get("distance"));
				final Vec3 markerPosition = (Vec3) object.get("position");
				final double distance = me.distanceTo(markerPosition);
				object.put("distance", distance);
			}
			objects.sort((a, b) -> Double.compare((double) b.get("distance"),
					(double) a.get("distance")));
		}
	}

	@EventHandler
	private void onRenderWorld(final RenderWorldEvent event) {
		if (!ClientUtil.notNull())
			return;
		final long now = System.currentTimeMillis();
		final float partialTicks = event.getPartialTicks();
		final long durationMs = (long) (duration.getValue() * 1000);
		final double baseScale = scale.getValue();
		final ScaledResolution res = new ScaledResolution(mc);
		final int scaleFactor = res.getScaleFactor();
		for (final Iterator<Map<String, Object>> it = objects.iterator(); it.hasNext();) {
			final Map<String, Object> object = it.next();
			final long elapsed = now - (long) object.get("time");
			if (elapsed > durationMs + FADE_OUT_TIME) {
				it.remove();
				continue;
			}
			final double distance = (double) object.get("distance");
			if (distance > 25)
				continue;
			final Vec3 position = (Vec3) object.get("position");
			int color = (int) object.get("color");
			final String health = (String) object.get("health");
			int alpha = 255;
			if (elapsed > durationMs) {
				alpha = (int) (255 * (1.0 - ((double) (elapsed - durationMs) / FADE_OUT_TIME)));
			}
			if (alpha <= 5) {
				it.remove();
				continue;
			}
			if (distance < FADE_DISTANCE) {
				final double scaledDistance = (distance - MIN_DISTANCE)
						/ (FADE_DISTANCE - MIN_DISTANCE);
				final int proximityAlpha = (int) (5 + (250 * Math.max(scaledDistance, 0)));
				alpha = Math.min(alpha, proximityAlpha);
			}
			color = (color & 0x00FFFFFF) | (alpha << 24);
			final Vec3 screenPos = worldToScreen(position.xCoord, position.yCoord, position.zCoord,
					scaleFactor, partialTicks);
			if (screenPos == null || screenPos.zCoord < 0 || screenPos.zCoord >= 1.0003684d)
				continue;
			final double lastDistance = (double) object.get("lastdistance");
			final double interpolatedDistance = lastDistance
					+ (distance - lastDistance) * partialTicks;
			final float scaleValue = (float) (baseScale / interpolatedDistance);
			final float textWidth = mc.fontRendererObj.getStringWidth(health) * scaleValue;
			final float textHeight = mc.fontRendererObj.FONT_HEIGHT * scaleValue;
			final float screenX = (float) screenPos.xCoord - textWidth / 2.0f;
			final float screenY = (float) screenPos.yCoord - textHeight / 2.0f;
			GL11.glPushMatrix();
			GL11.glScalef(scaleValue, scaleValue, 1.0f);
			mc.fontRendererObj.drawString(health, screenX / scaleValue, screenY / scaleValue, color,
					true);
			GL11.glPopMatrix();
		}
	}

	@EventHandler
	private void onWorldJoin(final WorldLoadEvent event) {
		objects.clear();
		playerHealth.clear();
	}

	@Override
	protected void onEnable() {
		objects.clear();
		playerHealth.clear();
	}

	@Override
	protected void onDisable() {
		objects.clear();
		playerHealth.clear();
	}

	@Nullable private Vec3 worldToScreen(final double x, final double y, final double z,
			final int scaleFactor, final float partialTicks) {
		try {
			double wx = x - mc.getRenderManager().viewerPosX;
			double wy = y - mc.getRenderManager().viewerPosY;
			double wz = z - mc.getRenderManager().viewerPosZ;
			((EntityRendererAccessor) mc.entityRenderer).ou$setupCameraTransform(partialTicks, 0);
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODEL_VIEW);
			GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
			GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);
			if (GLU.gluProject((float) wx, (float) wy, (float) wz, MODEL_VIEW, PROJECTION, VIEWPORT,
					OBJECT_COORDS)) {
				final Vec3 vec = new Vec3(OBJECT_COORDS.get(0) / scaleFactor,
						(Display.getHeight() - OBJECT_COORDS.get(1)) / scaleFactor,
						OBJECT_COORDS.get(2));
				mc.entityRenderer.setupOverlayRendering();
				return vec;
			} else {
				mc.entityRenderer.setupOverlayRendering();
			}
		} catch (final Exception ignored) {
		}
		return null;
	}

	private static double round(final double value, final int places) {
		if (places < 0)
			return value;
		final double factor = Math.pow(10, places);
		return Math.round(value * factor) / factor;
	}

	private static @NotNull String formatDoubleStr(final double val) {
		return val == (long) val ? Long.toString((long) val) : Double.toString(val);
	}
}
