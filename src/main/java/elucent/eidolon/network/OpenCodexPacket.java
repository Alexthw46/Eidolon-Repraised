package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenCodexPacket {

    public OpenCodexPacket() {
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
    }

    public static OpenCodexPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new OpenCodexPacket();
    }


    public static void consume(OpenCodexPacket pkt, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Eidolon.proxy.openCodexGui(context.getSender());
        });
        context.setPacketHandled(true);
    }
}
