package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.capability.IPlayerData;
import elucent.eidolon.capability.WingsDataImpl;
import elucent.eidolon.registries.EidolonCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.function.Supplier;


public class WingsDataUpdatePacket {
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

    public static void consume(WingsDataUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Level world = Eidolon.proxy.getWorld();
            Player p = world.getPlayerByUUID(packet.uuid);
            if (p != null && p != Minecraft.getInstance().player) {
                p.getCapability(IPlayerData.INSTANCE, null).ifPresent((d) -> {
                    if (packet.isFlying && !d.isFlying(p)) d.startFlying(p);
                    else if (!packet.isFlying && d.isFlying(p)) d.stopFlying(p);
                    d.setLastFlapTime(packet.lastFlapTime);
                    d.setDashTicks(packet.dashTicks);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
