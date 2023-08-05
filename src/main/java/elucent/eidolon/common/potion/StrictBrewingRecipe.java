package elucent.eidolon.common.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nonnull;

public class StrictBrewingRecipe extends BrewingRecipe {
    final ItemStack inputStack;

    public StrictBrewingRecipe(ItemStack input, Ingredient ingredient, ItemStack output) {
        super(Ingredient.of(input), ingredient, output);
        this.inputStack = input;
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack) {
        return ItemStack.isSameItem(inputStack, stack)
               && ItemStack.isSameItemSameTags(inputStack, stack);
    }
}
