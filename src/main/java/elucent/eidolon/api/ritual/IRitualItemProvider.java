package elucent.eidolon.api.ritual;

import net.minecraft.world.item.ItemStack;

public interface IRitualItemProvider {
    ItemStack provide();
    void take();
}
