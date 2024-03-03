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

public class ForagingRecipe implements Recipe<Container> {

    public ResourceLocation id;
    public ItemStack result;
    public Ingredient block;

    public ForagingRecipe(ResourceLocation id, ItemStack output, Ingredient block) {
        this.id = id;
        this.result = output;
        this.block = block;
    }


    @Override
    public boolean matches(@NotNull Container pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container pContainer, @NotNull RegistryAccess pRegistryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
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
        jsonobject.add("block", block.toJson());
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

        @Override
        public @NotNull ForagingRecipe fromJson(@NotNull ResourceLocation pId, @NotNull JsonObject pJson) {

            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "output"));
            Ingredient block = Ingredient.fromJson(pJson.get("block"));

            return new ForagingRecipe(pId, output, block);
        }

        @Override
        public ForagingRecipe fromNetwork(@NotNull ResourceLocation pId, @NotNull FriendlyByteBuf pBuffer) {
            return new ForagingRecipe(pId, pBuffer.readItem(), Ingredient.fromNetwork(pBuffer));
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, @NotNull ForagingRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.result);
            pRecipe.block.toNetwork(pBuffer);
        }
    }
}
