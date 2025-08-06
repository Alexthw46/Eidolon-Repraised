package alexthw.eidolon_repraised.api.ritual;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface IRitualItemProvider extends Container {
    ItemStack provide();
    void take();
}
