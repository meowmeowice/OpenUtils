package org.afterlike.openutils.platform.mixin.minecraft.gui;

import net.minecraft.client.gui.FontRenderer;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.module.impl.render.AntiShuffleModule;
import org.afterlike.openutils.module.impl.render.NameHiderModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FontRenderer.class)
public class FontRendererMixin {
	@ModifyVariable(method = "renderString", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
	private String ou$renderString(String string) {
		if (string == null) {
			return null;
		}
		if (OpenUtils.get().getModuleHandler().isEnabled(AntiShuffleModule.class)
				&& string.contains("§k")) {
			string = string.replace("§k", "");
		}
		if (OpenUtils.get().getModuleHandler().isEnabled(NameHiderModule.class)) {
			string = OpenUtils.get().getModuleHandler().getModuleClass(NameHiderModule.class)
					.replaceName(string);
		}
		return string;
	}

	@ModifyVariable(method = "getStringWidth", at = @At(value = "HEAD"), ordinal = 0,
			argsOnly = true)
	private String ou$getStringWidth(String string) {
		if (string == null) {
			return null;
		}
		if (OpenUtils.get().getModuleHandler().isEnabled(AntiShuffleModule.class)
				&& string.contains("§k")) {
			string = string.replace("§k", "");
		}
		if (OpenUtils.get().getModuleHandler().isEnabled(NameHiderModule.class)) {
			string = OpenUtils.get().getModuleHandler().getModuleClass(NameHiderModule.class)
					.replaceName(string);
		}
		return string;
	}
}
