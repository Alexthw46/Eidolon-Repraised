package elucent.eidolon.gui.jei;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import elucent.eidolon.Eidolon;
import elucent.eidolon.codex.CodexGui;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static elucent.eidolon.codex.CruciblePage.BACKGROUND;
import static elucent.eidolon.codex.Page.drawText;

public class CrucibleCategory implements IRecipeCategory<CrucibleRecipe> {
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
    public @NotNull RecipeType<CrucibleRecipe> getRecipeType() {
        return JEIRegistry.CRUCIBLE_CATEGORY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei." + Eidolon.MODID + ".crucible");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    public static class StackIngredient {
        final ItemStack stack;
        final Ingredient ingredient;

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
            if (!ItemStack.isSame(i.stack, last.stack) || !ItemStack.tagMatches(i.stack, last.stack) || last.stack.getCount() + i.stack.getCount() > last.stack.getMaxStackSize()) {
                last = i;
            }
            else {
                last.stack.grow(i.stack.getCount());
                iter.remove();
            }
        }
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder layout, CrucibleRecipe recipe, @NotNull IFocusGroup focusGroup) {

        List<CrucibleRecipe.Step> steps = recipe.getSteps();
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
                List<ItemStack> inputs = Arrays.stream(stepInput.ingredient.getItems()).map(ItemStack::copy).toList();
                inputs.forEach(ii -> ii.setCount(stepInput.stack.getCount()));
                layout.addSlot(RecipeIngredientRole.INPUT, tx, ty).addItemStacks(inputs);
                tx += 17;
            }
        }

        layout.addSlot(RecipeIngredientRole.OUTPUT, 60, yoff + steps.size() * 20 + 14).addItemStack(recipe.getResultItem());
    }

    @Override

    public void draw(CrucibleRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull PoseStack mStack, double mouseX, double mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        CodexGui guiGraphics = CodexGui.DUMMY;
        var steps = recipe.getSteps();
        int h = steps.size() * 20 + 32;
        int yoff = 80 - h / 2;
        int x = 4, y = 5;
        for (int i = 0; i < steps.size(); i++) {
            int tx = x, ty = (y + yoff + i * 20);
            guiGraphics.blit(mStack, tx, ty, 128, 0, 128, 20);
            tx += 24;

            List<StackIngredient> stepInputs = new ArrayList<>();
            for (Ingredient o : steps.get(i).matches) {
                ItemStack stack = o.getItems().length > 0 ? o.getItems()[0].copy() : ItemStack.EMPTY.copy();
                if (!stack.isEmpty()) stepInputs.add(new StackIngredient(stack, o));
            }
            condense(stepInputs);

            for (var counter : stepInputs) {
                if (!counter.stack.isEmpty()) {
                    guiGraphics.blit(mStack, tx, ty + 1, 176, 32, 16, 17);
                    tx += 17;
                }
            }
            for (int j = 0; j < steps.get(i).stirs; j++) {
                guiGraphics.blit(mStack, tx, ty + 1, 192, 32, 16, 17);
                tx += 17;
            }
        }
        guiGraphics.blit(mStack, x, y + yoff + steps.size() * 20, 128, 64, 128, 32);

        Font font = Minecraft.getInstance().font;
        for (int i = 0; i < steps.size(); i++) {
            int ty = y + yoff + i * 20;
            drawText(guiGraphics, mStack, I18n.get("enchantment.level." + (i + 1)) + ".", x + 7, ty + 17 - font.lineHeight);
        }

    }
}


