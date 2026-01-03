package org.afterlike.openutils.module.impl.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.AttackEntityEvent;
import org.afterlike.openutils.event.impl.ReceivePacketEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.util.client.ClientUtil;
import org.jetbrains.annotations.NotNull;

public class ActionSoundsModule extends Module {
	private final @NotNull BooleanSetting blocked;
	private final @NotNull BooleanSetting crit;
	public ActionSoundsModule() {
		super("Action Sounds", ModuleCategory.PLAYER);
		blocked = this.registerSetting(new BooleanSetting("Blocked damage", true));
		crit = this.registerSetting(new BooleanSetting("Critical hit", true));
	}

	@EventHandler
	private void onPacketReceived(final @NotNull ReceivePacketEvent event) {
		if (!ClientUtil.notNull())
			return;
		if (!blocked.getValue())
			return;
		if (event.getPacket() instanceof S19PacketEntityStatus && blocked.getValue()) {
			S19PacketEntityStatus packet = (S19PacketEntityStatus) event.getPacket();
			if (packet.getOpCode() != 2)
				return;
			if (packet.getEntity(mc.theWorld) != mc.thePlayer)
				return;
			if (!mc.thePlayer.isBlocking())
				return;
			mc.thePlayer.playSound("random.anvil_land", 0.7F, 1.8F);
		}
	}

	@EventHandler
	public void onAttackEntity(final @NotNull AttackEntityEvent event) {
		if (!ClientUtil.notNull())
			return;
		if (!crit.getValue())
			return;
		if (event.getTarget() instanceof EntityPlayer && event.getPlayerIn() == mc.thePlayer) {
			if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance > 0.0F
					&& !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()
					&& !mc.thePlayer.isRiding()) {
				mc.thePlayer.playSound("openutils:crit", 0.7F, 1.0F);
			}
		}
	}
}
