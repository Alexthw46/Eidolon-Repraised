package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.FocusItemPresentRequirement;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.common.ritual.CraftingRitual;
import elucent.eidolon.gui.jei.RitualCategory;
import elucent.eidolon.recipe.RitualRecipe;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static elucent.eidolon.Eidolon.prefix;
import static elucent.eidolon.util.RegistryUtil.getRegistryName;

public class RitualPage extends RecipePage<RitualRecipe> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_ritual_page.png");
    Ritual ritual;
    Ingredient center;

    RitualIngredient[] inputs;

    public RitualPage(ResourceLocation background, ResourceLocation recipeName, ItemStack empty) {
        super(background, (recipeName.getNamespace().equals("eidolon")) ? prefix("rituals/" + recipeName.getPath()) : recipeName, empty);
    }

    public static void renderRitualSymbol(@NotNull GuiGraphics guiGraphics, int x, int y, Ritual ritual) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        //RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ClientRegistry::getGlowingShader);
        RenderUtil.dragon(guiGraphics.pose(), buffersource, x + 64, y + 48, 20, 20, ritual.getRed(), ritual.getGreen(), ritual.getBlue());
        buffersource.endBatch();
        //RenderSystem.enableTexture();
        RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        for (int j = 0; j < 2; j++) {
            RenderUtil.litQuad(guiGraphics.pose(), buffersource, x + 52, y + 36, 24, 24,
                    ritual.getRed(), ritual.getGreen(), ritual.getBlue(), Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ritual.getSymbol()));
            buffersource.endBatch();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    @Override
    public @Nullable RitualRecipe getRecipe(ResourceLocation id) {
        return (RitualRecipe) Eidolon.proxy.getWorld().getRecipeManager().byKey(id).orElse(null);
    }

    public static class RitualIngredient {
        public final Ingredient stack;
        public boolean isFocus;

        public RitualIngredient(Ingredient stack, boolean isFocus) {
            this.stack = stack;
            this.isFocus = isFocus;
        }

        public RitualIngredient(ItemLike stack, boolean isFocus) {
            this.stack = Ingredient.of(stack);
            this.isFocus = isFocus;
        }

    }

    public RitualPage(ResourceLocation recipeName) {
        this(BACKGROUND, recipeName, ItemStack.EMPTY);
    }

    public RitualPage(ResourceLocation recipeName, ItemStack result) {
        this(BACKGROUND, recipeName, result);
    }

    public RitualPage(Ritual ritual) {
        this(BACKGROUND, ritual instanceof CraftingRitual cr ?
                        getRegistryName(cr.getResult().getItem()) : prefix("ritual_" + ritual.getRegistryName().getPath()),
                ritual instanceof CraftingRitual sr ? sr.getResult() : ItemStack.EMPTY);
        this.ritual = ritual;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics guiGraphics, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
        if (cachedRecipe == null) return;

        //cache recipe ingredients upon first render
        if (inputs == null || center == null) {
            int nIngredients = cachedRecipe.pedestalItems.size() + cachedRecipe.focusItems.size();
            if (nIngredients > 0) {
                var items = new ArrayList<RitualIngredient>(nIngredients);
                RitualCategory.rearrangeIngredients(cachedRecipe, items);
                inputs = items.toArray(new RitualIngredient[nIngredients]);
                center = cachedRecipe.reagent;
            } else return;
        }

        float angleStep = Math.min(30, 180 / inputs.length);
        double rootAngle = 90 - (inputs.length - 1) * angleStep / 2;
        for (int i = 0; i < inputs.length; i++) {
            double a = Math.toRadians(rootAngle + angleStep * i);
            int dx = (int) (64 + 48 * Math.cos(a));
            int dy = (int) (88 + 48 * Math.sin(a));
            if (inputs[i].isFocus) guiGraphics.blit(bg, x + dx - 13, y + dy - 13, 128, 0, 26, 24);
            else guiGraphics.blit(bg, x + dx - 8, y + dy - 8, 154, 0, 16, 16);
        }

        if (ritual == null) {
            ritual = cachedRecipe.getRitual();
            if (ritual == null) return;
        }

        if (ritual.getInvariants().stream().anyMatch(FocusItemPresentRequirement.class::isInstance))
            guiGraphics.blit(bg, x + 86 - 5, y + 80 - 5, 128, 0, 26, 24);

        renderRitualSymbol(guiGraphics, x, y, ritual);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        if (cachedRecipe == null || inputs == null || inputs.length == 0 || center == null) return;
        float angleStep = Math.min(30, 180 / inputs.length);
        double rootAngle = 90 - (inputs.length - 1) * angleStep / 2;
        for (int i = 0; i < inputs.length; i++) {
            double a = Math.toRadians(rootAngle + angleStep * i);
            int dx = (int) (64 + 48 * Math.cos(a));
            int dy = (int) (88 + 48 * Math.sin(a));
            drawItems(mStack, inputs[i].stack, x + dx - 8, y + dy - 8, mouseX, mouseY);
        }
        drawItems(mStack, center, x + 56, y + 80, mouseX, mouseY);

        if (ritual != null) {
            FocusItemPresentRequirement invariants = ritual.getInvariants().stream().filter(FocusItemPresentRequirement.class::isInstance).map(FocusItemPresentRequirement.class::cast).findFirst().orElse(null);
            if (invariants == null) return;
            drawItems(mStack, invariants.getMatch(), x + 86, y + 80, mouseX, mouseY);
        }
    }
}
