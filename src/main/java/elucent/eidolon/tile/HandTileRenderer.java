package elucent.eidolon.tile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import com.mojang.math.Vector3f;

public class HandTileRenderer implements BlockEntityRenderer<HandTileEntity> {
    public HandTileRenderer() {}

    @Override
    public void render(HandTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer ir = mc.getItemRenderer();
        if (!tileEntityIn.stack.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5,0.9375, 0.5);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(3 * (mc.level.getGameTime() % 360 + partialTicks)));
            ir.renderStatic(tileEntityIn.stack, TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
    }
}
