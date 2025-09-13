package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.registries.EidolonRecipes;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ChantConversionRecipe implements Recipe<RecipeInput> {

    public Ingredient input;
    ItemStack result;
    public float minDevotion;
    public @Nullable ResourceLocation deity;

    public ChantConversionRecipe(Ingredient input, ItemStack result, float minDevotion, @Nullable ResourceLocation Deity) {
        this.input = input;
        this.result = result;
        this.minDevotion = minDevotion;
        this.deity = Deity;
    }

    @Override
    public boolean matches(@NotNull RecipeInput RecipeInput, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput input, HolderLookup.@NotNull Provider registries) {
        return result.copy();
    }


    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return result.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.CHANT_CONVERSION_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.CHANT_CONVERSION_TYPE.get();
    }

    public JsonObject toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "eidolon_repraised:chant_conversion");
        jsonobject.add("input", Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, input).getOrThrow());
        jsonobject.addProperty("min_devotion", minDevotion);
        if (deity != null)
            jsonobject.addProperty("deity", deity.toString());

        jsonobject.add("output", ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, this.result).getOrThrow());

        return jsonobject;
    }

    public static class Serializer implements RecipeSerializer<ChantConversionRecipe> {

        public static MapCodec<ChantConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(
                c -> c.group(
                        Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                        ItemStack.CODEC.fieldOf("output").forGetter(r -> r.result),
                        Codec.FLOAT.fieldOf("min_devotion").forGetter(r -> r.minDevotion),
                        ResourceLocation.CODEC.optionalFieldOf("deity", null).forGetter(r -> r.deity)
                ).apply(c, ChantConversionRecipe::new)
        );

        public static StreamCodec<RegistryFriendlyByteBuf, ChantConversionRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                r -> r.input,
                ItemStack.STREAM_CODEC,
                r -> r.result,
                ByteBufCodecs.FLOAT,
                r -> r.minDevotion,
                ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional),
                r -> Optional.ofNullable(r.deity),
                (input, result, minDevotion, deity) -> new ChantConversionRecipe(input, result, minDevotion, deity.orElse(null))
        );

        @Override
        public @NotNull MapCodec<ChantConversionRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ChantConversionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
