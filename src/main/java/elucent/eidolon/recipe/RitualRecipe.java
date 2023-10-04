package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import elucent.eidolon.api.ritual.HealthRequirement;
import elucent.eidolon.api.ritual.ItemRequirement;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.tile.BrazierTileEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static elucent.eidolon.Eidolon.prefix;

public abstract class RitualRecipe implements Recipe<BrazierTileEntity> {

    public Ingredient reagent; // Used in the arcane pedestal
    public List<Ingredient> pedestalItems; // Items part of the recipe, on stone hands
    public List<Ingredient> focusItems; // Items part of the recipe, on necrotic focus

    public ResourceLocation id;

    float healthRequirement = 0;

    public RitualRecipe(ResourceLocation id, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems) {
        this.reagent = reagent;
        this.pedestalItems = pedestalItems;
        this.focusItems = focusItems;
        this.id = id;
    }

    public RitualRecipe(ResourceLocation id, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, float healthRequirement) {
        this(id, reagent, pedestalItems, focusItems);
        this.healthRequirement = healthRequirement;
    }

    public RitualRecipe() {
        reagent = Ingredient.EMPTY;
        pedestalItems = new ArrayList<>();
        focusItems = new ArrayList<>();
        this.id = prefix("empty");
    }

    static List<Ingredient> getPedestalItems(JsonArray pedestalJson) {
        return StreamSupport.stream(pedestalJson.spliterator(), true).map(Ingredient::fromJson).collect(Collectors.toList());
    }

    public static void addRitualElements(RitualRecipe RitualRecipe, JsonObject jsonobject) {
        jsonobject.add("reagent", RitualRecipe.reagent.toJson());
        if (RitualRecipe.healthRequirement > 0) jsonobject.addProperty("sacrifice", RitualRecipe.healthRequirement);

        JsonArray pedestalArr = new JsonArray();
        for (Ingredient i : RitualRecipe.pedestalItems) {
            pedestalArr.add(i.toJson());
        }
        jsonobject.add("pedestalItems", pedestalArr);


        JsonArray focusArr = new JsonArray();
        for (Ingredient i : RitualRecipe.focusItems) {
            focusArr.add(i.toJson());
        }
        jsonobject.add("focusItems", focusArr);
    }

    public boolean excludeJei() {
        return false;
    }

    public boolean isMatch(List<ItemStack> pedestalItems, List<ItemStack> focusItems, ItemStack reagent) {
        return doesReagentMatch(reagent) &&
               this.pedestalItems.size() == pedestalItems.size() && doItemsMatch(pedestalItems, this.pedestalItems) &&
               this.focusItems.size() == focusItems.size() && doItemsMatch(focusItems, this.focusItems);
    }

    public boolean doesReagentMatch(ItemStack reag) {
        return this.reagent.test(reag);
    }


    // Function to check if both arrays are same
    public static boolean doItemsMatch(List<ItemStack> inputs, List<Ingredient> recipeItems) {
        StackedContents recipeitemhelper = new StackedContents();
        for (ItemStack i : inputs)
            recipeitemhelper.accountStack(i, 1);

        return inputs.size() == recipeItems.size() && RecipeMatcher.findMatches(inputs, recipeItems) != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RitualRecipe that = (RitualRecipe) o;
        return Objects.equals(reagent, that.reagent) &&
               Objects.equals(pedestalItems, that.pedestalItems) &&
               Objects.equals(focusItems, that.focusItems);
    }


    @Override
    public int hashCode() {
        return Objects.hash(reagent, pedestalItems, focusItems);
    }

    @Override
    public String toString() {
        return "RitualBrazierRecipe{" + id +
               "catalyst=" + reagent +
               ", pedestalItems=" + pedestalItems +
               ", focusItems=" + focusItems +
               '}';
    }

    public abstract Ritual getRitual();

    public Ritual getRitualWithRequirements() {
        Ritual ritual = getRitual().addRequirements(pedestalItems.stream().map(ItemRequirement::new).toArray(ItemRequirement[]::new));
        if (!focusItems.isEmpty())
            ritual.addRequirements(focusItems.stream().map(ItemRequirement::new).toArray(ItemRequirement[]::new));
        if (healthRequirement > 0) ritual.addRequirement(new HealthRequirement(healthRequirement));
        return ritual;
    }

    public abstract JsonElement asRecipe();

    @Override
    public boolean matches(@NotNull BrazierTileEntity tile, @NotNull Level worldIn) {
        List<ItemStack> pedestalItems = new ArrayList<>(), focusItems = new ArrayList<>();
        tile.providePedestalItems(pedestalItems, focusItems);
        return isMatch(pedestalItems, focusItems, tile.getStack());
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull BrazierTileEntity inv, @NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    public abstract static class Serializer<T extends RitualRecipe> implements RecipeSerializer<T> {

        @Override
        public void toNetwork(FriendlyByteBuf buf, T recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            buf.writeInt(recipe.focusItems.size());
            recipe.reagent.toNetwork(buf);
            for (Ingredient i : recipe.pedestalItems) {
                i.toNetwork(buf);
            }
            for (Ingredient i : recipe.focusItems) {
                i.toNetwork(buf);
            }
            buf.writeFloat(recipe.healthRequirement);
        }
    }
}