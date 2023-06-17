package dev.sweetberry.foxbox;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equippable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class TbhBlock extends HorizontalFacingBlock implements Equippable {
	public static final VoxelShape shape = VoxelShapes.union(
		// Legs
		createCuboidShape(5, 0, 5.5, 7, 4, 7.5),
		createCuboidShape(9, 0, 5.5, 11, 4, 7.5),
		createCuboidShape(5, 0, 12.5, 7, 4, 14.5),
		createCuboidShape(9, 0, 12.5, 11, 4, 14.5),

		// Body
		createCuboidShape(5, 4, 5, 11, 8, 15),

		// Head
		createCuboidShape(4.5, 6, 1, 11.5, 13, 8)
	);

	protected TbhBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return FoxBoxMod.rotate(shape, state.get(FACING));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public EquipmentSlot getPreferredSlot() {
		return EquipmentSlot.HEAD;
	}
}
