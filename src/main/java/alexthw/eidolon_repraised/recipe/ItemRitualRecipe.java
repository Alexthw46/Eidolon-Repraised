package alexthw.eidolon_repraised.recipe;

import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.common.ritual.CraftingRitual;
import alexthw.eidolon_repraised.common.tile.BrazierTileEntity;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import alexthw.eidolon_repraised.util.RegistryUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
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

public class ItemRitualRecipe extends RitualRecipe {

    public ItemStack result; // Result item

    public boolean keepsComponent() {
        return keepNbtOfReagent;
    }

    public boolean keepNbtOfReagent = false;

    public ResourceLocation symbol;
    public int color;

    public ItemRitualRecipe(Ingredient reagent, List<Ingredient> pedestals, List<Ingredient> foci, ItemStack output, boolean keepNbtOfReagent, float healthRequirement) {
        super(reagent, pedestals, foci, healthRequirement);
        this.result = output;
        this.keepNbtOfReagent = keepNbtOfReagent;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull BrazierTileEntity inv, @NotNull HolderLookup.Provider registryAccess) {
        ItemStack result = this.result.copy();
        if (keepNbtOfReagent && !inv.getStack().isComponentsPatchEmpty()) {
            result.applyComponents(inv.getStack().getComponentsPatch());
            result.setDamageValue(0);
        }
        return result.copy();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider access) {
        return result == null ? ItemStack.EMPTY : result.copy();
    }

    public @NotNull ItemStack getResult() {
        return result;
    }

    @Override
    public Ritual getRitual() {
        return (symbol != null && color != 0 ? new CraftingRitual(symbol, color, result, keepNbtOfReagent) : new CraftingRitual.SanguineRitual(result)).setRegistryName(getId());
    }

    @Override
    public @NotNull ResourceLocation getId() {
        ResourceLocation itemId = RegistryUtil.getRegistryName(result.getItem());
        if (symbol != null && color != 0)
            return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "sanguine_" + itemId.getPath());
        return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "brazier_craft_" + itemId.getPath());
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EidolonRecipes.CRAFTING_RITUAL_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EidolonRecipes.CRAFTING_RITUAL_TYPE.get();
    }

    public static class SerializerCrafting extends RitualRecipe.Serializer<ItemRitualRecipe> {

        public static final MapCodec<ItemRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Ingredient.CODEC.fieldOf("reagent").forGetter(ItemRitualRecipe::getReagent),
                        Ingredient.CODEC.listOf().fieldOf("pedestal_items").forGetter(ItemRitualRecipe::getPedestalItems),
                        Ingredient.CODEC.listOf().fieldOf("focus_items").forGetter(ItemRitualRecipe::getFocusItems),
                        ItemStack.CODEC.fieldOf("result").forGetter(ItemRitualRecipe::getResult),
                        Codec.BOOL.optionalFieldOf("keep_nbt_of_reagent", false).forGetter(ItemRitualRecipe::keepsComponent),
                        Codec.FLOAT.optionalFieldOf("health_requirement", 0f).forGetter(ItemRitualRecipe::getHealthRequirement)
                ).apply(instance, ItemRitualRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemRitualRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                ItemRitualRecipe::getReagent,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                ItemRitualRecipe::getPedestalItems,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                ItemRitualRecipe::getFocusItems,
                ItemStack.STREAM_CODEC,
                ItemRitualRecipe::getResult,
                ByteBufCodecs.BOOL,
                ItemRitualRecipe::keepsComponent,
                ByteBufCodecs.FLOAT,
                ItemRitualRecipe::getHealthRequirement,
                ItemRitualRecipe::new
        );

        @Override
        public @NotNull MapCodec<ItemRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ItemRitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
