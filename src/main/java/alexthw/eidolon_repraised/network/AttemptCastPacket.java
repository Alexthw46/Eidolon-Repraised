package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.entity.ChantCasterEntity;
import alexthw.eidolon_repraised.registries.Signs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttemptCastPacket extends AbstractPacket {
    public static final Type<AttemptCastPacket> TYPE = new Type<>(Eidolon.prefix("attempt_cast"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AttemptCastPacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            pkt -> pkt.uuid,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            pkt -> pkt.runes.stream().map(sign -> sign.getRegistryName().toString()).toList(),
            (uuid, runeNames) -> {
                List<Sign> runes = new ArrayList<>();
                for (String name : runeNames) {
                    runes.add(Signs.find(ResourceLocation.parse(name)));
                }
                return new AttemptCastPacket(uuid, runes);
            }
    );

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

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        Level world = player.level();
        if (player.getUUID().equals(this.uuid)) {
            ChantCasterEntity.createChanter(player, world, this.runes);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
