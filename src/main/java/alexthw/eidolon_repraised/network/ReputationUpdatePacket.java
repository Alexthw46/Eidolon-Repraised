package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.capability.ReputationImpl;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ReputationUpdatePacket extends AbstractPacket {
    public static final Type<ReputationUpdatePacket> TYPE = new Type<>(Eidolon.prefix("reputation_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReputationUpdatePacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            pkt -> pkt.uuid,
            ByteBufCodecs.COMPOUND_TAG,
            pkt -> pkt.tag,
            ByteBufCodecs.BOOL,
            pkt -> pkt.playSound,
            ReputationUpdatePacket::new
    );

    final UUID uuid;
    CompoundTag tag;
    final boolean playSound;

    public ReputationUpdatePacket(UUID uuid, CompoundTag tag, boolean playSound) {
        this.uuid = uuid;
        this.tag = tag;
        this.playSound = playSound;
    }

    public ReputationUpdatePacket(Player entity, boolean playSound) {
        this.uuid = entity.getUUID();
        var k = entity.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        if (k != null) this.tag = k.serializeNBT(entity.registryAccess());
        this.playSound = playSound;
    }

    public static void encode(ReputationUpdatePacket object, FriendlyByteBuf buffer) {
        buffer.writeUUID(object.uuid);
        buffer.writeNbt(object.tag);
        buffer.writeBoolean(object.playSound);
    }

    public static ReputationUpdatePacket decode(FriendlyByteBuf buffer) {
        return new ReputationUpdatePacket(buffer.readUUID(), buffer.readNbt(), buffer.readBoolean());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();
        if (player.getUUID().equals(this.uuid)) {
            ReputationImpl k = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
            if (k != null) {
                k.deserializeNBT(player.registryAccess(), this.tag);
                if (this.playSound) player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0f, 0.5f);
            }
        }
    }

    public @NotNull Type<ReputationUpdatePacket> type() {
        return TYPE;
    }
}
