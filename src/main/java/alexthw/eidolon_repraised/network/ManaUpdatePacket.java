package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ManaUpdatePacket extends AbstractPacket {
    public static final CustomPacketPayload.Type<ManaUpdatePacket> TYPE = new CustomPacketPayload.Type<>(Eidolon.prefix("mana_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ManaUpdatePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            pkt -> pkt.isPlayer,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
            pkt -> pkt.isPlayer ? java.util.Optional.of(pkt.uuid) : java.util.Optional.empty(),
            ByteBufCodecs.optional(ByteBufCodecs.INT),
            pkt -> pkt.isPlayer ? java.util.Optional.empty() : java.util.Optional.of(pkt.id),
            ByteBufCodecs.COMPOUND_TAG,
            pkt -> pkt.tag,
            (isPlayer, uuidOpt, idOpt, tag) -> isPlayer
                    ? new ManaUpdatePacket(uuidOpt.orElseThrow(), tag)
                    : new ManaUpdatePacket(idOpt.orElseThrow(), tag)
    );

    final boolean isPlayer;
    UUID uuid;
    int id;
    CompoundTag tag;

    public ManaUpdatePacket(int id, CompoundTag tag) {
        this.isPlayer = false;
        this.id = id;
        this.tag = tag;
    }

    public ManaUpdatePacket(UUID uuid, CompoundTag tag) {
        this.isPlayer = true;
        this.uuid = uuid;
        this.tag = tag;
    }

    public ManaUpdatePacket(LivingEntity entity) {
        this.isPlayer = entity instanceof Player;
        if (isPlayer) this.uuid = entity.getUUID();
        else this.id = entity.getId();
        this.tag = entity.getCapability(EidolonCapabilities.MANA_CAPABILITY).serializeNBT();
    }

    public ManaUpdatePacket(Player entity) {
        this.isPlayer = true;
        this.uuid = entity.getUUID();
        this.tag = entity.getCapability(EidolonCapabilities.MANA_CAPABILITY).serializeNBT();
    }

    public static void encode(ManaUpdatePacket object, FriendlyByteBuf buffer) {
        buffer.writeBoolean(object.isPlayer);
        if (object.isPlayer) buffer.writeUUID(object.uuid);
        else buffer.writeInt(object.id);
        buffer.writeNbt(object.tag);
    }

    public static ManaUpdatePacket decode(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            return new ManaUpdatePacket(buffer.readUUID(), buffer.readNbt());
        } else return new ManaUpdatePacket(buffer.readInt(), buffer.readNbt());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        player.getCapability(EidolonCapabilities.MANA_CAPABILITY).deserializeNBT(this.tag);
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
