package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.EidolonCapabilities;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WingsFlapPacket extends AbstractPacket {
    public static final Type<WingsFlapPacket> TYPE = new Type<>(Eidolon.prefix("wings_flap"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WingsFlapPacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            pkt -> pkt.uuid,
            WingsFlapPacket::new
    );

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

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        Level world = player.level();
        Player targetPlayer = world.getPlayerByUUID(uuid);
        if (targetPlayer != null) {
            var wings = targetPlayer.getCapability(EidolonCapabilities.WINGS_CAPABILITY);
            if (wings != null) {
                wings.tryFlapWings(player);
            }
            Networking.sendToNearbyClient(world, targetPlayer.getOnPos(), new FeatherEffectPacket(targetPlayer.getOnPos()));
        }

    }

    public @NotNull Type<WingsFlapPacket> type() {
        return TYPE;
    }
}
