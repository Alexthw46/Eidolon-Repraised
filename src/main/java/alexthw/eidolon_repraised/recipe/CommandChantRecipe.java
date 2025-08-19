package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.api.spells.Spell;
import alexthw.eidolon_repraised.common.spell.ExecCommandSpell;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandChantRecipe extends ChantRecipe {

    List<String> commands;
    int manaCost = 0;

    public CommandChantRecipe(ResourceLocation id, List<Sign> signs, List<String> commands, int manaCost) {
        super(id, signs);
        this.commands = commands;
        this.manaCost = manaCost;
    }

    public int manaCost() {
        return manaCost;
    }

    public List<String> getCommands() {
        return commands;
    }

    @Override
    public Spell getChant() {
        return new ExecCommandSpell(id, manaCost, signs().toArray(new Sign[0]), getCommands());
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

        public static final MapCodec<CommandChantRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(CommandChantRecipe::getId),
                Sign.CODEC.listOf().fieldOf("signs").forGetter(recipe -> recipe.signs),
                Codec.STRING.listOf().fieldOf("commands").forGetter(CommandChantRecipe::getCommands),
                Codec.INT.fieldOf("mana_cost").forGetter(CommandChantRecipe::manaCost)
        ).apply(instance, CommandChantRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CommandChantRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                CommandChantRecipe::getId,
                Sign.STREAM_CODEC.apply(ByteBufCodecs.list()),
                CommandChantRecipe::signs,
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
                CommandChantRecipe::getCommands,
                ByteBufCodecs.INT,
                CommandChantRecipe::manaCost,
                CommandChantRecipe::new
        );

        @Override
        public @NotNull MapCodec<CommandChantRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CommandChantRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
