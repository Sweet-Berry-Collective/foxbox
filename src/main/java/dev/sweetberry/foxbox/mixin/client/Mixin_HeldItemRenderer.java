/*
 * Modified version of https://github.com/0xf24/FOX-PLUSHIES/blob/1.19/src/main/java/im/f24/plushies/mixin/HeldItemRendererMixin.java
 * Follows licence of repository. (https://github.com/0xf24/FOX-PLUSHIES/blob/1.19/LICENSE)
 * */

package dev.sweetberry.foxbox.mixin.client;

import dev.sweetberry.foxbox.content.item.Plushie;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class Mixin_HeldItemRenderer {
	@Inject(
		method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			ordinal = 1
		)
	)
	private void foxbox$renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (item.getItem() instanceof Plushie && player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
			Arm arm = (hand == Hand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();

			int i = arm == Arm.RIGHT ? 1 : -1;

			matrices.translate(
				(float)i * (0.56F - .15f),
				-0.52F + 0.11F, //y
				-0.72F + .2F
			);
		}
	}
}
