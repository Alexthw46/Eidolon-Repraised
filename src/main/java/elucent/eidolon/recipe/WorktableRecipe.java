package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import elucent.eidolon.registries.EidolonRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WorktableRecipe implements Recipe<Container> {
    final Ingredient[] core;
    final Ingredient[] extras;
    final ItemStack result;
    ResourceLocation registryName;

    public WorktableRecipe(Ingredient[] core, Ingredient[] extras, ItemStack result) {
        this.core = core;
        this.extras = extras;
        this.result = result;
    }

    //for use in the codex
    @Deprecated
    public WorktableRecipe(ItemStack[] inputs, ItemStack result) {
        this.core = new Ingredient[9];
        this.extras = new Ingredient[4];
        for (int i = 0; i < inputs.length; i++) {
            if (i < 9) core[i] = Ingredient.of(inputs[i]);
            else extras[i - 9] = Ingredient.of(inputs[i]);
        }
        this.result = result;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public WorktableRecipe setRegistryName(String domain, String path) {
        this.registryName = new ResourceLocation(domain, path);
        return this;
    }

    public WorktableRecipe setRegistryName(ResourceLocation registryName) {
        this.registryName = registryName;
        return this;
    }

    public Ingredient[] getCore() {
        return core;
    }

    public Ingredient[] getOuter() {
        return extras;
    }

    public boolean matches(Container coreInv, Container extraInv) {
        if (coreInv.getContainerSize() < 9 || extraInv.getContainerSize() < 4) return false;
        for (int i = 0; i < core.length; i ++) {
            if (!core[i].test(coreInv.getItem(i))) return false;
        }
        for (int i = 0; i < extras.length; i ++) {
            if (!extras[i].test(extraInv.getItem(i))) return false;
        }
        return true;
    }

    public NonNullList<ItemStack> getRemainingItems(Container coreInv, Container extraInv) {
        NonNullList<ItemStack> items = NonNullList.withSize(13, ItemStack.EMPTY);

        for(int i = 0; i < items.size(); ++i) {
            Container inv = i < 9 ? coreInv : extraInv;
            ItemStack item = inv.getItem(i < 9 ? i : i - 9);
            if (item.hasCraftingRemainingItem()) {
                items.set(i, item.getCraftingRemainingItem());
            }
        }

        return items;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    @Override
    public boolean matches(@NotNull Container inv, @NotNull Level worldIn) {
        return false; // we don't use a single inventory, so we ignore this one
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container inv) {
        return getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false; // we don't use a single inventory, so we ignore this one
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return result;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return registryName;
    }

    public static class Serializer implements RecipeSerializer<WorktableRecipe> {
        @Override
        public @NotNull WorktableRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            Map<String, Ingredient> ingredientMap = new HashMap<>();
            JsonObject keys = json.getAsJsonObject("key");
            for (Map.Entry<String, JsonElement> e : keys.entrySet()) {
                if (e.getKey().length() != 1)
                    throw new RuntimeException("Recipe ingredient key must be a single character");
                ingredientMap.put(e.getKey(), Ingredient.fromJson(e.getValue().getAsJsonObject()));
            }
            Ingredient[] core = new Ingredient[9], extras = new Ingredient[4];
            JsonArray pattern = json.getAsJsonArray("pattern");
            if (pattern.size() != 3) throw new JsonSyntaxException("All worktable recipes must have three rows.");
            for (int i = 0; i < 3; i ++) {
                if (pattern.get(i).getAsString().length() != 3) throw new JsonSyntaxException("All worktable recipe rows must have three columns.");
                for (int j = 0; j < 3; j ++) {
                    String key = pattern.get(i).getAsString().substring(j, j + 1);
                    Ingredient item = key.equals(" ") ? Ingredient.EMPTY : ingredientMap.get(key);
                    core[i * 3 + j] = item;
                }
            }
            String reagents = json.get("reagents").getAsString();
            if (reagents.length() != 4) throw new JsonSyntaxException("All worktable recipes must have reagent strings of length 4.");
            for (int i = 0; i < 4; i ++) {
                String key = reagents.substring(i, i + 1);
                extras[i] = key.equals(" ") ? Ingredient.EMPTY : ingredientMap.get(key);
            }
            ItemStack result = CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);
            return WorktableRegistry.register(new WorktableRecipe(core, extras, result).setRegistryName(recipeId));
        }

        @Override
        public WorktableRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            Ingredient[] core = new Ingredient[9], extras = new Ingredient[4];
            for (int i = 0; i < 9; i++) core[i] = Ingredient.fromNetwork(buffer);
            for (int i = 0; i < 4; i++) extras[i] = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            return WorktableRegistry.register(new WorktableRecipe(core, extras, result).setRegistryName(recipeId));
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull WorktableRecipe recipe) {
            for (int i = 0; i < 9; i++) recipe.core[i].toNetwork(buffer);
            for (int i = 0; i < 4; i++) recipe.extras[i].toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.WORKTABLE_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.WORKTABLE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true; // needed to prevent errors loading modded recipes in the recipe book
    }
}
