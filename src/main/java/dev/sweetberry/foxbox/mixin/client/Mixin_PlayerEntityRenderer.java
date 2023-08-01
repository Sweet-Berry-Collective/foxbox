/*
 * Modified version of https://github.com/0xf24/FOX-PLUSHIES/blob/1.19/src/main/java/im/f24/plushies/mixin/PlayerEntityRendererMixin.java
 * Follows licence of repository. (https://github.com/0xf24/FOX-PLUSHIES/blob/1.19/LICENSE)
 * */

package dev.sweetberry.foxbox.mixin.client;

import dev.sweetberry.foxbox.content.item.Plushie;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class Mixin_PlayerEntityRenderer {
	@Inject(
		method = "getArmPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;",
		at = @At(value = "TAIL"),
		cancellable = true
	)
	private static void foxbox$getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
		if (player.handSwinging || !(player.getStackInHand(hand).getItem() instanceof Plushie))
			return;
		cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
		cir.cancel();
	}
}
