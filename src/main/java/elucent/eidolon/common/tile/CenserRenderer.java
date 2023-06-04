package elucent.eidolon.common.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.jetbrains.annotations.NotNull;

public class CenserRenderer implements BlockEntityRenderer<CenserTileEntity> {
    public CenserRenderer() {
    }

    @Override
    public void render(CenserTileEntity tileEntityIn, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer ir = mc.getItemRenderer();
        if (!tileEntityIn.incense.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5, 0.25, 0.5);
            matrixStackIn.scale(0.75f, 2.5f, 0.75f);
            matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));

            ir.renderStatic(tileEntityIn.incense, ItemTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
    }

}

