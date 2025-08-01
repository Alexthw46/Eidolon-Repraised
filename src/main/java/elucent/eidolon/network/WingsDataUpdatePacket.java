package elucent.eidolon.network;

import elucent.eidolon.capability.WingsDataImpl;
import elucent.eidolon.registries.EidolonCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class WingsDataUpdatePacket extends AbstractPacket {
    public static final Type<WingsDataUpdatePacket> TYPE = new Type<>(elucent.eidolon.Eidolon.prefix("wings_data_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WingsDataUpdatePacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            pkt -> pkt.uuid,
            ByteBufCodecs.VAR_LONG,
            pkt -> pkt.lastFlapTime,
            ByteBufCodecs.INT,
            pkt -> pkt.dashTicks,
            ByteBufCodecs.BOOL,
            pkt -> pkt.isFlying,
            WingsDataUpdatePacket::new
    );

    final UUID uuid;
    long lastFlapTime;
    int dashTicks;
    boolean isFlying;

    public WingsDataUpdatePacket(Player player) {
        this.uuid = player.getUUID();
        WingsDataImpl wingsData = player.getCapability(EidolonCapabilities.WINGS_CAPABILITY);
        if (wingsData != null) {
            lastFlapTime = wingsData.getLastFlapTime(player);
            dashTicks = wingsData.getDashTicks(player);
            isFlying = wingsData.isFlying(player);
        }
    }

    public WingsDataUpdatePacket(UUID uuid, long lastFlapTime, int dashTicks, boolean isFlying) {
        this.uuid = uuid;
        this.lastFlapTime = lastFlapTime;
        this.dashTicks = dashTicks;
        this.isFlying = isFlying;
    }

    public static void encode(WingsDataUpdatePacket object, FriendlyByteBuf buffer) {
        buffer.writeUUID(object.uuid);
        buffer.writeLong(object.lastFlapTime);
        buffer.writeInt(object.dashTicks);
        buffer.writeBoolean(object.isFlying);
    }

    public static WingsDataUpdatePacket decode(FriendlyByteBuf buffer) {
        return new WingsDataUpdatePacket(buffer.readUUID(), buffer.readLong(), buffer.readInt(), buffer.readBoolean());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (player.getUUID().equals(this.uuid)) {
            var wingsData = player.getCapability(EidolonCapabilities.WINGS_CAPABILITY);
            if (wingsData != null) {
                if (this.isFlying && !wingsData.isFlying(player)) {
                    wingsData.startFlying(player);
                } else if (!this.isFlying && wingsData.isFlying(player)) {
                    wingsData.stopFlying(player);
                }
            }
        }

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
