package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import elucent.eidolon.api.ritual.FocusItemPresentRequirement;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.RitualRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericRitualRecipe extends RitualRecipe {

    ResourceLocation ritualRL;

    public GenericRitualRecipe(ResourceLocation id, ResourceLocation ritualRL, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, List<Ingredient> invariants, float healthRequirement) {
        super(id, reagent, pedestalItems, focusItems, invariants, healthRequirement);
        this.ritualRL = ritualRL;
    }

    @Override
    public Ritual getRitualWithRequirements() {
        Ritual ritual = super.getRitualWithRequirements();
        if (!invariantItems.isEmpty())
            ritual.addInvariants(invariantItems.stream().map(FocusItemPresentRequirement::new).collect(Collectors.toList()));
        return ritual;
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, List<ItemStack> focusItems, ItemStack reagent) {
        //do not count invariants
        if (!this.invariantItems.isEmpty())
            focusItems.removeIf(i -> invariantItems.stream().anyMatch(ing -> ing.test(i)));
        return super.isMatch(pedestalItems, focusItems, reagent);
    }

    @Override
    public Ritual getRitual() {
        return RitualRegistry.find(ritualRL);
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "eidolon:ritual_brazier");
        jsonobject.addProperty("ritual", ritualRL.toString());
        addRitualElements(this, jsonobject);

        return jsonobject;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.RITUAL_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.RITUAL_TYPE.get();
    }

    public static class Serializer extends RitualRecipe.Serializer<GenericRitualRecipe> {
        @Override
        public @NotNull GenericRitualRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "reagent"));
            float healthRequirement = json.has("healthRequirement") ? GsonHelper.getAsFloat(json, "healthRequirement") : 0;
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = getPedestalItems(pedestalItems);
            JsonArray focusItems = GsonHelper.getAsJsonArray(json, "focusItems");
            List<Ingredient> foci = getPedestalItems(focusItems);

            ResourceLocation ritualRL = new ResourceLocation(GsonHelper.getAsString(json, "ritual"));
            List<Ingredient> invariants = json.has("invariantItems") ? getPedestalItems(GsonHelper.getAsJsonArray(json, "invariantItems")) : new ArrayList<>(0);

            return new GenericRitualRecipe(recipeId, ritualRL, reagent, stacks, foci, invariants, healthRequirement);
        }

        @Override
        public @Nullable GenericRitualRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, @NotNull FriendlyByteBuf pBuffer) {
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

            ResourceLocation ritualRL = pBuffer.readResourceLocation();

            int length3 = pBuffer.readInt();

            List<Ingredient> invariantItems = new ArrayList<>();
            for (int i = 0; i < length3; i++) {
                try {
                    invariantItems.add(Ingredient.fromNetwork(pBuffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }


            return new GenericRitualRecipe(pRecipeId, ritualRL, reagent, stacks, foci, invariantItems, healthRequirement);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull GenericRitualRecipe recipe) {
            super.toNetwork(buf, recipe);
            buf.writeResourceLocation(recipe.ritualRL);
            buf.writeInt(recipe.invariantItems.size());
            for (Ingredient i : recipe.invariantItems) {
                i.toNetwork(buf);
            }
        }
    }

}
