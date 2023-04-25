package elucent.eidolon.item;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Item.Properties;

public class UnholySymbolItem extends ItemBase {
    public UnholySymbolItem(Properties builderIn) {
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
