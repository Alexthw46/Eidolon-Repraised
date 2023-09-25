package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import elucent.eidolon.ClientRegistry;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Rune;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IKnowledge;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.registries.EidolonSounds;
import elucent.eidolon.registries.Runes;
import elucent.eidolon.util.ColorUtil;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RuneIndexPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_rune_index_page.png");
    final Rune[] runes;
    int scroll = 0;

    public static class SignEntry {
        final Chapter chapter;
        final Sign sign;

        public SignEntry(Chapter chapter, Sign sign) {
            this.chapter = chapter;
            this.sign = sign;
        }
    }

    public RuneIndexPage() {
        super(BACKGROUND);
        this.runes = Runes.getRunes().toArray(Rune[]::new);
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean click(CodexGui gui, int x, int y, int mouseX, int mouseY) {
        Player entity = Minecraft.getInstance().player;
        if (entity.getCapability(IKnowledge.INSTANCE).resolve().isEmpty()) return false;
        IKnowledge knowledge = entity.getCapability(IKnowledge.INSTANCE, null).resolve().get();
        for (int i = 0; i < runes.length; i ++) {
            int xx = x + 2 + (i % 6) * 20, yy = y + 2 + (i / 6) * 20;
            if (knowledge.knowsRune(runes[i]) && mouseX >= xx && mouseX <= xx + 16 && mouseY >= yy && mouseY <= yy + 16) {
                //gui.addToChant(runes[i]);
                entity.playNotifySound(EidolonSounds.SELECT_RUNE.get(), SoundSource.NEUTRAL, 0.5f, entity.level().random.nextFloat() * 0.25f + 0.75f);
                return true;
            }
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics guiGraphics, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
        gui.hoveredRune = null;
        PoseStack mStack = guiGraphics.pose();
        Player entity = Minecraft.getInstance().player;
        Optional<IKnowledge> knowledge = entity.getCapability(IKnowledge.INSTANCE, null).resolve();
        for (int i = 0; i < runes.length; i++) {
            int xx = x + 2 + (i % 6) * 20, yy = y + 2 + (i / 6) * 20;
            boolean hover = knowledge.isPresent() && knowledge.get().knowsRune(runes[i]) && mouseX >= xx && mouseX <= xx + 16 && mouseY >= yy && mouseY <= yy + 16;
            if (hover) gui.hoveredRune = runes[i];
            guiGraphics.blit(bg, xx, yy, knowledge.isPresent() && knowledge.get().knowsRune(runes[i]) ? 128 : 148, 0, 20, 20);

            if (knowledge.isPresent() && knowledge.get().knowsRune(runes[i])) {
                Tesselator tess = Tesselator.getInstance();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
                if (hover) {
                    mStack.pushPose();
                    mStack.translate(xx + 10, yy + 10, 0);
                    mStack.mulPose(Axis.ZP.rotationDegrees(ClientEvents.getClientTicks() * 1.5f));
                    mStack.scale(0.5f, 0.5f, 1);
                    colorBlit(mStack, -12, -12, 128, 20, 24, 24, 256, 256, ColorUtil.packColor(255, 255, 255, 255));
                    mStack.popPose();
                }
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                for (int j = 0; j < (hover ? 2 : 1); j++) {
                    RenderUtil.litQuad(mStack, MultiBufferSource.immediate(tess.getBuilder()), xx + 6, yy + 6, 8, 8,
                            1, 1, 1, 0.75f, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(runes[i].getSprite()));
                    tess.end();
                }
                RenderSystem.disableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, BACKGROUND);
            }
        }
    }
}
