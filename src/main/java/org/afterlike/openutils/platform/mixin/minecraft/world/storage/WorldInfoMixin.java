package org.afterlike.openutils.platform.mixin.minecraft.world.storage;

import net.minecraft.world.storage.WorldInfo;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.module.impl.world.TimeChangerModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldInfo.class)
public class WorldInfoMixin {
	@Shadow
	private long worldTime;
	@Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true)
	private void ou$getWorldTime(final CallbackInfoReturnable<Long> cir) {
		if (!OpenUtils.get().getModuleHandler().isEnabled(TimeChangerModule.class))
			return;
		final TimeChangerModule module = OpenUtils.get().getModuleHandler()
				.getModuleClass(TimeChangerModule.class);
		cir.setReturnValue(module.timeToTicks());
	}
}
