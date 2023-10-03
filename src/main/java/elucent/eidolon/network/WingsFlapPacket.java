package elucent.eidolon.network;

import elucent.eidolon.capability.IPlayerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class WingsFlapPacket {
    final UUID uuid;

    public WingsFlapPacket(Player player) {
        this.uuid = player.getUUID();
    }

    public WingsFlapPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public static void encode(WingsFlapPacket object, FriendlyByteBuf buffer) {
        buffer.writeUUID(object.uuid);
    }

    public static WingsFlapPacket decode(FriendlyByteBuf buffer) {
        return new WingsFlapPacket(buffer.readUUID());
    }

    public static void consume(WingsFlapPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER;

            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;
            Level world = sender.level;
            Player player = world.getPlayerByUUID(packet.uuid);
            if (player != null) {
                player.getCapability(IPlayerData.INSTANCE).ifPresent((d) -> d.tryFlapWings(player));
                var pos = player.getOnPos();
                Networking.sendToTracking(world, pos, new FeatherEffectPacket(pos));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
