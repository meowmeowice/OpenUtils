package org.afterlike.openutils.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.module.impl.client.DebugModule;
import org.jetbrains.annotations.NotNull;

public class ClientUtil {
	private static final @NotNull Minecraft mc = Minecraft.getMinecraft();
	private static final @NotNull String prefix = "§8§l» §r";
	public static void sendMessage(@NotNull final String message) {
		if (notNull()) {
			mc.thePlayer.addChatMessage(
					new ChatComponentText(TextUtil.replaceColorCodes(prefix + message)));
		}
	}

	public static void sendChatComponent(@NotNull final ChatComponentText message) {
		if (notNull()) {
			mc.thePlayer.addChatMessage(message);
		}
	}

	public static void sendDebugMessage(@NotNull final String message) {
		if (notNull() && OpenUtils.get().getModuleHandler().isEnabled(DebugModule.class)) {
			sendMessage("&c[DEBUG] &r" + message);
		}
	}

	public static void sendMessageAsPlayer(@NotNull final String message) {
		if (notNull()) {
			mc.thePlayer.sendChatMessage(message);
		}
	}

	public static boolean notNull() {
		return mc.thePlayer != null && mc.theWorld != null;
	}

	public static @NotNull String getPrefix() {
		return prefix;
	}
}
