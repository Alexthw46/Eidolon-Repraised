package elucent.eidolon.proxy;

import elucent.eidolon.network.Networking;
import elucent.eidolon.network.OpenCodexPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

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
    public void init() {
        //
    }

    @Override
    public void openCodexGui(Player player) {
        Networking.sendTo(player, new OpenCodexPacket());
    }
}
