package elucent.eidolon.gui.jei;

import elucent.eidolon.api.IDyeable;
import elucent.eidolon.recipe.DyeRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DyeRecipeCategory implements ICraftingCategoryExtension {
    private final DyeRecipe recipe;

    public DyeRecipeCategory(DyeRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ICraftingGridHelper craftingGridHelper, @NotNull IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
                .map(ingredient -> List.of(ingredient.getItems()))
                .toList();
        ItemStack resultItem = recipe.getResultItem(RegistryAccess.EMPTY);
        List<ItemStack> results = new ArrayList<>();
        if (resultItem.getItem() instanceof IDyeable toDye) {
            var focus = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT)
                    .map(f -> f.getTypedValue().getIngredient())
                    .filter(f -> f.getItem() instanceof DyeItem)
                    .toList();

            List<DyeColor> colors = focus.isEmpty() ? Arrays.stream(recipe.getIngredients().get(0).getItems()).map(DyeColor::getColor).toList() : focus.stream().map(DyeColor::getColor).toList();

            for (DyeColor color : colors) {
                ItemStack copy = resultItem.copy();
                toDye.onDye(copy, color);
                results.add(copy);
            }
        }

        craftingGridHelper.createAndSetOutputs(builder, results);
        craftingGridHelper.createAndSetInputs(builder, inputs, 0, 0);
    }

}