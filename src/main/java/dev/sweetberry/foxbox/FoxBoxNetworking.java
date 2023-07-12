package dev.sweetberry.foxbox;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.UUID;

public class FoxBoxNetworking {
	public static final Identifier yippee_id = new Identifier("foxbox", "yippee");

	public static void sendYippeeToClients(ServerWorld world, YippeePacket packet) {
		world.getPlayers().forEach(player -> {
			if (player.getUuid() == packet.player)
				return;
			var buf = PacketByteBufs.create();
			packet.write(buf);
			ServerPlayNetworking.send(player, yippee_id, buf);
		});
	}

	public static class YippeePacket {
		public UUID player;
		public Vec3d pos;

		public YippeePacket(UUID player, Vec3d pos) {
			this.player = player;
			this.pos = pos;
		}

		public YippeePacket(PlayerEntity player, Vec3d pos) {
			this(player.getUuid(), pos);
		}

		public YippeePacket(PlayerEntity player, BlockPos pos, boolean boxed) {
			this(player, pos.ofCenter().add(0, boxed ? 0.5 : 0.25, 0));
		}

		public YippeePacket(PlayerEntity player, BlockPos pos) {
			this(player, pos, false);

		}

		public void write(PacketByteBuf buf) {
			buf.writeUuid(player);
			buf.writeDouble(pos.x);
			buf.writeDouble(pos.y);
			buf.writeDouble(pos.z);
		}

		public static YippeePacket read(PacketByteBuf buf) {
			UUID player = buf.readUuid();
			Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
			return new YippeePacket(player, pos);
		}
	}
}
