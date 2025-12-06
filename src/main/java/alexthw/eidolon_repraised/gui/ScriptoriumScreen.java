package alexthw.eidolon_repraised.gui;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.client.ClientRegistry;
import alexthw.eidolon_repraised.network.InscribePacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import alexthw.eidolon_repraised.util.RenderUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static alexthw.eidolon_repraised.codex.CodexGui.blit;

public class ScriptoriumScreen extends AbstractContainerScreen<ScriptoriumContainer> {

    public int FULL_WIDTH = 256;
    public int FULL_HEIGHT = 256;
    public static ResourceLocation background = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "textures/gui/inscription_table.png");

    public List<Sign> currentChant;
    public List<SignButton> signButtons = new ArrayList<>();

    public int bookLeft;
    public int bookTop;
    public int bookRight;
    public int bookBottom;

    public ScriptoriumScreen(ScriptoriumContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void init() {
        super.init();
        bookLeft = width / 2 - FULL_WIDTH / 2;
        bookTop = height / 2 - FULL_HEIGHT / 2;
        bookRight = width / 2 + FULL_WIDTH / 2;
        bookBottom = height / 2 + FULL_HEIGHT / 2;
        currentChant = new ArrayList<>(7);
        layoutSigns();
        addRenderableWidget(new ChantButton(bookLeft, bookTop + 8, 32, 32, (b) -> {
            if (!currentChant.isEmpty()) {
                Networking.sendToServer(new InscribePacket(this.menu.containerId, currentChant));
            }
        }));
        addRenderableWidget(new CancelButton(bookLeft, bookTop + 8 + 32, 32, 32, (b) -> currentChant.clear()));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        drawScreenAfterScale(graphics, mouseX, mouseY, partialTicks);
        matrixStack.popPose();
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        graphics.blit(background, bookLeft + 32, bookTop, 0, 0, 200, FULL_HEIGHT, FULL_WIDTH, FULL_HEIGHT);
        graphics.blit(background, bookLeft - 20, bookTop + 72, 200, 92, 54, 56, FULL_WIDTH, FULL_HEIGHT);
    }

    public void drawBackgroundElements(GuiGraphics graphics) {
    }

    public void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderChant(graphics, mouseX, mouseY, partialTicks);
    }

    public void drawScreenAfterScale(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(bookLeft, bookTop, 0);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        drawBackgroundElements(graphics);
        drawForegroundElements(graphics, mouseX, mouseY, partialTicks);
        poseStack.popPose();
        renderTooltip(graphics, mouseX, mouseY);
    }


    // Fills the sign list with sign buttons
    public void layoutSigns() {
        signButtons = new ArrayList<>();
        List<Sign> signs = KnowledgeUtil.getKnownSigns(Eidolon.proxy.getPlayer());
        int startX = bookLeft + 24;
        int startY = bookTop - 15;
        for (int i = 0; i < signs.size(); i++) {
            Sign sign = signs.get(i);
            SignButton button = new SignButton(startX + 8 + (i % 5) * 38, startY + 16 + (i / 5) * 40, 30, 30, sign, (b) -> {
                if (currentChant.size() < 7) currentChant.add(sign);
            });
            signButtons.add(button);
            addRenderableWidget(button);
        }
    }

    protected void renderChant(@NotNull GuiGraphics mStack, int mouseX, int mouseY, float pticks) {
        var chant = currentChant;
        int chantWidth = 32 + 24 * chant.size();
        int baseX = 4 + 128 - chantWidth / 2;
        int y = 92 + 33;

        RenderSystem.enableBlend();

        int bgx = baseX;
        // left border of the chant bg
        blit(mStack, bgx, y, 256, 208, 16, 32, 512, 512);
        bgx += 16;

        // middle of the chant bg
        for (int i = 0; i < chant.size(); i++) {
            blit(mStack, bgx, y, 272, 208, 24, 32, 512, 512);
            blit(mStack, bgx, y, 312, 208, 24, 24, 512, 512);
            bgx += 24;
        }

        // right border of the chant bg
        blit(mStack, bgx, y, 296, 208, 16, 32, 512, 512);

        RenderSystem.enableBlend();
        RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();

        bgx = baseX + 16;

        // render the signs over the red cloth
        for (Sign sign : chant) {
            RenderUtil.litQuad(mStack.pose(), buffersource, bgx + 4, y + 4, 16, 16,
                    sign.getRed(), sign.getGreen(), sign.getBlue(), Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(sign.sprite()));
            buffersource.endBatch();
            bgx += 24;
        }

        bgx = baseX + 16;
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        for (int i = 0; i < chant.size(); i++) {
            float flicker = 0.75f + 0.05f * (float) Math.sin(Math.toRadians(12 * pticks - 360.0f * i / chant.size()));
            Sign sign = chant.get(i);
            RenderUtil.litQuad(mStack.pose(), buffersource, bgx + 4, y + 4, 16, 16,
                    sign.getRed() * flicker, sign.getGreen() * flicker, sign.getBlue() * flicker, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(sign.sprite()));
            buffersource.endBatch();
            bgx += 24;
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, background);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        collectTooltips(mouseX, mouseY, tooltip);
        if (!tooltip.isEmpty()) {
            stack.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
        super.renderTooltip(stack, mouseX, mouseY);
    }

    private void collectTooltips(int mouseX, int mouseY, List<Component> tooltip) {

    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {

    }
}
