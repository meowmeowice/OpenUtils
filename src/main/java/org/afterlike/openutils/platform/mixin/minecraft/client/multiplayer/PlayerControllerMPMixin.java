package org.afterlike.openutils.platform.mixin.minecraft.client.multiplayer;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.event.impl.AttackEntityEvent;
import org.afterlike.openutils.module.impl.player.GhostLiquidFixModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin {
	@Inject(method = "attackEntity", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V"))
	private void ou$attackEntity(final EntityPlayer playerIn, final Entity targetEntity,
			final CallbackInfo ci) {
		OpenUtils.get().getEventBus().post(new AttackEntityEvent(playerIn, targetEntity));
	}

	@Inject(method = "onPlayerRightClick", at = @At("HEAD"))
	private void ou$onPlayerRightClick(EntityPlayerSP player, WorldClient worldIn,
			ItemStack heldStack, BlockPos hitPos, EnumFacing side, Vec3 hitVec,
			CallbackInfoReturnable<Boolean> cir) {
		if (!OpenUtils.get().getModuleHandler().isEnabled(GhostLiquidFixModule.class)) {
			return;
		}
		if (heldStack != null) {
			float hitX = (float) (hitVec.xCoord - hitPos.getX());
			float hitY = (float) (hitVec.yCoord - hitPos.getY());
			float hitZ = (float) (hitVec.zCoord - hitPos.getZ());
			C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(hitPos,
					side.getIndex(), player.inventory.getCurrentItem(), hitX, hitY, hitZ);
			if (heldStack.getItem() == Items.lava_bucket) {
				BlockPos targetPos = new BlockPos(packet.getPosition().getX(),
						packet.getPosition().getY(), packet.getPosition().getZ())
						.offset(EnumFacing.getFront(packet.getPlacedBlockDirection()));
				worldIn.setBlockState(targetPos, Blocks.flowing_lava.getDefaultState());
			} else if (heldStack.getItem() == Items.water_bucket) {
				BlockPos targetPos = new BlockPos(packet.getPosition().getX(),
						packet.getPosition().getY(), packet.getPosition().getZ())
						.offset(EnumFacing.getFront(packet.getPlacedBlockDirection()));
				worldIn.setBlockState(targetPos, Blocks.flowing_water.getDefaultState());
			}
		}
	}
}
