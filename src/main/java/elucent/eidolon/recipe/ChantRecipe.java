package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.api.spells.SignSequence;
import elucent.eidolon.api.spells.Spell;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.registries.Spells;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChantRecipe implements Recipe<Container> {

    ResourceLocation id;
    List<Sign> signs;

    public ChantRecipe(ResourceLocation id, List<Sign> signs) {
        this.id = id;
        this.signs = signs;
    }


    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.CHANT_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.CHANT_TYPE.get();
    }

    public Spell getChant() {
        return Spells.find(id);
    }

    public boolean matches(@NotNull SignSequence signs) {
        return signs.equals(new SignSequence(this.signs));
    }

    public Sign[] signs() {
        return signs.toArray(new Sign[0]);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        //json.addProperty("chant", chantId.toString());
        json.addProperty("type", getType().toString());
        JsonArray signsArray = new JsonArray();
        for (Sign sign : signs) {
            signsArray.add(sign.getRegistryName().toString());
        }
        json.add("signs", signsArray);
        return json;
    }

    public static class Serializer implements RecipeSerializer<ChantRecipe> {

        @Override
        public @NotNull ChantRecipe fromJson(@NotNull ResourceLocation resourceLocation, @NotNull JsonObject jsonObject) {
            //ResourceLocation chant = new ResourceLocation(jsonObject.get("chant").getAsString());
            JsonArray signsArray = jsonObject.getAsJsonArray("signs");
            List<Sign> signs = new ArrayList<>();
            for (var sign : signsArray) {
                signs.add(Signs.find(new ResourceLocation(sign.getAsString())));
            }
            return new ChantRecipe(resourceLocation, signs);
        }

        @Override
        public @Nullable ChantRecipe fromNetwork(@NotNull ResourceLocation resourceLocation, @NotNull FriendlyByteBuf friendlyByteBuf) {
            //ResourceLocation chant = friendlyByteBuf.readResourceLocation();
            int n = friendlyByteBuf.readInt();
            List<Sign> signs = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                signs.add(Signs.find(friendlyByteBuf.readResourceLocation()));
            }
            return new ChantRecipe(resourceLocation, signs);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf friendlyByteBuf, @NotNull ChantRecipe chantRecipe) {
            //friendlyByteBuf.writeResourceLocation(chantRecipe.chantId);
            friendlyByteBuf.writeVarInt(chantRecipe.signs.size());
            for (Sign sign : chantRecipe.signs) {
                friendlyByteBuf.writeResourceLocation(sign.getRegistryName());
            }
        }
    }
}
