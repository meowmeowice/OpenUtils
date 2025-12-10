package org.afterlike.openutils.platform.mixin.minecraft.entity;

import java.util.Objects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.impl.render.AntiDebuffModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin {
	@Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At("HEAD"),
			cancellable = true)
	private void isPotionActive(final Potion potionIn, final CallbackInfoReturnable<Boolean> cir) {
		if (!OpenUtils.get().getModuleHandler().isEnabled(AntiDebuffModule.class))
			return;
		final AntiDebuffModule module = OpenUtils.get().getModuleHandler()
				.getModuleClass(AntiDebuffModule.class);
		final BooleanSetting nauseaSetting = module.getSetting("Remove nausea",
				BooleanSetting.class);
		final BooleanSetting blindnessSetting = module.getSetting("Remove blindness",
				BooleanSetting.class);
		final boolean removeNausea = Objects.requireNonNull(nauseaSetting).getValue();
		final boolean removeBlindness = Objects.requireNonNull(blindnessSetting).getValue();
		if ((removeNausea && potionIn == Potion.confusion)
				|| (removeBlindness && potionIn == Potion.blindness)) {
			cir.setReturnValue(false);
		}
	}
}
