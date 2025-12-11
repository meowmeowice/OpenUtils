package org.afterlike.openutils.platform.mixin.minecraft.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.event.impl.WindowClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {
	@Inject(method = "handleMouseClick", at = @At("HEAD"), cancellable = true)
	private void ou$handleMouseClick(Slot slot, int slotId, int clickedButton, int clickType,
			CallbackInfo ci) {
		GuiContainer self = (GuiContainer) (Object) this;
		WindowClickEvent event = new WindowClickEvent(self, slot, slotId, clickedButton, clickType);
		OpenUtils.get().getEventBus().post(event);
		if (event.isCancelled()) {
			ci.cancel();
		}
	}
}
