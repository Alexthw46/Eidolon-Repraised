package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.registries.EidolonRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorktableRecipe implements Recipe<CraftingInput> {
    final List<Ingredient> core;
    final List<Ingredient> extras;
    final ItemStack result;
    ResourceLocation registryName;

    public WorktableRecipe(Ingredient[] core, Ingredient[] extras, ItemStack result) {
        this.core = List.of(core);
        this.extras = List.of(extras);
        this.result = result;
    }

    //for use in the codex
    @Deprecated
    public WorktableRecipe(ItemStack[] inputs, ItemStack result) {
        Ingredient[] core = new Ingredient[9];
        Ingredient[] extras = new Ingredient[4];
        for (int i = 0; i < inputs.length; i++) {
            if (i < 9) core[i] = Ingredient.of(inputs[i]);
            else extras[i - 9] = Ingredient.of(inputs[i]);
        }
        this.core = List.of(core);
        this.extras = List.of(extras);

        this.result = result;
    }

    public WorktableRecipe(List<Ingredient> core, List<Ingredient> outer, ItemStack itemStack) {
        this.core = core;
        this.extras = outer;
        this.result = itemStack;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public WorktableRecipe setRegistryName(String domain, String path) {
        this.registryName = ResourceLocation.fromNamespaceAndPath(domain, path);
        return this;
    }

    public WorktableRecipe setRegistryName(ResourceLocation registryName) {
        this.registryName = registryName;
        return this;
    }

    public Ingredient[] getCoreA() {
        return core.toArray(new Ingredient[9]);
    }

    public Ingredient[] getOuterA() {
        return extras.toArray(new Ingredient[4]);
    }


    public List<Ingredient> getCore() {
        return core;
    }

    public List<Ingredient> getOuter() {
        return extras;
    }


    public boolean matches(Container coreInv, Container extraInv) {
        if (coreInv.getContainerSize() < 9 || extraInv.getContainerSize() < 4 || core == null) return false;
        for (int i = 0; i < core.size(); i++) {
            Ingredient ingredient = core.get(i);
            if (ingredient == null) continue;
            if (!ingredient.test(coreInv.getItem(i))) return false;
        }
        for (int i = 0; i < extras.size(); i++) {
            Ingredient ingredient = extras.get(i);
            if (ingredient == null) continue;
            if (!ingredient.test(extraInv.getItem(i))) return false;
        }
        return true;
    }

    public NonNullList<ItemStack> getRemainingItems(Container coreInv, Container extraInv) {
        NonNullList<ItemStack> items = NonNullList.withSize(13, ItemStack.EMPTY);

        for (int i = 0; i < items.size(); ++i) {
            Container inv = i < 9 ? coreInv : extraInv;
            ItemStack item = inv.getItem(i < 9 ? i : i - 9);
            if (item.hasCraftingRemainingItem()) {
                items.set(i, item.getCraftingRemainingItem());
            }
        }

        return items;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.addAll(core);
        ingredients.addAll(extras);
        return ingredients;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    @Override
    public boolean matches(@NotNull CraftingInput inv, @NotNull Level worldIn) {
        return false; // we don't use a single inventory, so we ignore this one
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false; // we don't use a single inventory, so we ignore this one
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return result;
    }


    public static class Serializer implements RecipeSerializer<WorktableRecipe> {

        public static final MapCodec<WorktableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf().fieldOf("core").forGetter(r -> r.core),
                Ingredient.CODEC.listOf().fieldOf("reagents").forGetter(r -> (r.extras)),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result)
        ).apply(instance, WorktableRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WorktableRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                WorktableRecipe::getCore,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                WorktableRecipe::getOuter,
                ItemStack.STREAM_CODEC,
                WorktableRecipe::getResult,
                WorktableRecipe::new
        );

        @Override
        public @NotNull MapCodec<WorktableRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorktableRecipe> streamCodec() {
            return STREAM_CODEC;
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
