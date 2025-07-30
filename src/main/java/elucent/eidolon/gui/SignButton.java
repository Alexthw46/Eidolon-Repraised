package elucent.eidolon.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.codex.SignIndexPage;
import elucent.eidolon.util.ClientInfo;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

import static elucent.eidolon.codex.Page.colorBlit;

public class SignButton extends Button {

    protected Sign sign;

    protected SignButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
    }

    public SignButton(int x, int y, int w, int h, Sign sign, OnPress onPress) {
        this(x, y, w, h, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.sign = sign;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        boolean infoHover = isMouseOver(pMouseX, pMouseY);
        var mStack = pGuiGraphics.pose();
        //render the translucent sign
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
        RenderSystem.setShaderTexture(0, SignIndexPage.BACKGROUND);
        mStack.pushPose();
        mStack.translate(getX() + 24, getY() + 24, 0);
        mStack.mulPose(Axis.ZP.rotationDegrees(ClientInfo.getClientPartialTicks() * 1.5f));
        colorBlit(mStack, -18, -18, 128, 48, 36, 36, 256, 256, sign.color());
        mStack.popPose();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        float flicker = 0.75f + 0.05f * (float) Math.sin(Math.toRadians(12 * ClientInfo.getClientPartialTicks()));
        for (int j = 0; j < (!infoHover ? 1 : 2); j++) {
            RenderUtil.litQuad(mStack, bufferSource, getX() + 12, getY() + 12, 24, 24,
                    sign.getRed() * flicker, sign.getGreen() * flicker, sign.getBlue() * flicker, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(sign.sprite()));
            bufferSource.endBatch();
        }
        RenderSystem.disableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (infoHover) {
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("eidolon.codex.sign_suffix", Component.translatable(sign.getRegistryName().getNamespace() + ".sign." + sign.getRegistryName().getPath())), pMouseX, pMouseY);
        }
    }
}
