package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.ritual.ExecCommandRitual;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.Signs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandRitualRecipe extends RitualRecipe {

    List<String> commands;
    ResourceLocation symbol = Signs.HARMONY_SIGN.getSprite();
    int color = 0;

    private CommandRitualRecipe setSymbol(ResourceLocation symbol) {
        this.symbol = symbol;
        return this;
    }

    private CommandRitualRecipe setColor(int color) {
        this.color = color;
        return this;
    }

    public CommandRitualRecipe(ResourceLocation recipeId, List<String> commands, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, float healthRequirement) {
        super(recipeId, reagent, pedestalItems, focusItems, healthRequirement);
        this.commands = commands;
    }


    @Override
    public Ritual getRitual() {
        return new ExecCommandRitual(symbol, color, commands).setRegistryName(id);
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "eidolon:ritual_brazier_command");
        JsonArray commandsJson = new JsonArray();
        for (String command : this.commands) {
            commandsJson.add(command);
        }
        jsonobject.add("commands", commandsJson);
        jsonobject.addProperty("symbol", this.symbol.toString());
        jsonobject.addProperty("color", this.color);
        addRitualElements(this, jsonobject);

        return jsonobject;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.COMMAND_RITUAL_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.COMMAND_RITUAL_TYPE.get();
    }

    public static class Serializer extends RitualRecipe.Serializer<CommandRitualRecipe> {

        @Override
        public @NotNull CommandRitualRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject json) {
            Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "reagent"));
            float healthRequirement = json.has("healthRequirement") ? GsonHelper.getAsFloat(json, "healthRequirement") : 0;
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = getPedestalItems(pedestalItems);
            JsonArray focusItems = GsonHelper.getAsJsonArray(json, "focusItems");
            List<Ingredient> foci = getPedestalItems(focusItems);

            List<String> commands = new ArrayList<>();
            if (json.has("commands")) {
                JsonArray commandsJson = GsonHelper.getAsJsonArray(json, "commands");
                for (JsonElement element : commandsJson) {
                    commands.add(element.getAsString());
                }
            } else if (json.has("command")) {
                commands.add(GsonHelper.getAsString(json, "command"));
            }

            ResourceLocation symbol = json.has("symbol") ? ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json,"symbol" )) : Signs.HARMONY_SIGN.getSprite();
            int color = GsonHelper.getAsInt(json, "color");

            return new CommandRitualRecipe(pRecipeId, commands, reagent, stacks, foci, healthRequirement).setSymbol(symbol).setColor(color);
        }

        @Override
        public @Nullable CommandRitualRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, @NotNull FriendlyByteBuf pBuffer) {
            int length = pBuffer.readInt();
            int length2 = pBuffer.readInt();
            Ingredient reagent = Ingredient.fromNetwork(pBuffer);
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    stacks.add(Ingredient.fromNetwork(pBuffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            List<Ingredient> foci = new ArrayList<>();
            for (int i = 0; i < length2; i++) {
                try {
                    foci.add(Ingredient.fromNetwork(pBuffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            float healthRequirement = pBuffer.readFloat();

            int commandsLength = pBuffer.readInt();
            List<String> commands = new ArrayList<>();
            for (int i = 0; i < commandsLength; i++) {
                commands.add(pBuffer.readUtf());
            }

            ResourceLocation symbol = pBuffer.readResourceLocation();
            int color = pBuffer.readInt();

            return new CommandRitualRecipe(pRecipeId, commands, reagent, stacks, foci, healthRequirement).setSymbol(symbol).setColor(color);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull CommandRitualRecipe recipe) {
            super.toNetwork(buf, recipe);
            buf.writeInt(recipe.commands.size());
            for (String command : recipe.commands) {
                buf.writeUtf(command);
            }
            buf.writeResourceLocation(recipe.symbol);
            buf.writeInt(recipe.color);
        }


    }

}