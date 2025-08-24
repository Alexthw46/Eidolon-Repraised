package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EidolonRecipes {

    static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Eidolon.MODID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DyeRecipe>> DYE_RECIPE = RECIPE_SERIALIZERS.register("dye", () -> ExtendableShapelessSerializer.create(DyeRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrucibleRecipe>> CRUCIBLE_RECIPE = RECIPE_SERIALIZERS.register("crucible", CrucibleRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WorktableRecipe>> WORKTABLE_RECIPE = RECIPE_SERIALIZERS.register("worktable", WorktableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ItemRitualRecipe>> CRAFTING_RITUAL_RECIPE = RECIPE_SERIALIZERS.register("ritual_brazier_crafting", ItemRitualRecipe.SerializerCrafting::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SummonRitualRecipe>> SUMMON_RITUAL_RECIPE = RECIPE_SERIALIZERS.register("ritual_brazier_summoning", SummonRitualRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CommandRitualRecipe>> COMMAND_RITUAL_RECIPE = RECIPE_SERIALIZERS.register("ritual_brazier_command", CommandRitualRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GenericRitualRecipe>> RITUAL_RECIPE = RECIPE_SERIALIZERS.register("ritual_brazier", GenericRitualRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LocationRitualRecipe>> LOCATION_RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register("ritual_brazier_location", LocationRitualRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChantRecipe>> CHANT_SERIALIZER = RECIPE_SERIALIZERS.register("chant", ChantRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CommandChantRecipe>> COMMAND_CHANT_SERIALIZER = RECIPE_SERIALIZERS.register("command_chant", CommandChantRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForagingRecipe>> FORAGING_RECIPE = RECIPE_SERIALIZERS.register("athame_foraging", ForagingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChantConversionRecipe>> CHANT_CONVERSION_SERIALIZER = RECIPE_SERIALIZERS.register("chant_conversion", ChantConversionRecipe.Serializer::new);

    static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Eidolon.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DyeRecipe>> DYE_TYPE = RECIPE_TYPES.register("dye", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:dye";
        }
    });

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrucibleRecipe>> CRUCIBLE_TYPE = RECIPE_TYPES.register("crucible", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:crucible";
        }
    });

    public static final DeferredHolder<RecipeType<?>, RecipeType<WorktableRecipe>> WORKTABLE_TYPE = RECIPE_TYPES.register("worktable", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:worktable";
        }
    });

    public static final DeferredHolder<RecipeType<?>, RecipeType<ItemRitualRecipe>> CRAFTING_RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier_crafting", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:ritual_brazier_crafting";
        }
    });

    public static final DeferredHolder<RecipeType<?>, RecipeType<SummonRitualRecipe>> SUMMON_RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier_summoning", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:ritual_brazier_summoning";
        }
    });

    public static final DeferredHolder<RecipeType<?>, RecipeType<GenericRitualRecipe>> COMMAND_RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier_command", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:ritual_brazier_command";
        }
    });
    public static final DeferredHolder<RecipeType<?>, RecipeType<LocationRitualRecipe>> LOCATION_RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier_location", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:ritual_brazier_location";
        }
    });
    public static final DeferredHolder<RecipeType<?>, RecipeType<GenericRitualRecipe>> RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:ritual_brazier";
        }
    });
    public static final DeferredHolder<RecipeType<?>, RecipeType<ChantRecipe>> CHANT_TYPE = RECIPE_TYPES.register("chant", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:chant";
        }
    });
    public static final DeferredHolder<RecipeType<?>, RecipeType<CommandChantRecipe>> COMMAND_CHANT_TYPE = RECIPE_TYPES.register("command_chant", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:command_chant";
        }
    });

    public static final DeferredHolder<RecipeType<?>, RecipeType<ChantConversionRecipe>> CHANT_CONVERSION_TYPE = RECIPE_TYPES.register("chant_conversion", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:chant_conversion";
        }
    });

    public static final DeferredHolder<RecipeType<?>, RecipeType<ForagingRecipe>> FORAGING_TYPE = RECIPE_TYPES.register("athame_foraging", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon_repraised:athame_foraging";
        }
    });

    public static List<RecipeType<? extends RitualRecipe>> ritualRecipeTypes = new CopyOnWriteArrayList<>();
}
