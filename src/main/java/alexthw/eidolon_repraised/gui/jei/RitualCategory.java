package alexthw.eidolon_repraised.gui.jei;


import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.ritual.FocusItemPresentRequirement;
import alexthw.eidolon_repraised.api.ritual.HealthRequirement;
import alexthw.eidolon_repraised.api.ritual.IRequirement;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.codex.RitualPage;
import alexthw.eidolon_repraised.codex.RitualPage.RitualIngredient;
import alexthw.eidolon_repraised.recipe.ItemRitualRecipe;
import alexthw.eidolon_repraised.recipe.RitualRecipe;
import alexthw.eidolon_repraised.registries.Registry;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static alexthw.eidolon_repraised.codex.RitualPage.renderRitualSymbol;

public class RitualCategory implements IRecipeCategory<RitualRecipe> {
    static final ResourceLocation UUID = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"ritual" );
    private final IDrawable background, icon;

    public RitualCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/gui/jei_page_bg.png" ), 0, 0, 138, 172);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registry.BRAZIER.get()));
    }

    /**
     * @return the type of recipe that this category handles.
     * @since 9.5.0
     */
    @Override
    public @NotNull RecipeType<RitualRecipe> getRecipeType() {
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
    public void setRecipe(@NotNull IRecipeLayoutBuilder layout, @NotNull RitualRecipe recipe, @NotNull IFocusGroup ingredients) {

        List<RitualIngredient> inputs = new ArrayList<>();
        RitualPage.rearrangeIngredients(recipe, inputs);

        float angleStep = Math.min(30, 180 / inputs.size());
        double rootAngle = 90 - (inputs.size() - 1) * angleStep / 2;
        for (int i = 0; i < inputs.size(); i++) {
            double a = Math.toRadians(rootAngle + angleStep * i);
            int dx = (int) (69 + 48 * Math.cos(a));
            int dy = (int) (91 + 48 * Math.sin(a));
            layout.addSlot(RecipeIngredientRole.INPUT, dx - 8, dy - 8).addIngredients(inputs.get(i).stack);
        }

        layout.addSlot(RecipeIngredientRole.INPUT, 60, 85).addIngredients(recipe.reagent);

        for (IRequirement iRequirement : recipe.getRitual().getInvariants()) {
            if (iRequirement instanceof FocusItemPresentRequirement focusItemPresentRequirement) {
                layout.addSlot(RecipeIngredientRole.CATALYST, 91, 82).addIngredients(focusItemPresentRequirement.getMatch());
                break;
            }
        }

        if (recipe instanceof ItemRitualRecipe resultRitual)
            layout.addSlot(RecipeIngredientRole.OUTPUT, 62, 45).addItemStack(resultRitual.getResultItem(RegistryAccess.EMPTY));
    }

    @Override
    public void draw(@NotNull RitualRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var bg = RitualPage.BACKGROUND;
        int x = 5, y = 4;
        guiGraphics.blit(bg, x, y, 0, 0, 128, 160);

        List<RitualIngredient> inputs = new ArrayList<>();
        RitualPage.rearrangeIngredients(recipe, inputs);

        Ritual ritual = recipe.getRitualWithRequirements();

        var name = ritual.getName();
        var font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, name, x + 69 - font.width(name) / 2, y + 5, 0, false);

        float angleStep = Math.min(30, 180 / inputs.size());
        double rootAngle = 90 - (inputs.size() - 1) * angleStep / 2;
        for (int i = 0; i < inputs.size(); i++) {
            double a = Math.toRadians(rootAngle + angleStep * i);
            int dx = (int) (64 + 48 * Math.cos(a));
            int dy = (int) (88 + 48 * Math.sin(a));
            if (inputs.get(i).isFocus) guiGraphics.blit(bg, x + dx - 13, y + dy - 13, 128, 0, 26, 24);
            else guiGraphics.blit(bg, x + dx - 8, y + dy - 8, 154, 0, 16, 16);
        }

        for (IRequirement iRequirement : ritual.getInvariants()) {
            if (iRequirement instanceof FocusItemPresentRequirement) {
                guiGraphics.blit(bg, x + 86 - 5, y + 80 - 5, 128, 0, 26, 24);
                break;
            }
        }

        ritual.getRequirements().stream().filter(HealthRequirement.class::isInstance).map(HealthRequirement.class::cast).findFirst().ifPresent(
                healthRequirement -> guiGraphics.drawString(font, Component.translatable("eidolon.jei.health_sacrifice", healthRequirement.getHealth() / 2), x + 8, y + 150, 0xFF0000, false)
        );

        renderRitualSymbol(guiGraphics, x, y, ritual);

    }

}

