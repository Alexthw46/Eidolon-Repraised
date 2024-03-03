package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EidolonRecipes {

    static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Eidolon.MODID);
    public static final RegistryObject<RecipeSerializer<DyeRecipe>> DYE_RECIPE = RECIPE_SERIALIZERS.register("dye", DyeRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<CrucibleRecipe>> CRUCIBLE_RECIPE = RECIPE_SERIALIZERS.register("crucible", CrucibleRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<WorktableRecipe>> WORKTABLE_RECIPE = RECIPE_SERIALIZERS.register("worktable", WorktableRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<ItemRitualRecipe>> CRAFTING_RITUAL_RECIPE = RECIPE_SERIALIZERS.register("ritual_brazier_crafting", ItemRitualRecipe.SerializerCrafting::new);
    public static final RegistryObject<RecipeSerializer<SummonRitualRecipe>> SUMMON_RITUAL_RECIPE = RECIPE_SERIALIZERS.register("ritual_brazier_summoning", SummonRitualRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<CommandRitualRecipe>> COMMAND_RITUAL_RECIPE = RECIPE_SERIALIZERS.register("ritual_brazier_command", CommandRitualRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<GenericRitualRecipe>> RITUAL_RECIPE = RECIPE_SERIALIZERS.register("ritual_brazier", GenericRitualRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<LocationRitualRecipe>> LOCATION_RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register("ritual_brazier_location", LocationRitualRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<ChantRecipe>> CHANT_SERIALIZER = RECIPE_SERIALIZERS.register("chant", ChantRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<ForagingRecipe>> FORAGING_RECIPE = RECIPE_SERIALIZERS.register("athame_foraging", ForagingRecipe.Serializer::new);

    static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Eidolon.MODID);

    public static final RegistryObject<RecipeType<DyeRecipe>> DYE_TYPE = RECIPE_TYPES.register("dye", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:dye";
        }
    });

    public static final RegistryObject<RecipeType<CrucibleRecipe>> CRUCIBLE_TYPE = RECIPE_TYPES.register("crucible", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:crucible";
        }
    });

    public static final RegistryObject<RecipeType<WorktableRecipe>> WORKTABLE_TYPE = RECIPE_TYPES.register("worktable", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:worktable";
        }
    });

    public static final RegistryObject<RecipeType<ItemRitualRecipe>> CRAFTING_RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier_crafting", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:ritual_brazier_crafting";
        }
    });

    public static final RegistryObject<RecipeType<SummonRitualRecipe>> SUMMON_RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier_summoning", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:ritual_brazier_summoning";
        }
    });

    public static final RegistryObject<RecipeType<GenericRitualRecipe>> COMMAND_RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier_command", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:ritual_brazier_command";
        }
    });
    public static final RegistryObject<RecipeType<LocationRitualRecipe>> LOCATION_RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier_location", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:ritual_brazier_location";
        }
    });
    public static final RegistryObject<RecipeType<GenericRitualRecipe>> RITUAL_TYPE = RECIPE_TYPES.register("ritual_brazier", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:ritual_brazier";
        }
    });
    public static final RegistryObject<RecipeType<ChantRecipe>> CHANT_TYPE = RECIPE_TYPES.register("chant", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:chant";
        }
    });
    public static final RegistryObject<RecipeType<ForagingRecipe>> FORAGING_TYPE = RECIPE_TYPES.register("athame_foraging", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "eidolon:athame_foraging";
        }
    });

    public static List<RecipeType<? extends RitualRecipe>> ritualRecipeTypes = new CopyOnWriteArrayList<>();
}
