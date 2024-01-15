package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IKnowledge;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.registries.EidolonSounds;
import elucent.eidolon.util.ColorUtil;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class SignIndexPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_sign_index_page.png");
    final List<SignEntry> entries = new ArrayList<>();

    public static class SignEntry {
        final Chapter chapter;
        final Sign sign;

        public SignEntry(Chapter chapter, Sign sign) {
            this.chapter = chapter;
            this.sign = sign;
        }
    }

    public SignIndexPage(SignEntry... pages) {
        super(BACKGROUND);
        this.entries.addAll(List.of(pages));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean click(CodexGui gui, int x, int y, int mouseX, int mouseY) {
        Player entity = Minecraft.getInstance().player;
        IKnowledge knowledge = entity.getCapability(IKnowledge.INSTANCE, null).resolve().get();
        for (int i = 0; i < entries.size(); i++) {
            int xx = x + 8 + (i % 2) * 56, yy = y + 4 + (i / 2) * 52;
            if (knowledge.knowsSign(entries.get(i).sign) && mouseX >= xx + 38 && mouseY >= yy + 38 && mouseX <= xx + 50 && mouseY <= yy + 50) {
                gui.changeChapter(entries.get(i).chapter);
                Minecraft.getInstance().player.playNotifySound(SoundEvents.UI_BUTTON_CLICK, SoundSource.NEUTRAL, 1.0f, 1.0f);
                return true;
            } else if (knowledge.knowsSign(entries.get(i).sign) && mouseX >= xx && mouseX <= xx + 48 && mouseY >= yy && mouseY <= yy + 48) {
                gui.addToChant(entries.get(i).sign);
                entity.playNotifySound(EidolonSounds.SELECT_RUNE.get(), SoundSource.NEUTRAL, 0.5f, entity.level.random.nextFloat() * 0.25f + 0.75f);
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    static void colorBlit(PoseStack mStack, int x, int y, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight, int color) {
        Matrix4f matrix = mStack.last().pose();
        int maxX = x + width, maxY = y + height;
        float minU = (float) uOffset / textureWidth, minV = (float) vOffset / textureHeight;
        float maxU = minU + (float) width / textureWidth, maxV = minV + (float) height / textureHeight;
        int r = ColorUtil.getRed(color),
                g = ColorUtil.getGreen(color),
                b = ColorUtil.getBlue(color);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix, (float) x, (float) maxY, 0).uv(minU, maxV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float) maxX, (float) maxY, 0).uv(maxU, maxV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float) maxX, (float) y, 0).uv(maxU, minV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float) x, (float) y, 0).uv(minU, minV).color(r, g, b, 255).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, PoseStack mStack, int x, int y, int mouseX, int mouseY) {
        Player entity = Minecraft.getInstance().player;
        IKnowledge knowledge = entity.getCapability(IKnowledge.INSTANCE, null).resolve().get();
        for (int i = 0; i < entries.size(); i++) {
            RenderSystem.setShaderTexture(0, BACKGROUND);
            int xx = x + 8 + (i % 2) * 56, yy = y + 4 + (i / 2) * 52;
            Sign sign = entries.get(i).sign;
            boolean hover = knowledge.knowsSign(sign) && mouseX >= xx && mouseX <= xx + 48 && mouseY >= yy && mouseY <= yy + 48;
            boolean infoHover = knowledge.knowsSign(sign) && mouseX >= xx + 38 && mouseY >= yy + 38 && mouseX <= xx + 50 && mouseY <= yy + 50;
            gui.blit(mStack, xx, yy, knowledge.knowsSign(entries.get(i).sign) ? 128 : 176, 0, 48, 48);


            if (knowledge.knowsSign(sign)) {
                MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
                if (hover && !infoHover) {
                    mStack.pushPose();
                    mStack.translate(xx + 24, yy + 24, 0);
                    mStack.mulPose(Vector3f.ZP.rotationDegrees(ClientEvents.getClientTicks() * 1.5f));
                    colorBlit(mStack, -18, -18, 128, 48, 36, 36, 256, 256, sign.getColor());
                    mStack.popPose();
                }
                RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
                for (int j = 0; j < (hover && !infoHover ? 2 : 1); j++) {
                    float r = sign.getRed(), g = sign.getGreen(), b = sign.getBlue();
                    RenderUtil.litQuad(mStack, bufferSource, xx + 12, yy + 12, 24, 24,
                            sign.getRed(), sign.getGreen(), sign.getBlue(), Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(sign.getSprite()));
                    bufferSource.endBatch();
                }
                RenderSystem.disableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, BACKGROUND);
                gui.blit(mStack, xx + 38, yy + 38, infoHover ? 188 : 176, 48, 12, 14);

                if (infoHover) {
                    gui.renderTooltip(mStack, Component.translatable("eidolon.codex.sign_suffix", Component.translatable(sign.getRegistryName().getNamespace() + ".sign." + sign.getRegistryName().getPath())), mouseX, mouseY);
                }
            }
        }
    }
}
