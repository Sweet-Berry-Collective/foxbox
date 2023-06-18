package dev.sweetberry.foxbox.client;

import dev.sweetberry.foxbox.FoxBoxMod;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;

public class FoxBoxClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ResourceLoader.registerBuiltinResourcePack(
			new Identifier("foxbox:realistic"),
			mod,
			ResourcePackActivationType.NORMAL,
			Text.translatable("foxbox.resourcepack.realistic")
		);

		ParticleFactoryRegistry.getInstance().register(FoxBoxMod.confetti, ConfettiParticle.Factory::new);

		EntityRendererRegistry.register(FoxBoxMod.foxbox_seat, FoxBoxSeatEntityRenderer::new);
	}
}
