package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.codex.CodexChapters;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class InitCodexPacket extends AbstractPacket {
    public static final Type<InitCodexPacket> TYPE = new Type<>(Eidolon.prefix("init_codex"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, InitCodexPacket> CODEC = StreamCodec.of(
            (i, p) -> {
            },
            InitCodexPacket::new
    );

    public InitCodexPacket(RegistryFriendlyByteBuf buf) {
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        CodexChapters.init();
    }
}
