package dev.sweetberry.foxbox;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equippable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
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

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player.shouldCancelInteraction())
			return ActionResult.PASS;

		if (world instanceof ServerWorld serverWorld) {
			FoxBoxNetworking.sendYippeeToClients(serverWorld, new FoxBoxNetworking.YippeePacket(player, pos));
		}

		return ActionResult.SUCCESS;
	}

	public static void yippee(World world, Vec3d vec, float volume) {
		var random = world.getRandom();
		int count = random.range(10, 25);
		for (int i = 0; i < count; i++) {
			var randx = random.nextFloat() * 0.25 - 0.125;
			var randz = random.nextFloat() * 0.25 - 0.125;
			world.addParticle(FoxBoxMod.confetti, vec.x + randx, vec.y, vec.z + randz, 0, 0, 0);
		}

		world.playSound(vec.x, vec.y, vec.z, FoxBoxMod.yippee, SoundCategory.BLOCKS, volume, 1, false);
	}
}
