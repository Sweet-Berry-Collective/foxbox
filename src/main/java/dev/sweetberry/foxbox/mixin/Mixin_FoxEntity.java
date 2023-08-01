package dev.sweetberry.foxbox.mixin;

import dev.sweetberry.foxbox.content.SitInBoxGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxEntity.class)
public class Mixin_FoxEntity extends MobEntity {
	protected Mixin_FoxEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
		throw new IllegalAccessError("Shouldn't be instantiated.");
	}

	@Inject(method = "initGoals", at = @At("RETURN"))
	private void foxbox$initGoals(CallbackInfo ci) {
		var followup = goalSelector.getGoals().stream().filter(goal -> goal.getGoal() instanceof FoxEntity.SitDownAndLookAroundGoal).findFirst().get();
		goalSelector.add(15, new SitInBoxGoal((FoxEntity) (Object) this, followup, 1.2F, 15));
	}
}
