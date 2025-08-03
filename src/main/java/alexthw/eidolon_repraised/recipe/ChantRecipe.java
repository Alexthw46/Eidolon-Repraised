package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.api.spells.SignSequence;
import alexthw.eidolon_repraised.api.spells.Spell;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.registries.Spells;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChantRecipe implements Recipe<RecipeInput> {

    ResourceLocation id;
    List<Sign> signs;

    public ChantRecipe(ResourceLocation id, List<Sign> signs) {
        this.id = id;
        this.signs = signs;
    }


    @Override
    public boolean matches(@NotNull RecipeInput container, @NotNull Level level) {
        return false;
    }


    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput container, @NotNull HolderLookup.Provider registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }


    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registryAccess) {
        return ItemStack.EMPTY;
    }

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

    public List<Sign> signs() {
        return signs;
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

        public static final MapCodec<ChantRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ChantRecipe::getId),
                Sign.CODEC.listOf().fieldOf("signs").forGetter(recipe -> recipe.signs)
        ).apply(instance, ChantRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ChantRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                ChantRecipe::getId,
                Sign.STREAM_CODEC.apply(ByteBufCodecs.list()),
                ChantRecipe::signs,
                ChantRecipe::new
        );


        public @NotNull ChantRecipe fromJson(@NotNull ResourceLocation resourceLocation, @NotNull JsonObject jsonObject) {
            //ResourceLocation chant = new ResourceLocation(jsonObject.get("chant").getAsString());
            JsonArray signsArray = jsonObject.getAsJsonArray("signs");
            List<Sign> signs = new ArrayList<>();
            for (var sign : signsArray) {
                signs.add(Signs.find(ResourceLocation.parse(sign.getAsString())));
            }
            return new ChantRecipe(resourceLocation, signs);
        }

        @Override
        public @NotNull MapCodec<ChantRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ChantRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }
}
