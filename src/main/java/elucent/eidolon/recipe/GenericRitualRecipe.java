package elucent.eidolon.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import elucent.eidolon.api.ritual.FocusItemPresentRequirement;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.RitualRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class GenericRitualRecipe extends RitualRecipe {

    public @NotNull ResourceLocation getId() {
        return ritualRL;
    }

    ResourceLocation ritualRL;

    public GenericRitualRecipe(ResourceLocation ritualRL, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, List<Ingredient> invariants, float healthRequirement) {
        super(reagent, pedestalItems, focusItems, invariants, healthRequirement);
        this.ritualRL = ritualRL;
    }

    @Override
    public Ritual getRitualWithRequirements() {
        Ritual ritual = super.getRitualWithRequirements();
        if (!invariantItems.isEmpty())
            ritual.addInvariants(invariantItems.stream().map(FocusItemPresentRequirement::new).collect(Collectors.toList()));
        return ritual;
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, List<ItemStack> focusItems, ItemStack reagent) {
        //do not count invariants
        if (!this.invariantItems.isEmpty())
            focusItems.removeIf(i -> invariantItems.stream().anyMatch(ing -> ing.test(i)));
        return super.isMatch(pedestalItems, focusItems, reagent);
    }

    @Override
    public Ritual getRitual() {
        return RitualRegistry.find(ritualRL);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.RITUAL_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.RITUAL_TYPE.get();
    }

    public static class Serializer extends RitualRecipe.Serializer<GenericRitualRecipe> {

        public static final MapCodec<GenericRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("ritual").forGetter(recipe -> recipe.ritualRL),
                Ingredient.CODEC.fieldOf("reagent").forGetter(recipe -> recipe.reagent),
                Ingredient.CODEC.listOf().fieldOf("pedestal_items").forGetter(recipe -> recipe.pedestalItems),
                Ingredient.CODEC.listOf().fieldOf("focus_items").forGetter(recipe -> recipe.focusItems),
                Ingredient.CODEC.listOf().fieldOf("invariant_items").forGetter(recipe -> recipe.invariantItems),
                Codec.FLOAT.fieldOf("health_requirement").orElse(0f).forGetter(recipe -> recipe.healthRequirement)
        ).apply(instance, GenericRitualRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GenericRitualRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                GenericRitualRecipe::getId,
                Ingredient.CONTENTS_STREAM_CODEC,
                GenericRitualRecipe::getReagent,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                GenericRitualRecipe::getPedestalItems,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                GenericRitualRecipe::getFocusItems,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                GenericRitualRecipe::getInvariantItems,
                ByteBufCodecs.FLOAT,
                GenericRitualRecipe::getHealthRequirement,
                GenericRitualRecipe::new
        );



        @Override
        public @NotNull MapCodec<GenericRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, GenericRitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
