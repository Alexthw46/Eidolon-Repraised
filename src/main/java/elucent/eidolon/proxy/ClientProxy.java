package elucent.eidolon.proxy;

import elucent.eidolon.Eidolon;
import elucent.eidolon.codex.CodexGui;
import elucent.eidolon.registries.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
    public void init() {
        Registry.clientInit();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Eidolon::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Eidolon::registerOverlays);
    }

    @Override
    public void openCodexGui() {
        Minecraft.getInstance().setScreen(CodexGui.getInstance());
    }
}
