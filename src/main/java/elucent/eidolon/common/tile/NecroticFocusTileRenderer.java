package elucent.eidolon.common.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import elucent.eidolon.common.block.HorizontalBlockBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;

public class NecroticFocusTileRenderer implements BlockEntityRenderer<NecroticFocusTileEntity> {
    public NecroticFocusTileRenderer() {}

    @Override
    public void render(NecroticFocusTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer ir = mc.getItemRenderer();
        if (!tileEntityIn.stack.isEmpty()) {
            matrixStackIn.pushPose();
            Direction dir = tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalBlockBase.HORIZONTAL_FACING);
            matrixStackIn.translate(0.5 + dir.getStepX() * 0.25,0.5 + dir.getStepY() * 0.25, 0.5 + dir.getStepZ() * 0.25);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(3 * (mc.level.getGameTime() % 360 + partialTicks)));
            ir.renderStatic(tileEntityIn.stack, TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
    }
}
