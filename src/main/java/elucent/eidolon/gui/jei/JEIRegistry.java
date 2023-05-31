package elucent.eidolon.gui.jei;


import elucent.eidolon.Eidolon;
import elucent.eidolon.Registry;
import elucent.eidolon.recipe.CrucibleRegistry;
import elucent.eidolon.recipe.WorktableRegistry;
import elucent.eidolon.ritual.RitualRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIRegistry implements IModPlugin {
    public static final RecipeType<RecipeWrappers.Crucible> CRUCIBLE_CATEGORY = RecipeType.create(Eidolon.MODID, "crucible", RecipeWrappers.Crucible.class);
    public static final RecipeType<RecipeWrappers.Worktable> WORKTABLE_CATEGORY = RecipeType.create(Eidolon.MODID, "worktable", RecipeWrappers.Worktable.class);
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
        registry.addRecipes(CRUCIBLE_CATEGORY, CrucibleRegistry.getWrappedRecipes());
        registry.addRecipes(WORKTABLE_CATEGORY, WorktableRegistry.getWrappedRecipes());
        registry.addRecipes(RITUAL_CATEGORY, RitualRegistry.getWrappedRecipes());
    }

}