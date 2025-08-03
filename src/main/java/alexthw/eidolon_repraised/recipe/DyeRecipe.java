package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.registries.EidolonRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static alexthw.eidolon_repraised.util.RegistryUtil.getRegistryName;

public class DyeRecipe extends ShapelessRecipe {

    public DyeRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput inv, HolderLookup.@NotNull Provider p_266797_) {
        ItemStack output = super.assemble(inv, p_266797_);
        if (!output.isEmpty()) {
            for (int i = 0; i < inv.size(); i++) { // For each slot in the crafting inventory,
                final ItemStack ingredient = inv.getItem(i); // Get the ingredient in the slot
                if (!ingredient.isEmpty() && ingredient.is(output.getItem())) {
                    output.applyComponents(ingredient.getComponentsPatch());
                }
            }
            for (int i = 0; i < inv.size(); i++) { // For each slot in the crafting inventory,
                final ItemStack ingredient = inv.getItem(i); // Get the ingredient in the slot
                DyeColor color = DyeColor.getColor(ingredient);
                if (!ingredient.isEmpty() && color != null) {
                    output.set(DataComponents.BASE_COLOR, color);
                }
            }
        }
        return output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.DYE_RECIPE.get();
    }


    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.DYE_TYPE.get();
    }

    public static JsonElement asRecipe(Item item) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "eidolon_repraised:dye");
        JsonArray ingredients = new JsonArray();
        JsonObject dyeObject = new JsonObject();
        dyeObject.addProperty("tag", Tags.Items.DYES.location().toString());
        ingredients.add(dyeObject);

        JsonObject input = new JsonObject();
        input.addProperty("item", getRegistryName(item).toString());
        ingredients.add(input);

        jsonobject.add("ingredients", ingredients);
        JsonObject itemObject = new JsonObject();
        itemObject.addProperty("item", getRegistryName(item).toString());
        jsonobject.add("result", itemObject);
        return jsonobject;
    }

}
