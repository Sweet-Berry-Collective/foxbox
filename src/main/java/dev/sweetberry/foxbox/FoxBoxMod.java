package dev.sweetberry.foxbox;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class FoxBoxMod implements ModInitializer {
	public static final FoxBoxBlock foxbox_block = new FoxBoxBlock(
		QuiltBlockSettings.copyOf(Blocks.OAK_PLANKS)
	);
	public static final BlockItem foxbox_item = new BlockItem(
		foxbox_block,
		new QuiltItemSettings()
	);

	@Override
	public void onInitialize(ModContainer mod) {
		var id = new Identifier("foxbox:foxbox");

		Registry.register(Registries.BLOCK, id, foxbox_block);
		Registry.register(Registries.ITEM, id, foxbox_item);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register(entries -> entries.addAfter(Items.BEEHIVE, foxbox_item));
	}
}
