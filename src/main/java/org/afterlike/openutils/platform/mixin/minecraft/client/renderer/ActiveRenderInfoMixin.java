package org.afterlike.openutils.platform.mixin.minecraft.client.renderer;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.module.impl.render.FreeLookModule;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ActiveRenderInfo.class)
public class ActiveRenderInfoMixin {
	@Redirect(method = "updateRenderInfo",
			at = @At(value = "FIELD",
					target = "Lnet/minecraft/entity/player/EntityPlayer;rotationPitch:F",
					opcode = Opcodes.GETFIELD))
	private static float modifyPitch(final EntityPlayer instance) {
		return OpenUtils.get().getModuleHandler().isEnabled(FreeLookModule.class)
				? OpenUtils.get().getModuleHandler().getModuleClass(FreeLookModule.class)
						.getCameraPitch()
				: instance.rotationPitch;
	}

	@Redirect(method = "updateRenderInfo",
			at = @At(value = "FIELD",
					target = "Lnet/minecraft/entity/player/EntityPlayer;rotationYaw:F",
					opcode = Opcodes.GETFIELD))
	private static float modifyYaw(final EntityPlayer instance) {
		return OpenUtils.get().getModuleHandler().isEnabled(FreeLookModule.class)
				? OpenUtils.get().getModuleHandler().getModuleClass(FreeLookModule.class)
						.getCameraYaw()
				: instance.rotationYaw;
	}
}
