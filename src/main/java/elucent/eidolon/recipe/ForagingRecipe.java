package elucent.eidolon.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.util.RegistryUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ForagingRecipe implements Recipe<CraftingInput> {

    public ResourceLocation id;
    public ItemStack result;
    public Ingredient block;

    public ForagingRecipe(ResourceLocation id, ItemStack output, Ingredient block) {
        this.id = id;
        this.result = output;
        this.block = block;
    }

    public ForagingRecipe(ItemStack output, Ingredient block) {
        this.result = output;
        this.block = block;
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return result.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.FORAGING_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.FORAGING_TYPE.get();
    }

    public JsonObject toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "eidolon:athame_foraging");
        //TODO: restore block serialization
        //jsonobject.add("block", block.toJson());
        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", RegistryUtil.getRegistryName(result.getItem()).toString());
        int count = result.getCount();
        if (count > 1) {
            resultObj.addProperty("count", count);
        }
        jsonobject.add("output", resultObj);
        return jsonobject;
    }

    public static class Serializer implements RecipeSerializer<ForagingRecipe> {

        public static final MapCodec<ForagingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.result),
                Ingredient.CODEC.fieldOf("block").forGetter(r -> r.block)
        ).apply(instance, ForagingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ForagingRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, r -> r.result,
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.block,
                ForagingRecipe::new
        );

        @Override
        public @NotNull MapCodec<ForagingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ForagingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
