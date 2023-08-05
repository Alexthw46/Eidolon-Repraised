package elucent.eidolon.common.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class BrazierTileRenderer implements BlockEntityRenderer<BrazierTileEntity> {
    public BrazierTileRenderer() {}

    @Override
    public void render(BrazierTileEntity tileEntityIn, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer ir = mc.getItemRenderer();
        if (!tileEntityIn.stack.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5, 0.9375, 0.5);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(3 * (mc.level.getGameTime() % 360 + partialTicks)));
            ir.renderStatic(tileEntityIn.stack, ItemDisplayContext.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, mc.level, 0);
            matrixStackIn.popPose();
        }

        if (tileEntityIn.ritual != null) {
            MultiBufferSource buffer = ClientEvents.getDelayedRender();

            Ritual ritual = tileEntityIn.ritual;
            float r = ritual.getRed(), g = ritual.getGreen(), b = ritual.getBlue();
            RenderUtil.dragon(matrixStackIn, buffer, 0.5, 3.0, 0.5, 1, r, g, b);
            TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ritual.getSymbol());

            BlockPos pos = tileEntityIn.getBlockPos();
            RenderUtil.litBillboard(matrixStackIn, buffer, 0.5, 3.0, 0.5, r, g, b, sprite);
            RenderUtil.litBillboard(matrixStackIn, buffer, 0.5, 3.0, 0.5, r, g, b, sprite);
        }
    }
}
