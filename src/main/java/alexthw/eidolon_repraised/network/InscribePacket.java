package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.gui.ScriptoriumContainer;
import alexthw.eidolon_repraised.registries.Signs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InscribePacket extends AbstractPacket {
    public static final Type<InscribePacket> TYPE = new Type<>(Eidolon.prefix("inscribe"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InscribePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            pkt -> pkt.signs.stream().map(s -> s.getRegistryName().toString()).toList(),
            ByteBufCodecs.INT,
            pkt -> pkt.id,
            (runes, id) -> new InscribePacket(id, runes.stream().map(s -> Signs.find(ResourceLocation.tryParse(s))).toList())
    );

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

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (player == null || player.containerMenu.containerId != this.id) return;
        if (player.containerMenu instanceof ScriptoriumContainer container) {
            container.setChant(this.signs);
        }
    }

    public @NotNull Type<InscribePacket> type() {
        return TYPE;
    }
}
