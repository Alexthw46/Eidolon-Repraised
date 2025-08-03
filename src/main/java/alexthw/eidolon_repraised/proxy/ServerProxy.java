package alexthw.eidolon_repraised.proxy;

import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.OpenCodexPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ServerProxy implements ISidedProxy {
    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public Level getWorld() {
        return ServerLifecycleHooks.getCurrentServer().overworld();
    }

    @Override
    public void init(IEventBus modEventBus) {
        //
    }

    @Override
    public void openCodexGui(Player player) {
        Networking.sendToPlayerClient(new OpenCodexPacket(), (ServerPlayer) player);
    }
}
