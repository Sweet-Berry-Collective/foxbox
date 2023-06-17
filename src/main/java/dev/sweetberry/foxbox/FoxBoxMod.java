package dev.sweetberry.foxbox;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.ArrayList;

public class FoxBoxMod implements ModInitializer {
	public static final FoxBoxBlock foxbox_block = new FoxBoxBlock(
		QuiltBlockSettings.copyOf(Blocks.OAK_PLANKS).breakInstantly()
	);
	public static final BlockItem foxbox_item = new BlockItem(
		foxbox_block,
		new QuiltItemSettings()
	);

	public static final TbhBlock tbh_block = new TbhBlock(
		QuiltBlockSettings.copyOf(Blocks.WHITE_WOOL).breakInstantly()
	);

	public static final BlockItem tbh_item = new BlockItem(
		tbh_block,
		new QuiltItemSettings()
	);

	@Override
	public void onInitialize(ModContainer mod) {
		var id = new Identifier("foxbox:foxbox");

		Registry.register(Registries.BLOCK, id, foxbox_block);
		Registry.register(Registries.ITEM, id, foxbox_item);

		id = new Identifier("foxbox:tbh");

		Registry.register(Registries.BLOCK, id, tbh_block);
		Registry.register(Registries.ITEM, id, tbh_item);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register(entries -> entries.addAfter(Items.BEEHIVE, foxbox_item, tbh_item));
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
}
