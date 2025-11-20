package alexthw.eidolon_repraised.common.item;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.client.ClientRegistry;
import alexthw.eidolon_repraised.common.entity.ChantCasterEntity;
import alexthw.eidolon_repraised.registries.EidolonDataComponents;
import alexthw.eidolon_repraised.util.ClientInfo;
import alexthw.eidolon_repraised.util.RenderUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChantScrollItem extends ItemBase {
    public ChantScrollItem(Properties properties) {
        super(properties.durability(20));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (!pLevel.isClientSide() && pPlayer.getItemInHand(pUsedHand).has(EidolonDataComponents.SPELL)) {
            pPlayer.startUsingItem(pUsedHand);
            return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 16;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
        if (!(livingEntity instanceof Player pPlayer)) return;

        List<Sign> spell = stack.getOrDefault(EidolonDataComponents.SPELL, List.of());
        if (!spell.isEmpty()) {
            ChantCasterEntity.createChanter(pPlayer, level, spell);
            stack.hurtAndBreak(1, pPlayer, EquipmentSlot.MAINHAND);
            return;
        }

        super.releaseUsing(stack, level, livingEntity, timeCharged);
    }

    public static class ChantTooltipComponent implements ClientTooltipComponent {

        final ItemStack stack;
        final int maxWidth;

        public ChantTooltipComponent(ChantTooltipInfo info) {
            this.stack = info.stack;
            this.maxWidth = info.maxWidth;
        }

        @Override
        public int getHeight() {
            int charge = stack.getOrDefault(EidolonDataComponents.SPELL, List.of()).size();
            int rows = (charge + 15) / 8;
            return charge == 0 ? 0 : 12 * rows;
        }

        @Override
        public int getWidth(@NotNull Font font) {
            return maxWidth;
        }

        @Override
        public void renderImage(@NotNull Font pFont, int pX, int pY, @NotNull GuiGraphics pGuiGraphics) {
            List<Sign> spell = stack.getOrDefault(EidolonDataComponents.SPELL, List.of());
            if (spell.isEmpty()) return;
            for (int i = 0, spellSize = spell.size(); i < spellSize; i++) {
                Sign sign = spell.get(i);
                var mStack = pGuiGraphics.pose();
                //render the translucent sign
                MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                float flicker = 0.75f + 0.05f * (float) Math.sin(Math.toRadians(12 * ClientInfo.getClientPartialTicks() - 360.0f * i / spell.size()));
                for (int j = 0; j < 2; j++) {
                    RenderUtil.litQuad(mStack, bufferSource, 2 + pX + 17 * (i % 7), pY + 16 * (int) (i / 7F), 16, 16,
                            sign.getRed() * flicker, sign.getGreen() * flicker, sign.getBlue() * flicker, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(sign.sprite()));
                    bufferSource.endBatch();
                }
                RenderSystem.disableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
            }

        }
    }

    public record ChantTooltipInfo(ItemStack stack, int maxWidth) implements TooltipComponent {
    }

}
