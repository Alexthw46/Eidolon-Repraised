package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.api.spells.Spell;
import elucent.eidolon.common.spell.BaseConversionSpell;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.Signs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/*
 Recipe for chants that convert items, without requiring devotion to a specific deity. For the conversion recipes, see ChantConversionRecipe.
*/
public class BaseConversionChantRecipe extends ChantRecipe {
    int manaCost = 0;

    public BaseConversionChantRecipe(ResourceLocation id, List<Sign> signs, int manaCost) {
        super(id, signs);
        this.manaCost = manaCost;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.CONVERSION_CHANT_TYPE.get();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.CONVERSION_CHANT_SERIALIZER.get();
    }

    @Override
    public Spell getChant() {
        return new BaseConversionSpell(id, manaCost, signs());
    }

    public static class Serializer implements RecipeSerializer<BaseConversionChantRecipe> {

        @Override
        public @NotNull BaseConversionChantRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            JsonArray signsArray = json.getAsJsonArray("signs");
            List<Sign> signs = new ArrayList<>();
            for (var sign : signsArray) {
                signs.add(Signs.find(new ResourceLocation(sign.getAsString())));
            }
            int manaCost = json.has("mana_cost") ? json.get("mana_cost").getAsInt() : 0;
            return new BaseConversionChantRecipe(recipeId, signs, manaCost);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull BaseConversionChantRecipe recipe) {
            buffer.writeInt(recipe.signs.size());
            for (Sign sign : recipe.signs) {
                buffer.writeResourceLocation(sign.getRegistryName());
            }
            buffer.writeInt(recipe.manaCost);
        }

        @Override
        public BaseConversionChantRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            int n = buffer.readInt();
            List<Sign> signs = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                signs.add(Signs.find(buffer.readResourceLocation()));
            }
            int manaCost = buffer.readInt();
            return new BaseConversionChantRecipe(recipeId, signs, manaCost);
        }
    }
}
