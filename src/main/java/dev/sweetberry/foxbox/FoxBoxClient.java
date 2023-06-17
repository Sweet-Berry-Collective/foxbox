package dev.sweetberry.foxbox;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;

public class FoxBoxClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ResourceLoader.registerBuiltinResourcePack(new Identifier("foxbox:realistic"), mod, ResourcePackActivationType.NORMAL, Text.translatable("foxbox.resourcepack.realistic"));
	}
}
