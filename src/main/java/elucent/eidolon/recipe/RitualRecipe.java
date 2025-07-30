package elucent.eidolon.recipe;

import elucent.eidolon.api.ritual.FocusItemRequirement;
import elucent.eidolon.api.ritual.HealthRequirement;
import elucent.eidolon.api.ritual.ItemRequirement;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.tile.BrazierTileEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class RitualRecipe implements Recipe<BrazierTileEntity> {

    public Ingredient reagent; // Item on the brazier
    public List<Ingredient> pedestalItems; // Items part of the recipe, on stone hands

    public List<Ingredient> getFocusItems() {
        return focusItems;
    }

    public List<Ingredient> getInvariantItems() {
        return invariantItems;
    }

    public List<Ingredient> getPedestalItems() {
        return pedestalItems;
    }

    public Ingredient getReagent() {
        return reagent;
    }

    public float getHealthRequirement() {
        return healthRequirement;
    }

    public List<Ingredient> focusItems; // Items part of the recipe, on necrotic focus
    public List<Ingredient> invariantItems = new ArrayList<>(2); // Items part of the recipe, on necrotic focus

    float healthRequirement = 0;

    public RitualRecipe(Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, List<Ingredient> invariantItems, float healthRequirement) {
        this(reagent, pedestalItems, focusItems, healthRequirement);
        this.invariantItems = invariantItems;
    }

    public RitualRecipe(Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems) {
        this.reagent = reagent;
        this.pedestalItems = pedestalItems;
        this.focusItems = focusItems;
    }

    public RitualRecipe(Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, float healthRequirement) {
        this(reagent, pedestalItems, focusItems);
        this.healthRequirement = healthRequirement;
    }

    public RitualRecipe() {
        reagent = Ingredient.EMPTY;
        pedestalItems = new ArrayList<>();
        focusItems = new ArrayList<>();
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
        return "RitualBrazierRecipe{" +
                "catalyst=" + reagent +
                ", pedestalItems=" + pedestalItems +
                ", focusItems=" + focusItems +
                '}';
    }

    public abstract Ritual getRitual();

    public Ritual getRitualWithRequirements() {
        Ritual ritual = getRitual().clone().addRequirements(pedestalItems.stream().map(ItemRequirement::new).collect(Collectors.toList()));
        if (!focusItems.isEmpty())
            ritual.addRequirements(focusItems.stream().map(FocusItemRequirement::new).collect(Collectors.toList()));
        if (healthRequirement > 0) ritual.addRequirement(new HealthRequirement(healthRequirement));
        // use IRequirement's getPriority method to sort the step requirements so foci are checked first
        ritual.sortRequirements();
        return ritual;
    }

    @Override
    public boolean matches(@NotNull BrazierTileEntity tile, @NotNull Level worldIn) {
        List<ItemStack> pedestalItems = new ArrayList<>(), focusItems = new ArrayList<>();
        tile.providePedestalItems(pedestalItems, focusItems);
        return isMatch(pedestalItems, focusItems, tile.getStack());
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull BrazierTileEntity inv, @NotNull HolderLookup.Provider registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider access) {
        return ItemStack.EMPTY;
    }

    public abstract @NotNull ResourceLocation getId();

    public abstract static class Serializer<T extends RitualRecipe> implements RecipeSerializer<T> {

    }
}