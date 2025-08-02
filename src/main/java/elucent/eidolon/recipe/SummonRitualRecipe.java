package elucent.eidolon.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.ritual.SummonRitual;
import elucent.eidolon.registries.EidolonRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SummonRitualRecipe extends RitualRecipe {

    ResourceLocation entity;
    int count = 1;

    public SummonRitualRecipe(ResourceLocation result, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, int count, float healthRequirement) {
        super(reagent, pedestalItems, focusItems, healthRequirement);
        this.entity = result;
        this.count = count;
    }

    public SummonRitualRecipe(ResourceLocation result, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems) {
        super(reagent, pedestalItems, focusItems);
        this.entity = result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.SUMMON_RITUAL_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.SUMMON_RITUAL_TYPE.get();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(entity.getNamespace(), "summon_" + entity.getPath());
    }

    @Override
    public Ritual getRitual() {
        return new SummonRitual(BuiltInRegistries.ENTITY_TYPE.get(entity), count).setRegistryName(getId());
    }

    public ResourceLocation getEntityRL() {
        return entity;
    }

    public int getCount() {
        return count;
    }

    public static class Serializer extends RitualRecipe.Serializer<SummonRitualRecipe> {

        public static final MapCodec<SummonRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ResourceLocation.CODEC.fieldOf("output").forGetter(r -> r.entity),
                        Ingredient.CODEC.fieldOf("reagent").forGetter(r -> r.reagent),
                        Ingredient.CODEC.listOf().fieldOf("pedestal_items").forGetter(r -> r.pedestalItems),
                        Ingredient.CODEC.listOf().fieldOf("focus_items").forGetter(r -> r.focusItems),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count),
                        Codec.FLOAT.optionalFieldOf("healthRequirement", 0.0f).forGetter(r -> r.healthRequirement)
                ).apply(instance, SummonRitualRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SummonRitualRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                SummonRitualRecipe::getEntityRL,
                Ingredient.CONTENTS_STREAM_CODEC,
                SummonRitualRecipe::getReagent,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                SummonRitualRecipe::getPedestalItems,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                SummonRitualRecipe::getFocusItems,
                ByteBufCodecs.INT,
                SummonRitualRecipe::getCount,
                ByteBufCodecs.FLOAT,
                SummonRitualRecipe::getHealthRequirement,
                SummonRitualRecipe::new
        );

        @Override
        public @NotNull MapCodec<SummonRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SummonRitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
