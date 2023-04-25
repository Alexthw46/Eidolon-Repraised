package elucent.eidolon.gui.jei;

/*
@JeiPlugin
public class JEIRegistry implements IModPlugin {
    public static IRecipeCategory CRUCIBLE_CATEGORY, WORKTABLE_CATEGORY, RITUAL_CATEGORY;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Eidolon.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(Registry.CRUCIBLE.get()), CRUCIBLE_CATEGORY.getUid());
        registry.addRecipeCatalyst(new ItemStack(Registry.WORKTABLE.get()), WORKTABLE_CATEGORY.getUid());
        registry.addRecipeCatalyst(new ItemStack(Registry.BRAZIER.get()), RITUAL_CATEGORY.getUid());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(CRUCIBLE_CATEGORY = new CrucibleCategory(guiHelper));
        registry.addRecipeCategories(WORKTABLE_CATEGORY = new WorktableCategory(guiHelper));
        registry.addRecipeCategories(RITUAL_CATEGORY = new RitualCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(CrucibleRegistry.getWrappedRecipes(), CRUCIBLE_CATEGORY.getUid());
        registry.addRecipes(WorktableRegistry.getWrappedRecipes(), WORKTABLE_CATEGORY.getUid());
        registry.addRecipes(RitualRegistry.getWrappedRecipes(), RITUAL_CATEGORY.getUid());
    }
}


 */