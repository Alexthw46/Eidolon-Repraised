package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import elucent.eidolon.ClientRegistry;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IKnowledge;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.registries.EidolonSounds;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SignIndexPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_sign_index_page.png");
    final SignEntry[] entries;

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
        this.entries = pages;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean click(CodexGui gui, int x, int y, int mouseX, int mouseY) {
        Player entity = Minecraft.getInstance().player;
        IKnowledge knowledge = entity.getCapability(IKnowledge.INSTANCE, null).resolve().get();
        for (int i = 0; i < entries.length; i++) {
            int xx = x + 8 + (i % 2) * 56, yy = y + 4 + (i / 2) * 52;
            if (knowledge.knowsSign(entries[i].sign) && mouseX >= xx + 38 && mouseY >= yy + 38 && mouseX <= xx + 50 && mouseY <= yy + 50) {
                gui.changeChapter(entries[i].chapter);
                Minecraft.getInstance().player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
                return true;
            } else if (knowledge.knowsSign(entries[i].sign) && mouseX >= xx && mouseX <= xx + 48 && mouseY >= yy && mouseY <= yy + 48) {
                gui.addToChant(entries[i].sign);
                entity.playNotifySound(EidolonSounds.SELECT_RUNE.get(), SoundSource.NEUTRAL, 0.5f, entity.level.random.nextFloat() * 0.25f + 0.75f);
                return true;
            }
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics guiGraphics, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
        Player entity = Minecraft.getInstance().player;
        IKnowledge knowledge = entity.getCapability(IKnowledge.INSTANCE, null).resolve().get();
        var mStack = guiGraphics.pose();
        for (int i = 0; i < entries.length; i++) {
            int xx = x + 8 + (i % 2) * 56, yy = y + 4 + (i / 2) * 52;
            Sign sign = entries[i].sign;
            boolean hover = knowledge.knowsSign(sign) && mouseX >= xx && mouseX <= xx + 48 && mouseY >= yy && mouseY <= yy + 48;
            boolean infoHover = knowledge.knowsSign(sign) && mouseX >= xx + 38 && mouseY >= yy + 38 && mouseX <= xx + 50 && mouseY <= yy + 50;
            guiGraphics.blit(BACKGROUND, xx, yy, knowledge.knowsSign(entries[i].sign) ? 128 : 176, 0, 48, 48);

            if (knowledge.knowsSign(sign)) {
                Tesselator tess = Tesselator.getInstance();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
                if (hover && !infoHover) {
                    mStack.pushPose();
                    mStack.translate(xx + 24, yy + 24, 0);
                    mStack.mulPose(Axis.ZP.rotationDegrees(ClientEvents.getClientTicks() * 1.5f));
                    colorBlit(mStack, -18, -18, 128, 48, 36, 36, 256, 256, sign.getColor());
                    mStack.popPose();
                }
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                for (int j = 0; j < (hover && !infoHover ? 2 : 1); j++) {
                    RenderUtil.litQuad(mStack, MultiBufferSource.immediate(tess.getBuilder()), xx + 12, yy + 12, 24, 24,
                            sign.getRed(), sign.getGreen(), sign.getBlue(), Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(sign.getSprite()));
                    tess.end();
                }
                RenderSystem.disableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                guiGraphics.blit(BACKGROUND, xx + 38, yy + 38, infoHover ? 188 : 176, 48, 12, 14);

                if (infoHover) {
                    guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("eidolon.codex.sign_suffix", Component.translatable(sign.getRegistryName().getNamespace() + ".sign." + sign.getRegistryName().getPath())), mouseX, mouseY);
                }
            }
        }
    }
}
