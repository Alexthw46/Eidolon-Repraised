package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IKnowledge;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.util.ColorUtil;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChantPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_chant_page.png");
    final Sign[] chant;
    final String text;
    final String title;

    public ChantPage(String textKey, Sign... chant) {
        super(BACKGROUND);
        this.text = textKey;
        this.title = textKey + ".title";
        this.chant = chant;
    }

    @OnlyIn(Dist.CLIENT)
    static void colorBlit(PoseStack mStack, int x, int y, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight, int color) {
        Matrix4f matrix = mStack.last().pose();
        int maxX = x + width, maxY = y + height;
        float minU = (float)uOffset / textureWidth, minV = (float)vOffset / textureHeight;
        float maxU = minU + (float)width / textureWidth, maxV = minV + (float)height / textureHeight;
        int r = ColorUtil.getRed(color),
            g = ColorUtil.getGreen(color),
            b = ColorUtil.getBlue(color);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix, (float)x, (float)maxY, 0).uv(minU, maxV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float)maxX, (float)maxY, 0).uv(maxU, maxV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float)maxX, (float)y, 0).uv(maxU, minV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float) x, (float) y, 0).uv(minU, minV).color(r, g, b, 255).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, PoseStack mStack, int x, int y, int mouseX, int mouseY) {
        String title = I18n.get(this.title);
        int titleWidth = Minecraft.getInstance().font.width(title);
        drawText(gui, mStack, title, x + 64 - titleWidth / 2, y + 15 - Minecraft.getInstance().font.lineHeight);

        RenderSystem.setShaderTexture(0, CodexGui.CODEX_BACKGROUND);
        Player entity = Minecraft.getInstance().player;
        IKnowledge knowledge = entity.getCapability(IKnowledge.INSTANCE, null).resolve().get();
        int w = chant.length * 24;
        int baseX = x + 64 - w / 2;
        CodexGui.blit(mStack, baseX - 16, y + 28, 256, 208, 16, 32, 512, 512);
        for (int i = 0; i < chant.length; i ++) {
            CodexGui.blit(mStack, baseX + i * 24, y + 28, 272, 208, 24, 32, 512, 512);
        }
        CodexGui.blit(mStack, baseX + w, y + 28, 296, 208, 16, 32, 512, 512);
        RenderSystem.enableBlend();

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        for (int i = 0; i < chant.length; i ++) {
            RenderSystem.setShaderTexture(0, CodexGui.CODEX_BACKGROUND);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            CodexGui.blit(mStack, baseX + i * 24, y + 28, 312, 208, 24, 24, 512, 512);

            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            Sign sign = chant[i];
            float flicker = 0.875f + 0.125f * (float)Math.sin(Math.toRadians(12 * ClientEvents.getClientTicks()));
            RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderUtil.litQuad(mStack, bufferSource, baseX + i * 24 + 4, y + 32, 16, 16,
                    sign.getRed(), sign.getGreen(), sign.getBlue(), Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(sign.getSprite()));
            RenderUtil.litQuad(mStack, bufferSource, baseX + i * 24 + 4, y + 32, 16, 16,
                    sign.getRed() * flicker, sign.getGreen() * flicker, sign.getBlue() * flicker, Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(sign.getSprite()));
        }
        bufferSource.endBatch();
        RenderSystem.disableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        drawWrappingText(gui, mStack, I18n.get(text), x + 4, y + 72, 120);
    }
}
