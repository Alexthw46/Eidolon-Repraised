package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.ritual.SummonRitual;
import elucent.eidolon.registries.EidolonRecipes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SummonRitualRecipe extends RitualRecipe {

    ResourceLocation entity;
    int count;

    public SummonRitualRecipe(ResourceLocation id, ResourceLocation result, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, int count, float healthRequirement) {
        super(id, reagent, pedestalItems, focusItems, healthRequirement);
        this.entity = result;
        this.count = count;
    }

    public SummonRitualRecipe(ResourceLocation result, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, int count, float healthRequirement) {
        this(new ResourceLocation(result.getNamespace(), "summon_" + result.getPath()), result, reagent, pedestalItems, focusItems, count, healthRequirement);
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "eidolon:ritual_brazier_summoning");

        addRitualElements(this, jsonobject);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("entity", entity.toString());
        if (count > 1) {
            resultObj.addProperty("count", count);
        }

        jsonobject.add("output", resultObj);

        return jsonobject;
    }

    public SummonRitualRecipe(ResourceLocation id, ResourceLocation result, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems) {
        super(id, reagent, pedestalItems, focusItems);
        this.entity = result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.SUMMON_RITUAL_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.SUMMON_RITUAL_TYPE.get();
    }

    @Override
    public Ritual getRitual() {
        return new SummonRitual(ForgeRegistries.ENTITY_TYPES.getValue(entity), count);
    }

    public static class Serializer extends RitualRecipe.Serializer<SummonRitualRecipe> {

        @Override
        public @NotNull SummonRitualRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "reagent"));
            float healthRequirement = json.has("healthRequirement") ? GsonHelper.getAsFloat(json, "healthRequirement") : 0;
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = getPedestalItems(pedestalItems);
            JsonArray focusItems = GsonHelper.getAsJsonArray(json, "focusItems");
            List<Ingredient> foci = getPedestalItems(focusItems);

            JsonObject resultObj = GsonHelper.getAsJsonObject(json, "output");
            ResourceLocation entity = new ResourceLocation(GsonHelper.getAsString(resultObj, "entity"));
            int count = resultObj.has("count") ? GsonHelper.getAsInt(resultObj, "count") : 1;

            return new SummonRitualRecipe(recipeId, entity, reagent, stacks, foci, count, healthRequirement);
        }

        @Override
        public @Nullable SummonRitualRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, @NotNull FriendlyByteBuf pBuffer) {
            int length = pBuffer.readInt();
            int length2 = pBuffer.readInt();
            Ingredient reagent = Ingredient.fromNetwork(pBuffer);
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    stacks.add(Ingredient.fromNetwork(pBuffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            List<Ingredient> foci = new ArrayList<>();
            for (int i = 0; i < length2; i++) {
                try {
                    foci.add(Ingredient.fromNetwork(pBuffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            float healthRequirement = pBuffer.readFloat();

            ResourceLocation entity = pBuffer.readResourceLocation();
            int count = pBuffer.readInt();

            return new SummonRitualRecipe(pRecipeId, entity, reagent, stacks, foci, count, healthRequirement);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, @NotNull SummonRitualRecipe pRecipe) {
            super.toNetwork(pBuffer, pRecipe);
            pBuffer.writeResourceLocation(pRecipe.entity);
            pBuffer.writeInt(pRecipe.count);
        }

    }
}
