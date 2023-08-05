package elucent.eidolon.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeUtil {
    public static RecipeManager getRecipeManager() {
        return DistExecutor.unsafeRunForDist(
                () -> () -> Minecraft.getInstance().getConnection().getRecipeManager(),
                () -> () -> ServerLifecycleHooks.getCurrentServer().getRecipeManager()
        );
    }

    public static Ingredient ingredientFromObject(Object object) {
        if (object instanceof Item) return Ingredient.of((Item)object);
        else if (object instanceof Block) return Ingredient.of(new ItemStack((Block)object));
        else if (object instanceof ItemStack) return Ingredient.of((ItemStack)object);
        else if (object instanceof TagKey) return Ingredient.of((TagKey<Item>)object);
        else return Ingredient.EMPTY;
    }

    public static List<Ingredient> ingredientsFromObjects(List<Object> objects) {
        return objects.stream().map(RecipeUtil::ingredientFromObject).collect(Collectors.toList());
    }

    public static ItemStack stackFromObject(Object object) {
        if (object instanceof Item) return new ItemStack((Item) object);
        else if (object instanceof Block) return new ItemStack((Block) object);
        else if (object instanceof ItemStack) return ((ItemStack) object).copy();
            //TODO: tags
        else return ItemStack.EMPTY;
    }

    public static List<ItemStack> stacksFromObjects(List<Object> objects) {
        return objects.stream().map(RecipeUtil::stackFromObject).collect(Collectors.toList());
    }

    /**
     * Parse the input of a shapeless recipe.
     *
     * @param json The recipe's JSON object
     * @return A NonNullList containing the ingredients specified in the JSON object
     */
    public static NonNullList<Ingredient> parseShapeless(final JsonObject json) {
        final NonNullList<Ingredient> ingredients = NonNullList.create();
        for (final JsonElement element : GsonHelper.getAsJsonArray(json, "ingredients"))
            ingredients.add(CraftingHelper.getIngredient(element, true));

        if (ingredients.isEmpty())
            throw new JsonParseException("No ingredients for shapeless recipe");

        return ingredients;
    }
}
