package elucent.eidolon.gui.jei;


import elucent.eidolon.Eidolon;
import elucent.eidolon.codex.CodexGui;
import elucent.eidolon.codex.CruciblePage;
import elucent.eidolon.recipe.CrucibleRecipe;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CrucibleCategory implements IRecipeCategory<RecipeWrappers.Crucible> {
    static final ResourceLocation UID = new ResourceLocation(Eidolon.MODID, "crucible");
    private final IDrawable background, icon;

    public CrucibleCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(new ResourceLocation(Eidolon.MODID, "textures/gui/jei_page_bg.png"), 0, 0, 138, 172);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registry.CRUCIBLE.get()));
    }

    /**
     * @return the type of recipe that this category handles.
     * @since 9.5.0
     */
    @Override
    public @NotNull RecipeType<RecipeWrappers.Crucible> getRecipeType() {
        return JEIRegistry.CRUCIBLE_CATEGORY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable(I18n.get("jei." + Eidolon.MODID + ".crucible"));
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    protected static class StackIngredient {
        ItemStack stack;
        Ingredient ingredient;

        public StackIngredient(ItemStack stack, Ingredient ingredient) {
            this.stack = stack;
            this.ingredient = ingredient;
        }
    }

    public static void condense(List<StackIngredient> stacks) {
        Iterator<StackIngredient> iter = stacks.iterator();
        StackIngredient last = new StackIngredient(ItemStack.EMPTY, Ingredient.EMPTY);
        while (iter.hasNext()) {
            StackIngredient i = iter.next();
            if (!ItemStack.isSameItem(i.stack, last.stack) || !ItemStack.isSameItemSameTags(i.stack, last.stack) || last.stack.getCount() + i.stack.getCount() > last.stack.getMaxStackSize()) {
                last = i;
            } else {
                last.stack.grow(i.stack.getCount());
                iter.remove();
            }
        }
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder layout, RecipeWrappers.Crucible recipe, @NotNull IFocusGroup focusGroup) {

        List<CrucibleRecipe.Step> steps = recipe.recipe.getSteps();
        int h = steps.size() * 20 + 32;
        int yoff = 80 - h / 2;
        for (int i = 0; i < steps.size(); i++) {
            int tx = 4, ty = 3 + yoff + i * 20;
            tx += 24;

            List<StackIngredient> stepInputs = new ArrayList<>();
            for (Ingredient o : steps.get(i).matches) {
                ItemStack stack = o.getItems().length > 0 ? o.getItems()[0].copy() : ItemStack.EMPTY.copy();
                if (!stack.isEmpty()) stepInputs.add(new StackIngredient(stack, o));
            }
            condense(stepInputs);

            for (StackIngredient stepInput : stepInputs) {
                layout.addSlot(RecipeIngredientRole.INPUT, tx, ty).addIngredients(stepInput.ingredient);
                tx += 17;
            }
        }

        layout.addSlot(RecipeIngredientRole.OUTPUT, 60, yoff + steps.size() * 20 + 14).addItemStack(recipe.recipe.getResultItem());
    }

    @Override
    public void draw(RecipeWrappers.Crucible recipe, @NotNull IRecipeSlotsView slotsView, @NotNull GuiGraphics mStack, double mouseX, double mouseY) {
        recipe.getPage().renderBackground(CodexGui.DUMMY, mStack, 5, 4, (int) mouseX, (int) mouseY);
        recipe.getPage().render(CodexGui.DUMMY, mStack, CruciblePage.BACKGROUND, 5, 4, (int) mouseX, (int) mouseY);
    }
}


