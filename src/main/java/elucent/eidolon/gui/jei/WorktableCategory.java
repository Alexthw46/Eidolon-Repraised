package elucent.eidolon.gui.jei;


import elucent.eidolon.Eidolon;
import elucent.eidolon.recipe.WorktableRecipe;
import elucent.eidolon.registries.Registry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class WorktableCategory implements IRecipeCategory<WorktableRecipe> {
    static final ResourceLocation UID = new ResourceLocation(Eidolon.MODID, "worktable");
    private final IDrawable background, icon;

    public WorktableCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(new ResourceLocation(Eidolon.MODID, "textures/gui/jei_page_bg.png"), 0, 0, 138, 172);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registry.WORKTABLE.get()));
    }

    /**
     * @return the type of recipe that this category handles.
     * @since 9.5.0
     */
    @Override
    public @NotNull RecipeType<WorktableRecipe> getRecipeType() {
        return JEIRegistry.WORKTABLE_CATEGORY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable(I18n.get("jei." + Eidolon.MODID + ".worktable"));
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder layout, @NotNull WorktableRecipe recipe, @NotNull IFocusGroup ingredients) {
        Ingredient[] inputs = recipe.getCore();
        Ingredient[] outers = recipe.getOuter();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                if (index >= inputs.length) break;
                layout.addSlot(RecipeIngredientRole.INPUT, 44 + j * 17, 37 + i * 17).addIngredients(inputs[index]);
            }
        }
        layout.addSlot(RecipeIngredientRole.INPUT, 61, 15).addIngredients(outers[0]);
        layout.addSlot(RecipeIngredientRole.INPUT, 100, 54).addIngredients(outers[1]);
        layout.addSlot(RecipeIngredientRole.INPUT, 61, 93).addIngredients(outers[2]);
        layout.addSlot(RecipeIngredientRole.INPUT, 22, 54).addIngredients(outers[3]);

        layout.addSlot(RecipeIngredientRole.OUTPUT, 61, 133).addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(@NotNull WorktableRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull GuiGraphics mStack, double mouseX, double mouseY) {
        mStack.blit(BACKGROUND, 5, 4, 0, 0, 128, 160);
    }

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_worktable_page.png");

}
