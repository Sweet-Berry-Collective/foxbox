package dev.sweetberry.foxbox;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class SitInBoxGoal extends MoveToTargetPosGoal {
	public static final int wait_time = 40;
	public final FoxEntity fox;
	public final Goal followup;
	public int timer = 0;

	public SitInBoxGoal(FoxEntity fox, Goal followup, double speed, int range) {
		super(fox, speed, range, 1);
		this.fox = fox;
		this.followup = followup;
	}

	@Override
	public double getDesiredSquaredDistanceToTarget() {
		return 0.25;
	}

	@Override
	public boolean shouldResetPath() {
		return this.tryingTime % 100 == 0;
	}

	@Override
	protected boolean isTargetPos(WorldView world, BlockPos pos) {
		var state = world.getBlockState(pos);
		return state.isOf(FoxBoxMod.foxbox_block) && !state.get(FoxBoxBlock.tbh);
	}

	@Override
	public void tick() {
		if (hasReached()) {
			fox.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speed);
			timer++;
		} else if (fox.getRandom().nextFloat() < 0.05F) {
			fox.playSound(SoundEvents.ENTITY_FOX_SNIFF, 1.0F, 1.0F);
		}

		super.tick();
	}

	@Override
	public void start() {
		timer = 0;
		fox.setSitting(false);
		super.start();
	}

	@Override
	public void stop() {
		if (followup.canStart())
			followup.start();
		super.stop();
	}

	@Override
	public boolean canStart() {
		return !fox.isSleeping() && super.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return super.shouldContinue() && timer < wait_time;
	}
}
