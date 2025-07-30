package elucent.eidolon.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.ritual.ExecCommandRitual;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.Signs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandRitualRecipe extends RitualRecipe {

    List<String> commands;
    ResourceLocation symbol = Signs.HARMONY_SIGN.sprite();
    int color = 0;

    private CommandRitualRecipe setSymbol(ResourceLocation symbol) {
        this.symbol = symbol;
        return this;
    }

    private CommandRitualRecipe setColor(int color) {
        this.color = color;
        return this;
    }

    public List<String> getCommands() {
        return commands;
    }

    public CommandRitualRecipe(List<String> commands, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, float healthRequirement) {
        super(reagent, pedestalItems, focusItems, healthRequirement);
        this.commands = commands;
    }


    @Override
    public Ritual getRitual() {
        return new ExecCommandRitual(symbol, color, commands).setRegistryName(getId());
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath("eidolon", "ritual_exec_command_" + commands.hashCode());
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

        public static final MapCodec<CommandRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.listOf().fieldOf("commands").forGetter(CommandRitualRecipe::getCommands),
                        Ingredient.CODEC.fieldOf("reagent").forGetter(CommandRitualRecipe::getReagent),
                        Ingredient.CODEC.listOf().fieldOf("pedestal_items").forGetter(CommandRitualRecipe::getPedestalItems),
                        Ingredient.CODEC.listOf().fieldOf("focus_items").forGetter(CommandRitualRecipe::getFocusItems),
                        Codec.FLOAT.fieldOf("health_requirement").orElse(0.0f).forGetter(CommandRitualRecipe::getHealthRequirement)
                ).apply(instance, CommandRitualRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CommandRitualRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
                        CommandRitualRecipe::getCommands,
                        Ingredient.CONTENTS_STREAM_CODEC,
                        CommandRitualRecipe::getReagent,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                        CommandRitualRecipe::getPedestalItems,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                        CommandRitualRecipe::getFocusItems,
                        ByteBufCodecs.FLOAT,
                        CommandRitualRecipe::getHealthRequirement,
                        CommandRitualRecipe::new
                );

        @Override
        public @NotNull MapCodec<CommandRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CommandRitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}