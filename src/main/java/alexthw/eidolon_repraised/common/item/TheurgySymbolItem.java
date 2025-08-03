package alexthw.eidolon_repraised.common.item;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TheurgySymbolItem extends ItemBase {
    public TheurgySymbolItem(Properties builderIn) {
        super(builderIn);
    }

    @Override
    public boolean hasCraftingRemainingItem(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.copy();
    }
}
