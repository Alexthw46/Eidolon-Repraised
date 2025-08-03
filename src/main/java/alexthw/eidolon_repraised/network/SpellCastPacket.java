package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.spells.SignSequence;
import alexthw.eidolon_repraised.api.spells.Spell;
import alexthw.eidolon_repraised.registries.Spells;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SpellCastPacket extends AbstractPacket {
    public static final Type<SpellCastPacket> TYPE = new Type<>(Eidolon.prefix("spell_cast"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellCastPacket> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            pkt -> pkt.spell.getRegistryName(),
            ByteBufCodecs.COMPOUND_TAG,
            pkt -> pkt.seq.serializeNbt(),
            UUIDUtil.STREAM_CODEC,
            pkt -> pkt.uuid,
            BlockPos.STREAM_CODEC,
            pkt -> pkt.pos,
            (spellLoc, seqTag, uuid, pos) -> new SpellCastPacket(uuid, pos, spellLoc, SignSequence.deserializeNbt(seqTag))
    );

    final SignSequence seq;
    final Spell spell;
    final BlockPos pos;
    final UUID uuid;

    public SpellCastPacket(Player player, BlockPos pos, Spell spell, SignSequence seq) {
        this.seq = seq;
        this.pos = pos;
        this.spell = spell;
        this.uuid = player.getUUID();
    }

    public SpellCastPacket(UUID uuid, BlockPos pos, ResourceLocation location, SignSequence seq) {
        this.seq = seq;
        this.pos = pos;
        this.spell = Spells.find(location);
        this.uuid = uuid;
    }

    public static void encode(SpellCastPacket object, FriendlyByteBuf buffer) {
        buffer.writeUtf(object.spell.getRegistryName().toString());
        buffer.writeNbt(object.seq.serializeNbt());
        buffer.writeUUID(object.uuid);
        buffer.writeBlockPos(object.pos);
    }

    public static SpellCastPacket decode(FriendlyByteBuf buffer) {
        ResourceLocation spell = ResourceLocation.parse(buffer.readUtf());
        SignSequence seq = SignSequence.deserializeNbt(buffer.readNbt());
        return new SpellCastPacket(buffer.readUUID(), buffer.readBlockPos(), spell, seq);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        Level world = player.level();
        this.spell.cast(world, this.pos, player, this.seq);

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
