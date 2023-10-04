package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.ritual.SanguineRitual;
import elucent.eidolon.common.tile.BrazierTileEntity;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.util.RegistryUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemRitualRecipe extends RitualRecipe {

    public ItemStack result; // Result item
    public boolean keepNbtOfReagent = false;

    public ItemRitualRecipe(ResourceLocation recipeId, List<Ingredient> stacks, List<Ingredient> foci, Ingredient reagent, ItemStack output, boolean keepNbtOfReagent, float healthRequirement) {
        super(recipeId, reagent, stacks, foci, healthRequirement);
        this.result = output;
        this.keepNbtOfReagent = keepNbtOfReagent;
    }

    public ItemStack getResult(ItemStack reagent, BrazierTileEntity BrazierTile) {
        ItemStack result = this.result.copy();
        if (keepNbtOfReagent && reagent.hasTag()) {
            result.setTag(reagent.getTag());
            result.setDamageValue(0);
        }
        return result.copy();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
        return result == null ? ItemStack.EMPTY : result.copy();
    }

    @Override
    public Ritual getRitual() {
        return new SanguineRitual(result);
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "eidolon:ritual_brazier_crafting");

        addRitualElements(this, jsonobject);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", RegistryUtil.getRegistryName(result.getItem()).toString());
        int count = this.result.getCount();
        if (count > 1) {
            resultObj.addProperty("count", count);
        }
        jsonobject.add("output", resultObj);
        jsonobject.addProperty("keepNbtOfReagent", keepNbtOfReagent);

        return jsonobject;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.CRAFTING_RITUAL_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.CRAFTING_RITUAL_TYPE.get();
    }

    public static class SerializerCrafting extends RitualRecipe.Serializer<ItemRitualRecipe> {

        @Override
        public @NotNull ItemRitualRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "reagent"));
            float healthRequirement = json.has("healthRequirement") ? GsonHelper.getAsFloat(json, "healthRequirement") : 0;
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            boolean keepNbtOfReagent = json.has("keepNbtOfReagent") && GsonHelper.getAsBoolean(json, "keepNbtOfReagent");
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = getPedestalItems(pedestalItems);
            JsonArray focusItems = GsonHelper.getAsJsonArray(json, "focusItems");
            List<Ingredient> foci = getPedestalItems(focusItems);

            return new ItemRitualRecipe(recipeId, stacks, foci, reagent, output, keepNbtOfReagent, healthRequirement);
        }

        @Nullable
        @Override
        public ItemRitualRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            int length2 = buffer.readInt();
            Ingredient reagent = Ingredient.fromNetwork(buffer);
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    stacks.add(Ingredient.fromNetwork(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            List<Ingredient> foci = new ArrayList<>();
            for (int i = 0; i < length2; i++) {
                try {
                    foci.add(Ingredient.fromNetwork(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            int healthRequirement = buffer.readInt();

            ItemStack output = buffer.readItem();
            boolean keepNbtOfReagent = buffer.readBoolean();
            return new ItemRitualRecipe(recipeId, stacks, foci, reagent, output, keepNbtOfReagent, healthRequirement);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull ItemRitualRecipe recipe) {
            super.toNetwork(buf, recipe);
            buf.writeItem(recipe.result);
            buf.writeBoolean(recipe.keepNbtOfReagent);
        }
    }
}
