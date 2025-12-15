package org.afterlike.openutils.platform.mixin.minecraft.client.multiplayer;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerControllerMP.class)
public interface PlayerControllerMPAccessor {
	@Accessor("blockHitDelay")
	void ou$setBlockHitDelay(int delay);

	@Accessor("blockHitDelay")
	int ou$getBlockHitDelay();
}
