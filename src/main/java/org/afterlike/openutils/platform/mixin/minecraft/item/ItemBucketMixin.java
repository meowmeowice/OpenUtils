package org.afterlike.openutils.platform.mixin.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import org.afterlike.openutils.OpenUtils;
import org.afterlike.openutils.module.impl.player.GhostLiquidFixModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemBucket.class)
public class ItemBucketMixin extends Item {
	@Shadow
	private Block isFull;
	@Redirect(method = "onItemRightClick", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/item/ItemBucket;tryPlaceContainedLiquid(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)Z"))
	private boolean ou$onItemRightClick(ItemBucket instance, World worldIn, BlockPos pos) {
		if (!OpenUtils.get().getModuleHandler().isEnabled(GhostLiquidFixModule.class)) {
			return instance.tryPlaceContainedLiquid(worldIn, pos);
		}
		if (this.isFull == Blocks.air) {
			return false;
		}
		Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
		boolean isSolid = !material.isSolid();
		if (!worldIn.isAirBlock(pos) && !isSolid) {
			return false;
		}
		if (worldIn.provider.doesWaterVaporize() && this.isFull == Blocks.flowing_water) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.fizz", 0.5f,
					2.6f + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8f);
			for (int l = 0; l < 8; ++l) {
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + Math.random(),
						y + Math.random(), z + Math.random(), 0.0, 0.0, 0.0);
			}
		} else {
			if (!worldIn.isRemote && isSolid && !material.isLiquid()) {
				worldIn.destroyBlock(pos, true);
			}
			if (!worldIn.isRemote) {
				worldIn.setBlockState(pos, this.isFull.getDefaultState(), 3);
			}
		}
		return true;
	}
}
