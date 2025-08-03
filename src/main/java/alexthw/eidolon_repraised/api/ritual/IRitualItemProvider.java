package alexthw.eidolon_repraised.api.ritual;

import net.minecraft.world.item.ItemStack;

public interface IRitualItemProvider {
    ItemStack provide();
    void take();
}
