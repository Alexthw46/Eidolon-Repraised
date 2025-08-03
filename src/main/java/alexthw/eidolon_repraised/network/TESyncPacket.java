package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;


public class TESyncPacket extends AbstractPacket {
    public static final Type<TESyncPacket> TYPE = new Type<>(Eidolon.prefix("te_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TESyncPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            pkt -> pkt.pos,
            ByteBufCodecs.COMPOUND_TAG,
            pkt -> pkt.tag,
            TESyncPacket::new
    );

    final BlockPos pos;
    final CompoundTag tag;

    public TESyncPacket(BlockPos pos, CompoundTag tag) {
        this.pos = pos;
        this.tag = tag;
    }

    public static void encode(TESyncPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
        buffer.writeNbt(object.tag);
    }

    public static TESyncPacket decode(FriendlyByteBuf buffer) {
        return new TESyncPacket(buffer.readBlockPos(), buffer.readNbt());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();
        BlockEntity t = world.getBlockEntity(this.pos);
        if (t != null) {
            t.loadWithComponents(this.tag, world.registryAccess());
            t.setChanged();
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
