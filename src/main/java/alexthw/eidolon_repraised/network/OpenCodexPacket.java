package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.codex.CodexGui;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class OpenCodexPacket extends AbstractPacket {
    public static final Type<OpenCodexPacket> TYPE = new Type<>(Eidolon.prefix("open_codex"));
    public static final StreamCodec<FriendlyByteBuf, OpenCodexPacket> CODEC = StreamCodec.ofMember(
            OpenCodexPacket::encode,
            OpenCodexPacket::decode
    );

    public OpenCodexPacket() {
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
    }

    public static OpenCodexPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new OpenCodexPacket();
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        minecraft.setScreen(CodexGui.getInstance());
    }

    public @NotNull Type<OpenCodexPacket> type() {
        return TYPE;
    }

}
