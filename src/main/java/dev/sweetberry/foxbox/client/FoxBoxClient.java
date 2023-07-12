package dev.sweetberry.foxbox.client;

import com.mojang.blaze3d.platform.InputUtil;
import dev.sweetberry.foxbox.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBind;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;

public class FoxBoxClient implements ClientModInitializer {
	public static final KeyBind yippee_key = new KeyBind(
		"key.foxbox.yippee",
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_Y,
		"key.categories.gameplay"
	);
	@Override
	public void onInitializeClient(ModContainer mod) {
		KeyBindingHelper.registerKeyBinding(yippee_key);

		ClientTickEvents.START.register(client -> {
			if (!yippee_key.wasPressed())
				return;
			if (client.world == null)
				return;
			if (client.player == null)
				return;
			var main = client.player.getStackInHand(Hand.MAIN_HAND).isOf(FoxBoxMod.tbh_item);
			var off = client.player.getStackInHand(Hand.OFF_HAND).isOf(FoxBoxMod.tbh_item);
			var head = client.player.getEquippedStack(EquipmentSlot.HEAD).isOf(FoxBoxMod.tbh_item);
			if (FoxBoxConfig.instance.tbh.yippee_needs_tbh && !main && !off && !head)
				return;
			TbhBlock.yippee(client.world, client.player.getEyePos(), client.player.getBlockPos(), FoxBoxConfig.tbh_volume(false));
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
			if (packet.player == client.player.getUuid())
				return;
			client.execute(() -> {
				if (client.world == null)
					return;
				TbhBlock.yippee(client.world, packet.pos, new BlockPos((int)packet.pos.x, (int)packet.pos.y, (int)packet.pos.z), FoxBoxConfig.tbh_volume(true));
			});
		}));
	}
}
