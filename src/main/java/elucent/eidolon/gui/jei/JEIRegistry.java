package elucent.eidolon.gui.jei;


import elucent.eidolon.Eidolon;
import elucent.eidolon.common.tile.BrazierTileEntity;
import elucent.eidolon.recipe.CrucibleRecipe;
import elucent.eidolon.recipe.DyeRecipe;
import elucent.eidolon.recipe.RitualRecipe;
import elucent.eidolon.recipe.WorktableRecipe;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.Registry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIRegistry implements IModPlugin {
    public static final RecipeType<CrucibleRecipe> CRUCIBLE_CATEGORY = RecipeType.create(Eidolon.MODID, "crucible", CrucibleRecipe.class);
    public static final RecipeType<WorktableRecipe> WORKTABLE_CATEGORY = RecipeType.create(Eidolon.MODID, "worktable", WorktableRecipe.class);
    public static final RecipeType<RitualRecipe> RITUAL_CATEGORY = RecipeType.create(Eidolon.MODID, "rituals", RitualRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(Eidolon.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(Registry.CRUCIBLE.get()), CRUCIBLE_CATEGORY);
        registry.addRecipeCatalyst(new ItemStack(Registry.WORKTABLE.get()), WORKTABLE_CATEGORY);
        registry.addRecipeCatalyst(new ItemStack(Registry.BRAZIER.get()), RITUAL_CATEGORY);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        var guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new CrucibleCategory(guiHelper),
                new WorktableCategory(guiHelper),
                new RitualCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {

        RecipeManager manager = Eidolon.proxy.getWorld().getRecipeManager();

        registry.addRecipes(CRUCIBLE_CATEGORY, manager.getAllRecipesFor(EidolonRecipes.CRUCIBLE_TYPE.get()));
        registry.addRecipes(WORKTABLE_CATEGORY, manager.getAllRecipesFor(EidolonRecipes.WORKTABLE_TYPE.get()));
        registry.addRecipes(RITUAL_CATEGORY, BrazierTileEntity.getRitualRecipes(Eidolon.proxy.getWorld()));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(DyeRecipe.class, DyeRecipeCategory::new);
    }

}