package dev.sweetberry.foxbox.content;

import dev.sweetberry.foxbox.FoxBoxMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FoxBoxSeatEntity extends Entity {

	public FoxBoxSeatEntity(EntityType<?> variant, World world) {
		super(variant, world);

		noClip = true;
	}

	@Override
	public double getMountedHeightOffset() {
		return 0;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	public void move(MovementType movementType, Vec3d movement) {
		if (movementType == MovementType.PISTON)
			return;
		super.move(movementType, movement);
	}

	@Override
	public void tick() {
		super.tick();

		if (!getWorld().isClient()) {
			var state = getWorld().getBlockState(getBlockPos());
			if (!(state.getBlock() == FoxBoxMod.foxbox_block) || !this.hasPassengers())
				this.discard();
		}
	}

	@Override
	protected void initDataTracker() {

	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {

	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {

	}
}
