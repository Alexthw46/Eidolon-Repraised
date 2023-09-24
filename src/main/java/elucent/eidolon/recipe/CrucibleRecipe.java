package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import elucent.eidolon.Eidolon;
import elucent.eidolon.codex.CruciblePage;
import elucent.eidolon.common.tile.CrucibleTileEntity.CrucibleStep;
import elucent.eidolon.registries.Registry;
import net.minecraft.core.RegistryAccess;
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

import java.util.ArrayList;
import java.util.List;

public class CrucibleRecipe implements Recipe<Container> {
    List<Step> steps;
    ResourceLocation registryName;
    final ItemStack result;

    @Deprecated
    public CrucibleRecipe(CruciblePage.CrucibleStep[] steps, ItemStack result) {
        this.result = result;
        this.steps = new ArrayList<>();
        for (CruciblePage.CrucibleStep step : steps) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (ItemStack stack : step.stacks) ingredients.add(Ingredient.of(stack));
            this.steps.add(new Step(step.stirs, ingredients));
        }
    }

    public ItemStack getResult() {
        return result;
    }

    public static class Step {
        public final List<Ingredient> matches = new ArrayList<>();
        public final int stirs;

        public Step(int stirs, List<Ingredient> matches) {
            this.stirs = stirs;
            this.matches.addAll(matches);
        }
    }

    public CrucibleRecipe(List<Step> steps, ItemStack result) {
        this.steps = steps;
        this.result = result;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public CrucibleRecipe setRegistryName(ResourceLocation registryName) {
        this.registryName = registryName;
        return this;
    }

    public boolean matches(List<CrucibleStep> items) {
        if (steps.size() != items.size()) return false;

        List<Ingredient> matchList = new ArrayList<>();
        List<ItemStack> itemList = new ArrayList<>();

        for (int i = 0; i < steps.size(); i++) {
            Step correct = steps.get(i);
            CrucibleStep provided = items.get(i);
            if (correct.stirs != provided.getStirs()) return false;

            matchList.clear();
            itemList.clear();
            matchList.addAll(correct.matches);
            itemList.addAll(provided.getContents());

            for (int j = 0; j < matchList.size(); j++) {
                for (int k = 0; k < itemList.size(); k++) {
                    if (matchList.get(j).test(itemList.get(k))) {
                        matchList.remove(j--);
                        itemList.remove(k--);
                        break;
                    }
                }
            }

            if (!matchList.isEmpty() || !itemList.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public boolean matches(@NotNull Container inv, @NotNull Level worldIn) {
        return false; // we don't use a single inventory, so we ignore this one
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registryAccess) {
        return getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false; // we don't use a single inventory, so we ignore this one
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return result;
    }

    public @NotNull ItemStack getResultItem() {
        return result;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return registryName;
    }

    public static class Type implements RecipeType<CrucibleRecipe> {
        @Override
        public String toString() {
            return Eidolon.MODID + ":crucible";
        }

        public static final CrucibleRecipe.Type INSTANCE = new CrucibleRecipe.Type();
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        @Override
        public @NotNull CrucibleRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            List<Step> steps = new ArrayList<>();
            JsonArray stepArray = json.getAsJsonArray("steps");
            for (JsonElement elt : stepArray) {
                if (!elt.isJsonObject()) throw new JsonSyntaxException("Expected JSON object for crucible step.");
                JsonObject step = elt.getAsJsonObject();
                int stirs = step.has("stirs") ? step.get("stirs").getAsInt() : 0;
                List<Ingredient> matches = new ArrayList<>();
                if (step.has("items")) {
                    JsonArray items = step.get("items").getAsJsonArray();
                    for (JsonElement item : items) matches.add(Ingredient.fromJson(item));
                }
                steps.add(new Step(stirs, matches));
            }
            ItemStack result = CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);
            return CrucibleRegistry.register(new CrucibleRecipe(steps, result).setRegistryName(recipeId));
        }

        @Override
        public CrucibleRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int count = buffer.readInt();
            List<Step> steps = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int stirs = buffer.readInt();
                int ingredients = buffer.readInt();
                List<Ingredient> matches = new ArrayList<>();
                for (int j = 0; j < ingredients; j++) matches.add(Ingredient.fromNetwork(buffer));
                steps.add(new Step(stirs, matches));
            }
            ItemStack result = buffer.readItem();
            return CrucibleRegistry.register(new CrucibleRecipe(steps, result).setRegistryName(recipeId));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CrucibleRecipe recipe) {
            buffer.writeInt(recipe.steps.size());
            for (Step step : recipe.steps) {
                buffer.writeInt(step.stirs);
                buffer.writeInt(step.matches.size());
                for (Ingredient i : step.matches) i.toNetwork(buffer);
            }
            buffer.writeItem(recipe.result);
        }
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Registry.CRUCIBLE_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return CrucibleRecipe.Type.INSTANCE;
    }

    @Override
    public boolean isSpecial() {
        return true; // needed to prevent errors loading modded recipes in the recipe book
    }
}
