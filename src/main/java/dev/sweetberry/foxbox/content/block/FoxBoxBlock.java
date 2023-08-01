package dev.sweetberry.foxbox.content.block;

import dev.sweetberry.foxbox.FoxBoxConfig;
import dev.sweetberry.foxbox.FoxBoxMod;
import dev.sweetberry.foxbox.FoxBoxNetworking;
import dev.sweetberry.foxbox.content.FoxBoxSeatEntity;
import net.minecraft.block.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FoxBoxBlock extends HorizontalFacingBlock {
	public static final VoxelShape outline = VoxelShapes.union(
		createCuboidShape(0.1, 0, 0.1, 15.9,  0.1, 15.9),
		createCuboidShape(0, 0, 0, 16,  16, 0.1),
		createCuboidShape(0, 0, 0, 0.1,  16, 16),
		createCuboidShape(0, 0, 15.9, 16,  16, 16),
		createCuboidShape(15.9, 0, 0, 16,  16, 16)
	);
	public static final VoxelShape collision = VoxelShapes.union(
		createCuboidShape(0.1, 0, 0.1, 15.9,  6, 15.9),
		createCuboidShape(0, 0, 0, 16,  16, 0.1),
		createCuboidShape(0, 0, 0, 0.1,  16, 16),
		createCuboidShape(0, 0, 15.9, 16,  16, 16),
		createCuboidShape(15.9, 0, 0, 16,  16, 16)
	);
	public static final VoxelShape tbh_collision = FoxBoxMod.forwards(FoxBoxMod.upwards(TbhBlock.shape, 11), 3);

	public static final BooleanProperty left = BooleanProperty.of("left");
	public static final BooleanProperty right = BooleanProperty.of("right");
	public static final BooleanProperty tbh = BooleanProperty.of("tbh");

	public FoxBoxBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(tbh, false));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (state.get(tbh))
			return VoxelShapes.union(outline, FoxBoxMod.rotate(tbh_collision, state.get(FACING).getOpposite()));
		return outline;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (state.get(tbh))
			return VoxelShapes.union(collision, FoxBoxMod.rotate(tbh_collision, state.get(FACING).getOpposite()));
		return collision;
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return collision;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, left, right, tbh);
	}

	public BlockState getSideState(BlockState state, BlockPos pos, Direction dir, WorldAccess world) {
		var left_dir = dir.rotateYCounterclockwise();
		var left_pos = pos.offset(left_dir);
		var right_dir = dir.rotateYClockwise();
		var right_pos = pos.offset(right_dir);
		var left = world.getBlockState(left_pos).isSideSolidFullSquare(world, left_pos, right_dir);
		var right = world.getBlockState(right_pos).isSideSolidFullSquare(world, right_pos, left_dir);

		return state.with(FoxBoxBlock.left, !left).with(FoxBoxBlock.right, !right);
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

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		var stacks = new ArrayList<>(super.getDroppedStacks(state, builder));

		if (state.get(tbh))
			stacks.add(FoxBoxMod.tbh_item.getDefaultStack());

		return stacks;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (state.get(tbh)) return interactWithTbh(state, world, pos, player, hand, hit);
		return interact(state, world, pos, player, hand, hit);
	}

	public ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		var stack = player.getStackInHand(hand);

		for (var sitting_player : world.getNonSpectatingEntities(PlayerEntity.class, new Box(pos)))
			if (sitting_player.hasVehicle())
				if (sitting_player.getVehicle() instanceof FoxBoxSeatEntity)
					return ActionResult.PASS;

		if (stack.isEmpty() && !state.get(tbh)) {
			var seat = FoxBoxMod.foxbox_seat.create(world);
			if (seat == null)
				return ActionResult.PASS;
			seat.setPosition(pos.getX() + .5f, pos.getY() + (6f/16f), pos.getZ() + .5f);
			world.spawnEntity(seat);
			player.startRiding(seat, true);

			return ActionResult.SUCCESS;
		}

		if (!stack.isOf(FoxBoxMod.tbh_item)) return ActionResult.PASS;

		if (!player.canModifyBlocks()) return ActionResult.PASS;

		if (!player.isCreative()) {
			stack.decrement(1);
			player.setStackInHand(hand, stack);
		}

		world.setBlockState(pos, state.with(tbh, true));

		world.playSound(pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

		return ActionResult.SUCCESS;
	}

	public ActionResult interactWithTbh(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!player.getStackInHand(hand).isEmpty() || !player.isSneaking()) {
			if (world instanceof ServerWorld serverWorld) {
				FoxBoxNetworking.sendYippeeToClients(serverWorld, new FoxBoxNetworking.YippeePacket(player, pos, true));
				return ActionResult.SUCCESS;
			}

			TbhBlock.yippee(world, pos.ofCenter().add(0, 1, 0), FoxBoxConfig.tbh_volume(false));
			return ActionResult.SUCCESS;
		}

		if (!player.canModifyBlocks()) return ActionResult.PASS;

		world.setBlockState(pos, state.with(tbh, false));

		if (!player.isCreative()) {
			var realPos = pos.ofCenter().add(0, 0.5, 0);
			var item = new ItemEntity(world, realPos.x, realPos.y, realPos.z, FoxBoxMod.tbh_item.getDefaultStack());
			world.spawnEntity(item);
		}

		world.playSound(pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
		return ActionResult.SUCCESS;
	}
}
