package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.gui.ResearchTableContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public class ResearchActionPacket extends AbstractPacket {
    public static final Type<ResearchActionPacket> TYPE = new Type<>(Eidolon.prefix("research_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchActionPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            pkt -> pkt.action.ordinal(),
            ByteBufCodecs.INT,
            pkt -> pkt.index,
            (actionOrdinal, index) -> new ResearchActionPacket(Action.values()[actionOrdinal], index)
    );

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

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu;
        Level world = player.level();
        if (menu instanceof ResearchTableContainer rc) {
            if (this.action == Action.SUBMIT_GOAL) {
                rc.trySubmitGoal(player, this.index);
            } else if (this.action == Action.STAMP) {
                rc.tryStamp(player);
            }
        }
    }

    public @NotNull Type<ResearchActionPacket> type() {
        return TYPE;
    }
}
