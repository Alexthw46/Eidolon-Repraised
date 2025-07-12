package elucent.eidolon.proxy;

import elucent.eidolon.Eidolon;
import elucent.eidolon.codex.CodexGui;
import elucent.eidolon.registries.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;

public class ClientProxy implements ISidedProxy {
    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public Level getWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public void init(IEventBus modEventBus) {
        Registry.clientInit();
        modEventBus.addListener(Eidolon::clientSetup);
        modEventBus.addListener(Eidolon::registerOverlays);
    }

    @Override
    public void openCodexGui(Player player) {
        Minecraft.getInstance().setScreen(CodexGui.getInstance());
    }
}
