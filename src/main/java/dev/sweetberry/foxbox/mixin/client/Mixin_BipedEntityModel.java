/*
 * Modified version of https://github.com/0xf24/FOX-PLUSHIES/blob/1.19/src/main/java/im/f24/plushies/mixin/BipedEntityModelMixin.java
 * Follows licence of repository. (https://github.com/0xf24/FOX-PLUSHIES/blob/1.19/LICENSE)
 * */

package dev.sweetberry.foxbox.mixin.client;

import dev.sweetberry.foxbox.content.item.Plushie;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class Mixin_BipedEntityModel {
	@Shadow
	public @Final ModelPart rightArm;

	@Shadow
	public @Final ModelPart leftArm;

	@Inject(
		method = {"positionRightArm", "positionLeftArm"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/model/CrossbowPosing;hold(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Z)V",
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
	public void foxbox$poseArmsHold(LivingEntity entity, CallbackInfo ci) {
		if (!(entity.getMainHandStack().getItem() instanceof Plushie) && !(entity.getOffHandStack().getItem() instanceof Plushie))
			return;
		foxbox$posArms(1.0f);
		ci.cancel();
	}

	@Inject(
		method = {"positionRightArm", "positionLeftArm"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/model/CrossbowPosing;charge(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/entity/LivingEntity;Z)V",
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
	public void foxbox$poseArmsChange(LivingEntity entity, CallbackInfo ci) {
		if (!(entity.getMainHandStack().getItem() instanceof Plushie) && !(entity.getOffHandStack().getItem() instanceof Plushie))
			return;
		foxbox$posArms(1.2f);
		ci.cancel();
	}

	private void foxbox$posArms(float mult) {
		this.rightArm.pitch = -0.95F * mult;
		this.rightArm.yaw = (float) (-Math.PI / 8);
		this.leftArm.pitch = -0.90F * mult;
		this.leftArm.yaw = (float) (Math.PI / 8);
	}
}
