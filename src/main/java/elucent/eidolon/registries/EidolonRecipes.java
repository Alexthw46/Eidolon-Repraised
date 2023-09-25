package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.recipe.CrucibleRecipe;
import elucent.eidolon.recipe.DyeRecipe;
import elucent.eidolon.recipe.WorktableRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EidolonRecipes {

    static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Eidolon.MODID);
    public static final RegistryObject<RecipeSerializer<DyeRecipe>> DYE_RECIPE = RECIPE_SERIALIZERS.register("dye", DyeRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<CrucibleRecipe>> CRUCIBLE_RECIPE = RECIPE_SERIALIZERS.register("crucible", CrucibleRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<WorktableRecipe>> WORKTABLE_RECIPE = RECIPE_SERIALIZERS.register("worktable", WorktableRecipe.Serializer::new);
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


}
