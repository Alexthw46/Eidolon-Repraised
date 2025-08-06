package alexthw.eidolon_repraised.common.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class GobletTileRenderer implements BlockEntityRenderer<GobletTileEntity> {

    public GobletTileRenderer() {
    }

    @Override
    public void render(GobletTileEntity tile, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();

        if (tile.getEntityType() != null) {
            TextureAtlasSprite water = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(IClientFluidTypeExtensions.of(Fluids.WATER).getStillTexture());
            VertexConsumer builder = bufferIn.getBuffer(RenderType.translucentMovingBlock());
            Matrix4f mat = matrixStackIn.last().pose();
            builder.addVertex(mat, 0.375f, 0.46875f, 0.375f).setColor(192, 16, 32, 224).setUv(water.getU(0.375f), water.getV(0.375f)).setLight(combinedLightIn).setNormal(0, 1, 0);
            builder.addVertex(mat, 0.375f, 0.46875f, 0.625f).setColor(192, 16, 32, 224).setUv(water.getU(0.625f), water.getV(0.375f)).setLight(combinedLightIn).setNormal(0, 1, 0);
            builder.addVertex(mat, 0.625f, 0.46875f, 0.625f).setColor(192, 16, 32, 224).setUv(water.getU(0.625f), water.getV(0.625f)).setLight(combinedLightIn).setNormal(0, 1, 0);
            builder.addVertex(mat, 0.625f, 0.46875f, 0.375f).setColor(192, 16, 32, 224).setUv(water.getU(0.375f), water.getV(0.625f)).setLight(combinedLightIn).setNormal(0, 1, 0);
        }
    }
}
