package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.ritual.LocationRitual;
import elucent.eidolon.registries.EidolonRecipes;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LocationRitualRecipe extends RitualRecipe {

    ResourceLocation structureTagKey;


    public LocationRitualRecipe(ResourceLocation id, ResourceLocation structure, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, float healthRequirement) {
        super(id, reagent, pedestalItems, focusItems, healthRequirement);
        this.structureTagKey = structure;
    }


    @Override
    public Ritual getRitual() {
        return new LocationRitual(TagKey.create(Registry.STRUCTURE_REGISTRY, structureTagKey)).setRegistryName(id);
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "eidolon:ritual_brazier_location");

        addRitualElements(this, jsonobject);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("structure", structureTagKey.toString());

        jsonobject.add("output", resultObj);

        return jsonobject;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.LOCATION_RITUAL_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.LOCATION_RITUAL_TYPE.get();
    }


    public static class Serializer extends RitualRecipe.Serializer<LocationRitualRecipe> {

        @Override
        public @NotNull LocationRitualRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "reagent"));
            float healthRequirement = json.has("healthRequirement") ? GsonHelper.getAsFloat(json, "healthRequirement") : 0;
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = getPedestalItems(pedestalItems);
            JsonArray focusItems = GsonHelper.getAsJsonArray(json, "focusItems");
            List<Ingredient> foci = getPedestalItems(focusItems);

            JsonObject resultObj = GsonHelper.getAsJsonObject(json, "output");
            ResourceLocation structure = new ResourceLocation(GsonHelper.getAsString(resultObj, "structure"));

            return new LocationRitualRecipe(recipeId, structure, reagent, stacks, foci, healthRequirement);
        }

        @Override
        public @Nullable LocationRitualRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, @NotNull FriendlyByteBuf pBuffer) {
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

            ResourceLocation structure = pBuffer.readResourceLocation();

            return new LocationRitualRecipe(pRecipeId, structure, reagent, stacks, foci, healthRequirement);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, @NotNull LocationRitualRecipe pRecipe) {
            super.toNetwork(pBuffer, pRecipe);
            pBuffer.writeResourceLocation(pRecipe.structureTagKey);
        }

    }
}
