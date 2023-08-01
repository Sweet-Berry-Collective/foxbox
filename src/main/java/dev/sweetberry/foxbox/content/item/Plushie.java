/*
 * Modified version of https://github.com/0xf24/FOX-PLUSHIES/blob/1.19/src/main/java/im/f24/plushies/FoxPlushieItem.java
 * Follows licence of repository. (https://github.com/0xf24/FOX-PLUSHIES/blob/1.19/LICENSE)
 * */

package dev.sweetberry.foxbox.content.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;

public interface Plushie {
	default int getMaxUseTime(ItemStack stack) {
		return 600;
	}

	default UseAction getUseAction(ItemStack stack) {
		return UseAction.CROSSBOW;
	}

	default void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (world.isClient && (remainingUseTicks % 4) == 0) {

			float yaw = (float) Math.toRadians(user.bodyYaw + 90);

			Vec2f rot = new Vec2f((float) Math.cos(yaw), (float) Math.sin(yaw)).multiply(0.4f);

			world.addParticle(
				ParticleTypes.HEART,
				user.getX() + rot.x + ((world.random.nextDouble() * 2) - 1) * 0.25,
				user.getY() + 2,
				user.getZ() + rot.y + ((world.random.nextDouble() * 2) - 1) * 0.25,
				0.0,
				0.1 + (world.random.nextDouble() * 0.5),
				0.0
			);
		}
	}

	default TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return ItemUsage.consumeHeldItem(world, user, hand);
	}
}
