package elucent.eidolon.common.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import elucent.eidolon.Eidolon;
import elucent.eidolon.event.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SoulEnchanterTileRenderer implements BlockEntityRenderer<SoulEnchanterTileEntity> {
    public static final ResourceLocation BOOK_TEXTURE = new ResourceLocation(Eidolon.MODID, "entity/enchanter_book");
    public static final Material BOOK_MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, BOOK_TEXTURE);
    private final BookModel model;

    public SoulEnchanterTileRenderer() {
        model = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(SoulEnchanterTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 0.75D, 0.5D);
        float f = ClientEvents.getClientTicks();
        matrixStackIn.translate(0.0D, 0.1F + Mth.sin(f * 0.1F) * 0.01F, 0.0D);

        float f1;
        for (f1 = tileEntityIn.nextPageAngle - tileEntityIn.pageAngle; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
        }

        while (f1 < -(float) Math.PI) {
            f1 += ((float) Math.PI * 2F);
        }

        float f2 = tileEntityIn.pageAngle + f1 * partialTicks;
        matrixStackIn.mulPose(Axis.YP.rotation(-f2));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(80.0F));
        float f3 = Mth.lerp(partialTicks, tileEntityIn.oFlip, tileEntityIn.flip);
        float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
        float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
        float f6 = Mth.lerp(partialTicks, tileEntityIn.pageTurningSpeed, tileEntityIn.nextPageTurningSpeed);
        this.model.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
        VertexConsumer ivertexbuilder = BOOK_MATERIAL.buffer(bufferIn, RenderType::entitySolid);
        this.model.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
    }
}
