package elucent.eidolon.gui.jei;


import elucent.eidolon.Eidolon;
import elucent.eidolon.recipe.CrucibleRecipe;
import elucent.eidolon.recipe.DyeRecipe;
import elucent.eidolon.recipe.WorktableRecipe;
import elucent.eidolon.registries.Registry;
import elucent.eidolon.ritual.RitualRegistry;
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
    public static final RecipeType<RecipeWrappers.RitualRecipe> RITUAL_CATEGORY = RecipeType.create(Eidolon.MODID, "rituals", RecipeWrappers.RitualRecipe.class);

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

        registry.addRecipes(CRUCIBLE_CATEGORY, manager.getAllRecipesFor(CrucibleRecipe.Type.INSTANCE));
        registry.addRecipes(WORKTABLE_CATEGORY, manager.getAllRecipesFor(WorktableRecipe.Type.INSTANCE));
        registry.addRecipes(RITUAL_CATEGORY, RitualRegistry.getWrappedRecipes());
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(DyeRecipe.class, DyeRecipeCategory::new);
    }

}