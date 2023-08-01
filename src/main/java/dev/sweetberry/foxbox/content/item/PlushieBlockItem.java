package dev.sweetberry.foxbox.content.item;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class PlushieBlockItem extends BlockItem implements Plushie {
	public PlushieBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return Plushie.super.getMaxUseTime(stack);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return Plushie.super.getUseAction(stack);
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		Plushie.super.usageTick(world, user, stack, remainingUseTicks);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return Plushie.super.use(world, user, hand);
	}
}
