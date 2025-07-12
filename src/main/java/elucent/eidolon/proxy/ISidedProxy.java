package elucent.eidolon.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;

public interface ISidedProxy {
    Player getPlayer();
    Level getWorld();
    void init(IEventBus modEventBus);

    void openCodexGui(Player player);
}
