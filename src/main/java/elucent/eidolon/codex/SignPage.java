package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SignPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_sign_page.png");
    final Sign sign;

    public SignPage(Sign sign) {
        super(BACKGROUND);
        this.sign = sign;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics guiGraphics, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        Tesselator tess = Tesselator.getInstance();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
        PoseStack mStack = guiGraphics.pose();
        mStack.pushPose();
        mStack.translate(x + 64, y + 80, 0);
        // mStack.scale(0.9f, 0.9f, 0.9f);
        mStack.mulPose(Axis.ZP.rotationDegrees(ClientEvents.getClientTicks() * 1.5f));
        colorBlit(mStack, -40, -40, 128, 96, 80, 80, 256, 256, sign.getColor());
        mStack.popPose();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        for (int i = 0; i < 2; i ++) {
            RenderUtil.litQuad(mStack, MultiBufferSource.immediate(tess.getBuilder()), x + 44, y + 60, 40, 40,
                    sign.getRed(), sign.getGreen(), sign.getBlue(), Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(sign.getSprite()));
            tess.end();
        }
        RenderSystem.disableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }
}
