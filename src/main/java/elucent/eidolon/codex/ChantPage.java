package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.api.spells.Spell;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.recipe.ChantRecipe;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ChantPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_chant_page.png");
    protected Sign[] chant;
    final String text;
    final String title;
    final Spell spell;

    public ChantPage(String textKey, Spell spell) {
        super(BACKGROUND);
        this.text = textKey;
        this.title = textKey + ".title";
        this.spell = spell;
    }

    @Override
    public void fullRender(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        if (spell != null && chant == null) {
            if (Eidolon.proxy.getWorld().getRecipeManager().byKey(spell.getRegistryName()).orElse(null) instanceof ChantRecipe chantRecipe) {
                chant = chantRecipe.signs();
                if (chant == null || chant.length == 0) {
                    mStack.drawString(gui.getMinecraft().font, "No matching recipe found for " + spell.getRegistryName(), x + 10, y + 10, 0x000000);
                }
            }
        }
        super.fullRender(gui, mStack, x, y, mouseX, mouseY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics guiGraphics, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
        String title = I18n.get(this.title);
        int titleWidth = Minecraft.getInstance().font.width(title);
        PoseStack mStack = guiGraphics.pose();
        drawText(guiGraphics, title, x + 64 - titleWidth / 2, y + 15 - Minecraft.getInstance().font.lineHeight);

        if (chant != null) {
            int w = chant.length * 24;
            int baseX = x + 64 - w / 2;
            CodexGui.blit(guiGraphics, baseX - 16, y + 28, 256, 208, 16, 32, 512, 512);
            for (int i = 0; i < chant.length; i++) {
                CodexGui.blit(guiGraphics, baseX + i * 24, y + 28, 272, 208, 24, 32, 512, 512);
            }
            CodexGui.blit(guiGraphics, baseX + w, y + 28, 296, 208, 16, 32, 512, 512);

            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            RenderSystem.enableBlend();
            for (int i = 0; i < chant.length; i++) {
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                CodexGui.blit(guiGraphics, baseX + i * 24, y + 28, 312, 208, 24, 24, 512, 512);

                Sign sign = chant[i];
                float flicker = 0.875f + 0.125f * (float) Math.sin(Math.toRadians(12 * ClientEvents.getClientTicks()));
                RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderUtil.litQuad(mStack, bufferSource, baseX + i * 24 + 4, y + 32, 16, 16,
                        sign.getRed(), sign.getGreen(), sign.getBlue(), Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(sign.getSprite()));
                RenderUtil.litQuad(mStack, bufferSource, baseX + i * 24 + 4, y + 32, 16, 16,
                        sign.getRed() * flicker, sign.getGreen() * flicker, sign.getBlue() * flicker, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(sign.getSprite()));
            }

            bufferSource.endBatch();
            RenderSystem.disableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }

        drawWrappingText(guiGraphics, I18n.get(text), x + 4, y + 72, 120);
    }
}
