package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WingsDashPacket extends AbstractPacket {
    public static final Type<WingsDashPacket> TYPE = new Type<>(Eidolon.prefix("wings_dash"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WingsDashPacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            pkt -> pkt.uuid,
            WingsDashPacket::new
    );

    final UUID uuid;

    public WingsDashPacket(Player player) {
        this.uuid = player.getUUID();
    }

    public WingsDashPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public static void encode(WingsDashPacket object, FriendlyByteBuf buffer) {
        buffer.writeUUID(object.uuid);
    }

    public static WingsDashPacket decode(FriendlyByteBuf buffer) {
        return new WingsDashPacket(buffer.readUUID());
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (player.getUUID().equals(this.uuid)) {
            var wing = player.getCapability(EidolonCapabilities.WINGS_CAPABILITY);
            if (wing != null) {
                wing.tryDash(player);
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
