package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;


public class TESyncPacket {
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

    public static void consume(TESyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            ServerPlayer sender = ctx.get().getSender();
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
                world = Eidolon.proxy.getWorld();
            else {
                if (sender == null) return;
                world = sender.level;
            }

            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                System.out.printf("TESyncPacket received from client %s. This is not supposed to happen and has been suppressed.", sender == null ? "" : sender.getUUID());
                return;
            }

            BlockEntity t = world.getBlockEntity(packet.pos);
            if (t != null) {
                t.load(packet.tag);
                t.setChanged();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
