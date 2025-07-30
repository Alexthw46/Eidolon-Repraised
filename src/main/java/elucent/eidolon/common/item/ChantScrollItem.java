package elucent.eidolon.common.item;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.common.entity.ChantCasterEntity;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.util.ClientInfo;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChantScrollItem extends ItemBase {
    public ChantScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (!pLevel.isClientSide() && hasSpell(pPlayer.getItemInHand(pUsedHand))) {

            List<Sign> spell = getSpell(pPlayer.getItemInHand(pUsedHand));
            if (!spell.isEmpty()) {
                ChantCasterEntity.createChanter(pPlayer, pLevel, spell);
            }

        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public boolean hasSpell(ItemStack stack) {
        var tag = stack.getTag();
        return tag != null && tag.contains("spell");
    }

    public static void setSpell(ItemStack stack, List<Sign> spell) {
        var tag = stack.getOrCreateTag();
        var list = new ListTag();
        for (Sign sign : spell) {
            var signTag = new CompoundTag();
            signTag.putString("id", sign.getRegistryName().toString());
            list.add(signTag);
        }
        tag.put("spell", list);
    }

    public static List<Sign> getSpell(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("spell")) {
            var list = tag.getList("spell", 10);
            return list.stream().map(signTag -> Signs.find(ResourceLocation.tryParse(((CompoundTag) signTag).getString("id")))).toList();
        }
        return new ArrayList<>();
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
            int charge = getSpell(stack).size();
            int rows = (charge + 15) / 8;
            return charge == 0 ? 0 : 12 * rows;
        }

        @Override
        public int getWidth(@NotNull Font font) {
            return maxWidth;
        }

        @Override
        public void renderImage(@NotNull Font pFont, int pX, int pY, @NotNull GuiGraphics pGuiGraphics) {
            List<Sign> spell = getSpell(stack);
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
