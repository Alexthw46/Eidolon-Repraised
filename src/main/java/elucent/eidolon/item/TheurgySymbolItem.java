package elucent.eidolon.item;

import net.minecraft.world.item.ItemStack;

public class TheurgySymbolItem extends ItemBase {
    public TheurgySymbolItem(Properties builderIn) {
        super(builderIn);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.copy();
    }
}
