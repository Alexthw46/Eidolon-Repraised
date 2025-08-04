package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.registries.EidolonRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class WorktableRecipe implements Recipe<CraftingInput> {
    public final ShapedRecipePattern pattern_core;
    public final ShapedRecipePattern pattern_outer;

    final ItemStack result;

    public WorktableRecipe(ShapedRecipePattern core, ShapedRecipePattern outer, ItemStack result) {
        this.pattern_core = core;
        this.pattern_outer = outer;
        this.result = result;
    }

    public Ingredient[] getCoreA() {
        return getCore().toArray(new Ingredient[9]);
    }

    public Ingredient[] getOuterA() {
        return getOuter().toArray(new Ingredient[4]);
    }


    public List<Ingredient> getCore() {
        return pattern_core.ingredients();
    }

    public List<Ingredient> getOuter() {
        return pattern_outer.ingredients();
    }

    public boolean matches(CraftingContainer coreInv, CraftingContainer extraInv) {
        if (coreInv.getContainerSize() < 9 || extraInv.getContainerSize() < 4 || pattern_core == null) return false;
        return pattern_core.matches(coreInv.asCraftInput()) && pattern_outer.matches(extraInv.asCraftInput());
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
        ingredients.addAll(getCore());
        ingredients.addAll(getOuter());
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
        static int maxWidth = 4;
        static int maxHeight = 1;
        public static final Codec<List<String>> R_PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(p_312085_ -> {
            if (p_312085_.size() > maxHeight) {
                return DataResult.error(() -> "Invalid pattern: too many rows, %s is maximum".formatted(maxHeight));
            } else if (p_312085_.isEmpty()) {
                return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
            } else {
                int i = p_312085_.getFirst().length();

                for (String s : p_312085_) {
                    if (s.length() > maxWidth) {
                        return DataResult.error(() -> "Invalid pattern: too many columns, %s is maximum".formatted(maxWidth));
                    }

                    if (i != s.length()) {
                        return DataResult.error(() -> "Invalid pattern: each row must be the same width");
                    }
                }

                return DataResult.success(p_312085_);
            }
        }, Function.identity());

        public static final MapCodec<ShapedRecipePattern.Data> REAGENT_MAP_CODEC = RecordCodecBuilder.mapCodec(
                p_312573_ -> p_312573_.group(
                                ExtraCodecs.strictUnboundedMap(ShapedRecipePattern.Data.SYMBOL_CODEC, Ingredient.CODEC_NONEMPTY).fieldOf("key").forGetter(ShapedRecipePattern.Data::key),
                                R_PATTERN_CODEC.fieldOf("reagents").forGetter(ShapedRecipePattern.Data::pattern)
                        )
                        .apply(p_312573_, ShapedRecipePattern.Data::new)
        );

        public static final MapCodec<ShapedRecipePattern> PATTERN_CORE_CODEC = ShapedRecipePattern.Data.MAP_CODEC.flatXmap(
                data -> unpack(data, false),
                p_344423_ -> p_344423_.data.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
        );
        public static final MapCodec<ShapedRecipePattern> PATTERN_OUTER_CODEC = REAGENT_MAP_CODEC.flatXmap(
                data -> unpack(data, true),
                p_344423_ -> p_344423_.data.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
        );

        public static final MapCodec<WorktableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Serializer.PATTERN_CORE_CODEC.forGetter(r -> r.pattern_core),
                Serializer.PATTERN_OUTER_CODEC.forGetter(r -> r.pattern_outer),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result)
        ).apply(instance, WorktableRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WorktableRecipe> STREAM_CODEC = StreamCodec.composite(
                ShapedRecipePattern.STREAM_CODEC,
                r -> r.pattern_core,
                ShapedRecipePattern.STREAM_CODEC,
                r -> r.pattern_outer,
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

        private static DataResult<ShapedRecipePattern> unpack(ShapedRecipePattern.Data data, boolean isOuter) {
            String[] astring = ShapedRecipePattern.shrink(data.pattern());
            int i = isOuter ? 4 : astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);
            CharSet charset = new CharArraySet(data.key().keySet());

            for (int k = 0; k < astring.length; k++) {
                String s = astring[k];

                for (int l = 0; l < s.length(); l++) {
                    char c0 = s.charAt(l);
                    Ingredient ingredient = c0 == ' ' ? Ingredient.EMPTY : data.key().get(c0);
                    if (ingredient == null) {
                        return DataResult.error(() -> "Pattern references symbol '" + c0 + "' but it's not defined in the key");
                    }

                    charset.remove(c0);
                    nonnulllist.set(l + i * k, ingredient);
                }
            }

            return DataResult.success(new ShapedRecipePattern(i, j, nonnulllist, Optional.of(data)));
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
