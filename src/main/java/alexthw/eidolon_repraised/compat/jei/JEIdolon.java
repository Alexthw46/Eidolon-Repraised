package alexthw.eidolon_repraised.compat.jei;


import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.common.tile.BrazierTileEntity;
import alexthw.eidolon_repraised.gui.WorktableContainer;
import alexthw.eidolon_repraised.gui.WorktableScreen;
import alexthw.eidolon_repraised.recipe.CrucibleRecipe;
import alexthw.eidolon_repraised.recipe.DyeRecipe;
import alexthw.eidolon_repraised.recipe.RitualRecipe;
import alexthw.eidolon_repraised.recipe.WorktableRecipe;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import alexthw.eidolon_repraised.registries.Registry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIdolon implements IModPlugin {
    public static final RecipeType<CrucibleRecipe> CRUCIBLE_CATEGORY = RecipeType.create(Eidolon.MODID, "crucible", CrucibleRecipe.class);
    public static final RecipeType<WorktableRecipe> WORKTABLE_CATEGORY = RecipeType.create(Eidolon.MODID, "worktable", WorktableRecipe.class);
    public static final RecipeType<RitualRecipe> RITUAL_CATEGORY = RecipeType.create(Eidolon.MODID, "rituals", RitualRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "jei_plugin");
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

        registry.addRecipes(CRUCIBLE_CATEGORY, manager.getAllRecipesFor(EidolonRecipes.CRUCIBLE_TYPE.get()).stream().map(RecipeHolder::value).toList());
        registry.addRecipes(WORKTABLE_CATEGORY, manager.getAllRecipesFor(EidolonRecipes.WORKTABLE_TYPE.get()).stream().map(RecipeHolder::value).toList());
        registry.addRecipes(RITUAL_CATEGORY, BrazierTileEntity.getRitualRecipes(Eidolon.proxy.getWorld()));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(DyeRecipe.class, new DyeRecipeCategory());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(WorktableScreen.class, 128, 55, 28, 23, RecipeTypes.CRAFTING, WORKTABLE_CATEGORY);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                new WorktableTransferHandler(registration.getTransferHelper()),
                WORKTABLE_CATEGORY
        );
        registration.addRecipeTransferHandler(
                WorktableContainer.class,
                Registry.WORKTABLE_CONTAINER.get(),
                RecipeTypes.CRAFTING,
                1, // 0 is the output slot
                9, // 1-9 are the crafting grid, plus 4 that aren't used for basic crafting
                14, 36 // start slot and n slots of the player inventory
        );
    }
}