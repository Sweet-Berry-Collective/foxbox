package dev.sweetberry.foxbox;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class FoxBoxBlock extends HorizontalFacingBlock {
	public static final VoxelShape OUTLINE = VoxelShapes.union(
		createCuboidShape(0.1, 0, 0.1, 15.9,  0.1, 15.9),
		createCuboidShape(0, 0, 0, 16,  16, 0.1),
		createCuboidShape(0, 0, 0, 0.1,  16, 16),
		createCuboidShape(0, 0, 15.9, 16,  16, 16),
		createCuboidShape(15.9, 0, 0, 16,  16, 16)
	);
	public static final VoxelShape COLLISION = VoxelShapes.union(
		createCuboidShape(0.1, 0, 0.1, 15.9,  6, 15.9),
		createCuboidShape(0, 0, 0, 16,  16, 0.1),
		createCuboidShape(0, 0, 0, 0.1,  16, 16),
		createCuboidShape(0, 0, 15.9, 16,  16, 16),
		createCuboidShape(15.9, 0, 0, 16,  16, 16)
	);

	public static final BooleanProperty LEFT = BooleanProperty.of("left");

	public static final BooleanProperty RIGHT = BooleanProperty.of("right");

	public FoxBoxBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return COLLISION;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, LEFT, RIGHT);
	}

	public BlockState getSideState(BlockState state, BlockPos pos, Direction dir, WorldAccess world) {
		var left = dir.rotateYCounterclockwise();
		var right = dir.rotateYClockwise();

		return state.with(LEFT, world.isAir(pos.offset(left))).with(RIGHT, world.isAir(pos.offset(right)));
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		var pos = ctx.getBlockPos();
		var world = ctx.getWorld();
		var dir = ctx.getPlayerFacing();

		return getSideState(getDefaultState(), pos, dir, world).with(FACING, dir);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return getSideState(state, pos, state.get(FACING), world);
	}
}
