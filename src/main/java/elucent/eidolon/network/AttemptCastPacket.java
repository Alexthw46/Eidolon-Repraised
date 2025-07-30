package elucent.eidolon.network;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.common.entity.ChantCasterEntity;
import elucent.eidolon.registries.Signs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class AttemptCastPacket {
    final List<Sign> runes = new ArrayList<>();
    final UUID uuid;

    public AttemptCastPacket(Player player, List<Sign> runes) {
        this.runes.addAll(runes);
        this.uuid = player.getUUID();
    }

    public AttemptCastPacket(UUID uuid, List<Sign> runes) {
        this.runes.addAll(runes);
        this.uuid = uuid;
    }

    public static void encode(AttemptCastPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.runes.size());
        for (int i = 0; i < object.runes.size(); i ++) buffer.writeUtf(object.runes.get(i).getRegistryName().toString(), 255);
        buffer.writeUUID(object.uuid);
    }

    public static AttemptCastPacket decode(FriendlyByteBuf buffer) {
        int n = buffer.readInt();
        List<Sign> runes = new ArrayList<>();
        for (int i = 0; i < n; i++) runes.add(Signs.find(new ResourceLocation(buffer.readUtf(255))));
        return new AttemptCastPacket(buffer.readUUID(), runes);
    }

    public static void consume(AttemptCastPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER;

            Player player = ctx.get().getSender();
            if (player == null) return;
            Level world = player.level();
            player = world.getPlayerByUUID(packet.uuid);
            if (player != null) {
                ChantCasterEntity.createChanter(player, world, packet.runes);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
