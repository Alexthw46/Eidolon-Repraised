package elucent.eidolon.network;

import java.util.function.Supplier;

import elucent.eidolon.gui.ResearchTableContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.world.level.Level;

public class ResearchActionPacket {
    public enum Action {
        SUBMIT_GOAL, STAMP
    }

    final Action action;
    final int index;

    public ResearchActionPacket(Action action, int index) {
        this.action = action;
        this.index = index;
    }

    public ResearchActionPacket(Action action) {
        this(action, 0);
    }

    public static void encode(ResearchActionPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.action.ordinal());
        buffer.writeInt(object.index);
    }

    public static ResearchActionPacket decode(FriendlyByteBuf buffer) {
        Action action = Action.values()[buffer.readInt()];
        int index = buffer.readInt();
        return new ResearchActionPacket(action, index);
    }

    public static void consume(ResearchActionPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER;

            ServerPlayer player = ctx.get().getSender();
            AbstractContainerMenu menu = player.containerMenu;
            Level world = ctx.get().getSender().level;
            if (world != null && menu instanceof ResearchTableContainer rc) {
                if (packet.action == Action.SUBMIT_GOAL) {
                    rc.trySubmitGoal(player, packet.index);
                }
                else if (packet.action == Action.STAMP) {
                    rc.tryStamp(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
