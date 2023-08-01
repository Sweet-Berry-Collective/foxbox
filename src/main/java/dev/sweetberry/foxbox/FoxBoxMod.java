package dev.sweetberry.foxbox;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.sweetberry.foxbox.content.block.FoxBoxBlock;
import dev.sweetberry.foxbox.content.FoxBoxSeatEntity;
import dev.sweetberry.foxbox.content.block.TbhBlock;
import dev.sweetberry.foxbox.content.item.PlushieBlockItem;
import dev.sweetberry.foxbox.content.item.PlushieItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.ArrayList;

public class FoxBoxMod implements ModInitializer, CommandRegistrationCallback {
	public static final FoxBoxBlock foxbox_block = new FoxBoxBlock(
		QuiltBlockSettings.copyOf(Blocks.OAK_PLANKS).breakInstantly()
	);
	public static final BlockItem foxbox_item = new BlockItem(
		foxbox_block,
		new QuiltItemSettings()
	);
	public static final EntityType<FoxBoxSeatEntity> foxbox_seat = QuiltEntityTypeBuilder
		.create(SpawnGroup.MISC, FoxBoxSeatEntity::new)
		.disableSaving()
		.disableSummon()
		.maxChunkTrackingRange(10)
		.build();

	public static final TbhBlock tbh_block = new TbhBlock(
		QuiltBlockSettings.copyOf(Blocks.WHITE_WOOL).breakInstantly()
	);

	public static final BlockItem tbh_item = new PlushieBlockItem(
		tbh_block,
		new QuiltItemSettings()
	);

	public static final Item fox_plushie = new PlushieItem(
		new QuiltItemSettings()
	);
	public static final Item snow_fox_plushie = new PlushieItem(
		new QuiltItemSettings()
	);

	public static final DefaultParticleType confetti = FabricParticleTypes.simple();
	public static final Identifier yippee_id = new Identifier("foxbox:yippee");
	public static final SoundEvent yippee = SoundEvent.createVariableRangeEvent(yippee_id);

	@Override
	public void onInitialize(ModContainer mod) {
		FoxBoxConfig.poke();

		Identifier id;

		id = new Identifier("foxbox:foxbox");

		Registry.register(Registries.BLOCK, id, foxbox_block);
		Registry.register(Registries.ITEM, id, foxbox_item);

		Registry.register(Registries.ENTITY_TYPE, new Identifier("foxbox:foxbox_seat"), foxbox_seat);

		id = new Identifier("foxbox:tbh");

		Registry.register(Registries.BLOCK, id, tbh_block);
		Registry.register(Registries.ITEM, id, tbh_item);

		Registry.register(Registries.ITEM, new Identifier("foxbox:fox_plushie"), fox_plushie);
		Registry.register(Registries.ITEM, new Identifier("foxbox:snow_fox_plushie"), snow_fox_plushie);

		Registry.register(
			Registries.PARTICLE_TYPE,
			new Identifier("foxbox", "confetti"),
			confetti
		);

		Registry.register(Registries.SOUND_EVENT, yippee_id, yippee);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register(entries -> entries.addAfter(Items.BEEHIVE, foxbox_item, tbh_item));
//		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS_AND_UTILITIES).register(entries -> entries.addAfter());

		ServerPlayNetworking.registerGlobalReceiver(FoxBoxNetworking.yippee_id, ((server, player, handler, buf, responseSender) -> {
			var packet = FoxBoxNetworking.YippeePacket.read(buf);
			var main = player.getStackInHand(Hand.MAIN_HAND).isOf(FoxBoxMod.tbh_item);
			var off = player.getStackInHand(Hand.OFF_HAND).isOf(FoxBoxMod.tbh_item);
			var head = player.getEquippedStack(EquipmentSlot.HEAD).isOf(FoxBoxMod.tbh_item);
			if (FoxBoxConfig.instance.tbh.yippee_needs_tbh.value() && !main && !off && !head)
				return;
			server.execute(() -> FoxBoxNetworking.sendYippeeToClients(player.getServerWorld(), packet));
		}));

		CommandRegistrationCallback.EVENT.register(this);
	}

	public static VoxelShape rotate(VoxelShape shape, Direction dir) {
		var shapes = new ArrayList<VoxelShape>();

		shape.forEachBox((x1, y1, z1, x2, y2, z2) -> shapes.add(switch (dir) {
			case WEST -> VoxelShapes.cuboid(z1, y1, x1, z2, y2, x2);
			case SOUTH -> VoxelShapes.cuboid(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
			case EAST -> VoxelShapes.cuboid(1 - z2, y1, 1 - x2, 1 - z1, y2, 1 - x1);
			default -> VoxelShapes.cuboid(x1, y1, z1, x2, y2, z2);
		}));

		return VoxelShapes.union(VoxelShapes.empty(), shapes.toArray(new VoxelShape[]{}));
	}

	public static VoxelShape upwards(VoxelShape shape, float amount) {
		var shapes = new ArrayList<VoxelShape>();

		shape.forEachBox((x1, y1, z1, x2, y2, z2) -> shapes.add(VoxelShapes.cuboid(x1, y1+(amount/16), z1, x2, y2+(amount/16), z2)));

		return VoxelShapes.union(VoxelShapes.empty(), shapes.toArray(new VoxelShape[]{}));
	}

	public static VoxelShape forwards(VoxelShape shape, float amount) {
		var shapes = new ArrayList<VoxelShape>();

		shape.forEachBox((x1, y1, z1, x2, y2, z2) -> shapes.add(VoxelShapes.cuboid(x1, y1, z1-(amount/16), x2, y2, z2-(amount/16))));

		return VoxelShapes.union(VoxelShapes.empty(), shapes.toArray(new VoxelShape[]{}));
	}

	@Override
	public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandBuildContext buildContext, CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(CommandManager.literal("foxbox").then(CommandManager.literal("tbh:require").requires(it -> it.hasPermissionLevel(2)).then(CommandManager.argument("require", BoolArgumentType.bool()).executes(FoxBoxMod::tbhRequire))));
	}

	public static int tbhRequire(CommandContext<ServerCommandSource> ctx) {
		boolean require = ctx.getArgument("require", Boolean.class);

		FoxBoxConfig.instance.tbh.yippee_needs_tbh.setValue(require, true);

		return 1;
	}
}
