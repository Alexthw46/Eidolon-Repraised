package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.EidolonCapabilities;
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
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class KnowledgeUpdatePacket extends AbstractPacket {
    public static final Type<KnowledgeUpdatePacket> TYPE = new Type<>(Eidolon.prefix("knowledge_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgeUpdatePacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            pkt -> pkt.uuid,
            ByteBufCodecs.COMPOUND_TAG,
            pkt -> pkt.tag,
            ByteBufCodecs.BOOL,
            pkt -> pkt.playSound,
            KnowledgeUpdatePacket::new
    );

    final UUID uuid;
    CompoundTag tag;
    final boolean playSound;

    public KnowledgeUpdatePacket(UUID uuid, CompoundTag tag, boolean playSound) {
        this.uuid = uuid;
        this.tag = tag;
        this.playSound = playSound;
    }

    public KnowledgeUpdatePacket(Player entity, boolean playSound) {
        this.uuid = entity.getUUID();
        var k = entity.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
        if (k != null) this.tag = ((INBTSerializable<CompoundTag>) k).serializeNBT(entity.registryAccess());
        this.playSound = playSound;
    }

    public static void encode(KnowledgeUpdatePacket object, FriendlyByteBuf buffer) {
        buffer.writeUUID(object.uuid);
        buffer.writeNbt(object.tag);
        buffer.writeBoolean(object.playSound);
    }

    public static KnowledgeUpdatePacket decode(FriendlyByteBuf buffer) {
        return new KnowledgeUpdatePacket(buffer.readUUID(), buffer.readNbt(), buffer.readBoolean());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();
        if (player.getUUID().equals(this.uuid)) {
            var k = player.getCapability(EidolonCapabilities.KNOWLEDGE_CAPABILITY);
            {
                //((INBTSerializable<CompoundTag>) k).deserializeNBT(this.tag);
                if (this.playSound) player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0f, 0.5f);
            }
        }
    }

    public @NotNull Type<KnowledgeUpdatePacket> type() {
        return TYPE;
    }
}
