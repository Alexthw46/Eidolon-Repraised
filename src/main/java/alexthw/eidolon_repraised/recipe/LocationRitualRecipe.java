package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.common.ritual.LocationRitual;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LocationRitualRecipe extends RitualRecipe {

    public ResourceLocation getStructureTagKey() {
        return structureTagKey;
    }

    ResourceLocation structureTagKey;


    public LocationRitualRecipe(ResourceLocation structure, Ingredient reagent, List<Ingredient> pedestalItems, List<Ingredient> focusItems, float healthRequirement) {
        super(reagent, pedestalItems, focusItems, healthRequirement);
        this.structureTagKey = structure;
    }


    @Override
    public Ritual getRitual() {
        return new LocationRitual(TagKey.create(Registries.STRUCTURE, structureTagKey)).setRegistryName(getId());
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(structureTagKey.getNamespace(), "ritual_locate_" + structureTagKey.getPath());
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.LOCATION_RITUAL_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.LOCATION_RITUAL_TYPE.get();
    }


    public static class Serializer extends RitualRecipe.Serializer<LocationRitualRecipe> {

        public static final MapCodec<LocationRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("structure").forGetter(recipe -> recipe.structureTagKey),
                Ingredient.CODEC.fieldOf("reagent").forGetter(LocationRitualRecipe::getReagent),
                Ingredient.CODEC.listOf().fieldOf("pedestal_items").forGetter(LocationRitualRecipe::getPedestalItems),
                Ingredient.CODEC.listOf().fieldOf("focus_items").forGetter(LocationRitualRecipe::getFocusItems),
                Codec.FLOAT.fieldOf("health_requirement").orElse(0f).forGetter(LocationRitualRecipe::getHealthRequirement)
        ).apply(instance, LocationRitualRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LocationRitualRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                LocationRitualRecipe::getStructureTagKey,
                Ingredient.CONTENTS_STREAM_CODEC,
                LocationRitualRecipe::getReagent,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                LocationRitualRecipe::getPedestalItems,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                LocationRitualRecipe::getFocusItems,
                ByteBufCodecs.FLOAT,
                LocationRitualRecipe::getHealthRequirement,
                LocationRitualRecipe::new
        );


        @Override
        public @NotNull MapCodec<LocationRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, LocationRitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
