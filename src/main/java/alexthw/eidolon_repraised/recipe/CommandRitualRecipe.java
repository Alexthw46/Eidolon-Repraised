package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.common.ritual.ExecCommandRitual;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.ColorUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public ResourceLocation getSymbol() {
        return symbol;
    }

    public int getColor() {
        return color;
    }

    public List<String> getCommands() {
        return commands;
    }

    public CommandRitualRecipe(List<String> commands, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, ResourceLocation symbol, int color, float healthRequirement) {
        super(reagent, pedestalItems, focusItems, healthRequirement);
        this.commands = commands;
        this.symbol = symbol;
        this.color = color;
    }


    @Override
    public Ritual getRitual() {
        return new ExecCommandRitual(symbol, color, commands).setRegistryName(getId());
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "ritual_exec_command_" + commands.hashCode());
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
                        ResourceLocation.CODEC.optionalFieldOf("symbol", Signs.HARMONY_SIGN.sprite()).forGetter(CommandRitualRecipe::getSymbol),
                        Codec.INT.optionalFieldOf("color", ColorUtil.packColor(255, 255, 255, 255)).forGetter(CommandRitualRecipe::getColor),
                        Codec.FLOAT.fieldOf("health_requirement").orElse(0.0f).forGetter(CommandRitualRecipe::getHealthRequirement)
                ).apply(instance, CommandRitualRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CommandRitualRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())
                                    .encode(buf, recipe.getCommands());

                            Ingredient.CONTENTS_STREAM_CODEC
                                    .encode(buf, recipe.getReagent());

                            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list())
                                    .encode(buf, recipe.getPedestalItems());

                            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list())
                                    .encode(buf, recipe.getFocusItems());

                            ResourceLocation.STREAM_CODEC
                                    .encode(buf, recipe.getSymbol());

                            ByteBufCodecs.INT
                                    .encode(buf, recipe.getColor());

                            ByteBufCodecs.FLOAT
                                    .encode(buf, recipe.getHealthRequirement());
                        },
                        buf -> new CommandRitualRecipe(
                                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())
                                        .decode(buf),
                                Ingredient.CONTENTS_STREAM_CODEC
                                        .decode(buf),
                                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list())
                                        .decode(buf),
                                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list())
                                        .decode(buf),
                                ResourceLocation.STREAM_CODEC
                                        .decode(buf),
                                ByteBufCodecs.INT
                                        .decode(buf),
                                ByteBufCodecs.FLOAT
                                        .decode(buf)
                        )
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