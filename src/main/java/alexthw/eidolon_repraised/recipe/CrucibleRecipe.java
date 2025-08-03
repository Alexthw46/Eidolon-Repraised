package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.common.tile.CrucibleTileEntity.CrucibleStep;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static alexthw.eidolon_repraised.recipe.CrucibleRecipe.Step.STEP_CODEC;

public class CrucibleRecipe implements Recipe<CraftingInput> {
    List<Step> steps;
    ResourceLocation registryName;
    final ItemStack result;

    public ItemStack getResult() {
        return result;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (Step step : steps) {
            ingredients.addAll(step.matches);
        }
        return ingredients;
    }

    public record Step(
            int stirs,
            List<Ingredient> matches) {

        public Step(int stirs, List<Ingredient> matches) {
            this.stirs = stirs;
            this.matches = new ArrayList<>(matches);
        }

        public static final Codec<Step> STEP_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("stirs", 0).forGetter(s -> s.stirs),
                Ingredient.CODEC.listOf().fieldOf("items").forGetter(s -> s.matches)
        ).apply(instance, Step::new));

        public static StreamCodec<RegistryFriendlyByteBuf, Step> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                Step::stirs,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                Step::matches,
                Step::new
        );

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
    public boolean matches(@NotNull CraftingInput inv, @NotNull Level worldIn) {
        return false; // we don't use a single inventory, so we ignore this one
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput inv, @NotNull HolderLookup.Provider registryAccess) {
        return getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false; // we don't use a single inventory, so we ignore this one
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registryAccess) {
        return result;
    }

    public @NotNull ItemStack getResultItem() {
        return result;
    }

    public @NotNull ResourceLocation getId() {
        return registryName;
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {

        public static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                STEP_CODEC.listOf().fieldOf("steps").forGetter(r -> r.steps),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result)
        ).apply(instance, CrucibleRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> STREAM_CODEC = StreamCodec.composite(
                Step.STREAM_CODEC.apply(ByteBufCodecs.list()),
                CrucibleRecipe::getSteps,
                ItemStack.STREAM_CODEC,
                CrucibleRecipe::getResultItem,
                CrucibleRecipe::new
        );


        @Override
        public @NotNull MapCodec<CrucibleRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.CRUCIBLE_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.CRUCIBLE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true; // needed to prevent errors loading modded recipes in the recipe book
    }
}
