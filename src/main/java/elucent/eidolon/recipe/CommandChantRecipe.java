package elucent.eidolon.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.api.spells.Spell;
import elucent.eidolon.common.spell.ExecCommandSpell;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.Signs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandChantRecipe extends ChantRecipe {

    List<String> commands;
    int manaCost = 0;

    public CommandChantRecipe(ResourceLocation id, List<Sign> signs, List<String> commands, int manaCost) {
        super(id, signs);
        this.commands = commands;
        this.manaCost = manaCost;
    }

    public List<String> getCommands() {
        return commands;
    }

    @Override
    public Spell getChant() {
        return new ExecCommandSpell(id, manaCost, signs(), getCommands());
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.COMMAND_CHANT_TYPE.get();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.COMMAND_CHANT_SERIALIZER.get();
    }

    @Override
    public JsonObject toJson() {
        var base = super.toJson();
        JsonArray commandsJson = new JsonArray();
        for (String command : commands) {
            commandsJson.add(command);
        }
        base.add("commands", commandsJson);
        base.addProperty("mana_cost", manaCost);
        return base;
    }

    public static class Serializer implements RecipeSerializer<CommandChantRecipe> {

        @Override
        public @NotNull CommandChantRecipe fromJson(@NotNull ResourceLocation resourceLocation, @NotNull JsonObject jsonObject) {
            //ResourceLocation chant = new ResourceLocation(jsonObject.get("chant").getAsString());
            JsonArray signsArray = jsonObject.getAsJsonArray("signs");
            List<Sign> signs = new ArrayList<>();
            for (var sign : signsArray) {
                signs.add(Signs.find(new ResourceLocation(sign.getAsString())));
            }
            JsonArray commandsArray = jsonObject.getAsJsonArray("commands");
            List<String> commands = new ArrayList<>();
            for (var command : commandsArray) {
                commands.add(command.getAsString());
            }
            int manaCost = jsonObject.has("mana_cost") ? jsonObject.get("mana_cost").getAsInt() : 0;
            return new CommandChantRecipe(resourceLocation, signs, commands, manaCost);
        }

        @Override
        public @Nullable CommandChantRecipe fromNetwork(@NotNull ResourceLocation resourceLocation, @NotNull FriendlyByteBuf friendlyByteBuf) {
            //ResourceLocation chant = friendlyByteBuf.readResourceLocation();
            int n = friendlyByteBuf.readInt();
            List<Sign> signs = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                signs.add(Signs.find(friendlyByteBuf.readResourceLocation()));
            }
            int commandCount = friendlyByteBuf.readInt();
            List<String> commands = new ArrayList<>();
            for (int i = 0; i < commandCount; i++) {
                commands.add(friendlyByteBuf.readUtf()); // Read a UTF-8 string with a max length of 32767
            }
            int manaCost = friendlyByteBuf.readInt();
            return new CommandChantRecipe(resourceLocation, signs, commands, manaCost);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf friendlyByteBuf, @NotNull CommandChantRecipe chantRecipe) {
            //friendlyByteBuf.writeResourceLocation(chantRecipe.chantId);
            friendlyByteBuf.writeInt(chantRecipe.signs.size());
            for (Sign sign : chantRecipe.signs) {
                friendlyByteBuf.writeResourceLocation(sign.getRegistryName());
            }
            friendlyByteBuf.writeInt(chantRecipe.commands.size());
            for (String command : chantRecipe.commands) {
                friendlyByteBuf.writeUtf(command); // Write a UTF-8 string with a max length of 32767
            }
            friendlyByteBuf.writeInt(chantRecipe.manaCost);
        }
    }


}
