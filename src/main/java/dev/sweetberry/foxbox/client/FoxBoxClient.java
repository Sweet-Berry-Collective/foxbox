package dev.sweetberry.foxbox.client;

import com.mojang.blaze3d.platform.InputUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.sweetberry.foxbox.*;
import dev.sweetberry.foxbox.content.block.TbhBlock;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBind;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;

public class FoxBoxClient implements ClientModInitializer, ClientCommandRegistrationCallback {
	public static final KeyBind yippee_key = new KeyBind(
		"key.foxbox.yippee",
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_Y,
		"key.categories.gameplay"
	);
	public static boolean was_yippee_pressed = false;

	@Override
	public void onInitializeClient(ModContainer mod) {
		KeyBindingHelper.registerKeyBinding(yippee_key);

		ClientTickEvents.START.register(client -> {
			var was_pressed = was_yippee_pressed;
			was_yippee_pressed = yippee_key.isPressed();
			if (was_pressed)
				return;
			if (!was_yippee_pressed)
				return;
			if (client.world == null)
				return;
			if (client.player == null)
				return;
			var buf = PacketByteBufs.create();
			new FoxBoxNetworking.YippeePacket(client.player, client.player.getEyePos()).write(buf);
			ClientPlayNetworking.send(FoxBoxNetworking.yippee_id, buf);
		});

		ResourceLoader.registerBuiltinResourcePack(
			new Identifier("foxbox:realistic"),
			mod,
			ResourcePackActivationType.NORMAL,
			Text.translatable("foxbox.resourcepack.realistic")
		);

		ParticleFactoryRegistry.getInstance().register(FoxBoxMod.confetti, ConfettiParticle.Factory::new);

		EntityRendererRegistry.register(FoxBoxMod.foxbox_seat, FoxBoxSeatEntityRenderer::new);
		ClientPlayNetworking.registerGlobalReceiver(FoxBoxNetworking.yippee_id, ((client, handler, buf, responseSender) -> {
			var packet = FoxBoxNetworking.YippeePacket.read(buf);
			client.execute(() -> {
				if (client.world == null)
					return;
				TbhBlock.yippee(client.world, packet.pos, FoxBoxConfig.tbh_volume(packet.player != client.player.getUuid()));
			});
		}));

		ClientCommandRegistrationCallback.EVENT.register(this);
	}

	@Override
	public void registerCommands(CommandDispatcher<QuiltClientCommandSource> dispatcher, CommandBuildContext buildContext, CommandManager.RegistrationEnvironment environment) {
		var tbh_local_volume = ClientCommandManager.literal("tbh:local").then(ClientCommandManager.argument("volume", FloatArgumentType.floatArg(0, 1)).executes(ctx -> setTbhVolume(ctx, true)));
		var tbh_global_volume = ClientCommandManager.literal("tbh:global").then(ClientCommandManager.argument("volume", FloatArgumentType.floatArg(0, 1)).executes(ctx -> setTbhVolume(ctx, false)));

		dispatcher.register(ClientCommandManager.literal("foxbox_client").then(tbh_global_volume).then(tbh_local_volume));
	}

	public static int setTbhVolume(CommandContext<QuiltClientCommandSource> context, boolean local) {
		float volume = context.getArgument("volume", Float.class);
		getVolumeSetting(local).setValue(volume, true);
		return 1;
	}

	private static TrackedValue<Float> getVolumeSetting(boolean local) {
		var tbh = FoxBoxConfig.instance.tbh;
		return local ? tbh.tbh_local_volume : tbh.tbh_global_volume;
	}
}
