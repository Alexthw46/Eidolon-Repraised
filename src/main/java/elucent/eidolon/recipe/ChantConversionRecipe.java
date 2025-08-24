package elucent.eidolon.recipe;

import com.google.gson.JsonObject;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.util.RegistryUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChantConversionRecipe implements Recipe<Container> {

    ResourceLocation id;
    public Ingredient input;
    ItemStack result;
    public float minDevotion;
    public @Nullable ResourceLocation deity;

    public ChantConversionRecipe(ResourceLocation id, Ingredient input, ItemStack result, float minDevotion, @Nullable ResourceLocation Deity) {
        this.id = id;
        this.input = input;
        this.result = result;
        this.minDevotion = minDevotion;
        this.deity = Deity;
    }

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
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
        jsonobject.addProperty("type", "eidolon:chant_conversion");
        jsonobject.add("input", input.toJson());
        jsonobject.addProperty("min_devotion", minDevotion);
        if (deity != null)
            jsonobject.addProperty("deity", deity.toString());

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", RegistryUtil.getRegistryName(result.getItem()).toString());
        int count = result.getCount();
        if (count > 1) {
            resultObj.addProperty("count", count);
        }
        jsonobject.add("output", resultObj);

        return jsonobject;
    }

    public static class Serializer implements RecipeSerializer<ChantConversionRecipe> {

        @Override
        public @NotNull ChantConversionRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            float minDevotion = GsonHelper.getAsFloat(pSerializedRecipe, "min_devotion", 0);
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            ResourceLocation deity = pSerializedRecipe.has("deity") ? new ResourceLocation(GsonHelper.getAsString(pSerializedRecipe, "deity")) : null;
            return new ChantConversionRecipe(pRecipeId, input, output, minDevotion, deity);
        }

        @Override
        public @Nullable ChantConversionRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, @NotNull FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            ItemStack output = pBuffer.readItem();
            float minDevotion = pBuffer.readFloat();
            ResourceLocation deity = pBuffer.readBoolean() ? pBuffer.readResourceLocation() : null;
            return new ChantConversionRecipe(pRecipeId, input, output, minDevotion, deity);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, @NotNull ChantConversionRecipe pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeFloat(pRecipe.minDevotion);
            pBuffer.writeBoolean(pRecipe.deity != null);
            if (pRecipe.deity != null)
                pBuffer.writeResourceLocation(pRecipe.deity);
        }
    }
}
