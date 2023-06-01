package elucent.eidolon.gui.jei;


import com.mojang.blaze3d.vertex.PoseStack;
import elucent.eidolon.Eidolon;
import elucent.eidolon.Registry;
import elucent.eidolon.codex.CodexGui;
import elucent.eidolon.codex.RitualPage;
import elucent.eidolon.ritual.ItemSacrifice;
import elucent.eidolon.ritual.SanguineRitual;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RitualCategory implements IRecipeCategory<RecipeWrappers.RitualRecipe> {
    static final ResourceLocation UUID = new ResourceLocation(Eidolon.MODID, "ritual");
    private final IDrawable background, icon;

    public RitualCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(new ResourceLocation(Eidolon.MODID, "textures/gui/jei_page_bg.png"), 0, 0, 138, 172);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registry.BRAZIER.get()));
    }

    /**
     * @return the type of recipe that this category handles.
     * @since 9.5.0
     */
    @Override
    public @NotNull RecipeType<RecipeWrappers.RitualRecipe> getRecipeType() {
        return JEIRegistry.RITUAL_CATEGORY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable(I18n.get("jei." + Eidolon.MODID + ".ritual"));
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
    public void setRecipe(@NotNull IRecipeLayoutBuilder layout, RecipeWrappers.@NotNull RitualRecipe recipe, @NotNull IFocusGroup ingredients) {

        List<Ingredient> inputs = new ArrayList<>();
        ItemSacrifice sacrifice = recipe.sacrifice;

        for (RitualPage.RitualIngredient item : recipe.page.getInputs()) {
            inputs.add(Ingredient.of(item.stack));
        }

        float angleStep = Math.min(30, 180 / inputs.size());
        double rootAngle = 90 - (inputs.size() - 1) * angleStep / 2;
        for (int i = 0; i < inputs.size(); i++) {
            double a = Math.toRadians(rootAngle + angleStep * i);
            int dx = (int) (69 + 48 * Math.cos(a));
            int dy = (int) (91 + 48 * Math.sin(a));
            layout.addSlot(RecipeIngredientRole.INPUT, dx - 8, dy - 8).addIngredients(inputs.get(i));
        }

        layout.addSlot(RecipeIngredientRole.CATALYST, 60, 85).addIngredients(sacrifice.main);

        if (recipe.ritual instanceof SanguineRitual resultRitual)
            layout.addSlot(RecipeIngredientRole.OUTPUT, 62, 45).addItemStack(resultRitual.getResult());

    }

    @Override
    public void draw(RecipeWrappers.RitualRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull PoseStack mStack, double mouseX, double mouseY) {
        recipe.page.renderBackground(CodexGui.DUMMY, mStack, 5, 4, (int) mouseX, (int) mouseY);
        recipe.page.render(CodexGui.DUMMY, mStack, 5, 4, (int) mouseX, (int) mouseY);
    }

}

