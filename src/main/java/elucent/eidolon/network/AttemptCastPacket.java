package elucent.eidolon.network;

import elucent.eidolon.entity.ChantCasterEntity;
import elucent.eidolon.spell.Sign;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

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

            Level world = ctx.get().getSender().level;
            if (world != null) {
                Player player = world.getPlayerByUUID(packet.uuid);
                if (player != null) {
                    List<Sign> runes = packet.runes;
                    for (Sign rune : runes) if (!KnowledgeUtil.knowsSign(player, rune)) return;
                    Vec3 placement = player.position().add(0, player.getBbHeight() * 2 / 3, 0).add(player.getLookAngle().scale(0.5f));
                    ChantCasterEntity entity = new ChantCasterEntity(world, player, runes, player.getLookAngle());
                    entity.setPos(placement.x, placement.y, placement.z);
                    world.addFreshEntity(entity);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
