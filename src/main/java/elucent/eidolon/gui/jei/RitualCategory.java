package elucent.eidolon.gui.jei;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.FocusItemPresentRequirement;
import elucent.eidolon.api.ritual.HealthRequirement;
import elucent.eidolon.api.ritual.IRequirement;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.codex.CodexGui;
import elucent.eidolon.codex.RitualPage;
import elucent.eidolon.codex.RitualPage.RitualIngredient;
import elucent.eidolon.recipe.ItemRitualRecipe;
import elucent.eidolon.recipe.RitualRecipe;
import elucent.eidolon.registries.Registry;
import elucent.eidolon.util.ColorUtil;
import elucent.eidolon.util.RenderUtil;
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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RitualCategory implements IRecipeCategory<RitualRecipe> {
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
        rearrangeIngredients(recipe, inputs);

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
            layout.addSlot(RecipeIngredientRole.OUTPUT, 62, 45).addItemStack(resultRitual.getResultItem());
    }

    public static void rearrangeIngredients(@NotNull RitualRecipe recipe, List<RitualIngredient> inputs) {
        for (Ingredient ingredient : recipe.pedestalItems) {
            inputs.add(new RitualIngredient(ingredient, false));
        }

        //put foci in the middle
        int nIngredients = inputs.size();
        int middle = nIngredients / 2;

        List<Ingredient> focusItems = recipe.focusItems;
        for (int i = 0; i < focusItems.size(); i++) {
            Ingredient ingredient = focusItems.get(i);
            inputs.add(i + middle, new RitualIngredient(ingredient, true));
        }

    }

    @Override
    public void draw(@NotNull RitualRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull PoseStack stack, double mouseX, double mouseY) {
        var bg = RitualPage.BACKGROUND;
        RenderSystem.setShaderTexture(0, bg);
        int x = 5, y = 4;
        var guiGraphics = CodexGui.DUMMY;
        guiGraphics.blit(stack, x, y, 0, 0, 128, 160);

        List<RitualIngredient> inputs = new ArrayList<>();
        rearrangeIngredients(recipe, inputs);

        Ritual ritual = recipe.getRitualWithRequirements();
        float angleStep = Math.min(30, 180 / inputs.size());
        double rootAngle = 90 - (inputs.size() - 1) * angleStep / 2;
        for (int i = 0; i < inputs.size(); i++) {
            double a = Math.toRadians(rootAngle + angleStep * i);
            int dx = (int) (64 + 48 * Math.cos(a));
            int dy = (int) (88 + 48 * Math.sin(a));
            if (inputs.get(i).isFocus) guiGraphics.blit(stack, x + dx - 13, y + dy - 13, 128, 0, 26, 24);
            else guiGraphics.blit(stack, x + dx - 8, y + dy - 8, 154, 0, 16, 16);
        }

        for (IRequirement iRequirement : ritual.getInvariants()) {
            if (iRequirement instanceof FocusItemPresentRequirement) {
                guiGraphics.blit(stack, x + 86 - 5, y + 80 - 5, 128, 0, 26, 24);
                break;
            }
        }

        ritual.getRequirements().stream().filter(HealthRequirement.class::isInstance).map(HealthRequirement.class::cast).findFirst().ifPresent(
                healthRequirement -> {
                    var font = Minecraft.getInstance().font;
                    Component text = Component.translatable("eidolon.jei.health_sacrifice", healthRequirement.getHealth() / 2);
                    font.draw(stack, text, x, y - 1, ColorUtil.packColor(128, 255, 255, 255));
                    font.draw(stack, text, x - 1, y, ColorUtil.packColor(128, 219, 212, 184));
                    font.draw(stack, text, x + 1, y, ColorUtil.packColor(128, 219, 212, 184));
                    font.draw(stack, text, x, y + 1, ColorUtil.packColor(128, 191, 179, 138));
                    font.draw(stack, text, x, y, ColorUtil.packColor(255, 79, 59, 47));
                }
        );

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Tesselator tess = Tesselator.getInstance();
        //RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ClientRegistry::getGlowingShader);
        RenderUtil.dragon(stack, MultiBufferSource.immediate(tess.getBuilder()), x + 64, y + 48, 20, 20, ritual.getRed(), ritual.getGreen(), ritual.getBlue());
        tess.end();
        //RenderSystem.enableTexture();
        RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        for (int j = 0; j < 2; j++) {
            RenderUtil.litQuad(stack, MultiBufferSource.immediate(tess.getBuilder()), x + 52, y + 36, 24, 24,
                    ritual.getRed(), ritual.getGreen(), ritual.getBlue(), Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ritual.getSymbol()));
            tess.end();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

    }

}

