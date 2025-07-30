package elucent.eidolon.network;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.gui.ScriptoriumContainer;
import elucent.eidolon.registries.Signs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InscribePacket {
    final List<Sign> signs = new ArrayList<>();
    final int id;

    public InscribePacket(int uuid, List<Sign> runes) {
        this.signs.addAll(runes);
        this.id = uuid;
    }

    public static void encode(InscribePacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.signs.size());
        for (int i = 0; i < object.signs.size(); i++)
            buffer.writeUtf(object.signs.get(i).getRegistryName().toString(), 255);
        buffer.writeInt(object.id);
    }

    public static InscribePacket decode(FriendlyByteBuf buffer) {
        int n = buffer.readInt();
        List<Sign> runes = new ArrayList<>();
        for (int i = 0; i < n; i++) runes.add(Signs.find(ResourceLocation.tryParse(buffer.readUtf(255))));
        return new InscribePacket(buffer.readInt(), runes);
    }

    public static void consume(InscribePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER;

            Player player = ctx.get().getSender();
            if (player == null || player.containerMenu.containerId != packet.id) return;
            if (player.containerMenu instanceof ScriptoriumContainer container) {
                container.setChant(packet.signs);
            }

        });
        ctx.get().setPacketHandled(true);
    }

}

