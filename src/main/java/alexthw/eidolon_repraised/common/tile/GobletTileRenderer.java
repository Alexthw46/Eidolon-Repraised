package alexthw.eidolon_repraised.common.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class GobletTileRenderer implements BlockEntityRenderer<GobletTileEntity> {

    public GobletTileRenderer() {}

    @Override
    public void render(GobletTileEntity tile, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();

        if (tile.getEntityType() != null) {
            TextureAtlasSprite water = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(ResourceLocation.fromNamespaceAndPath("minecraft","block/water_still" ));
            VertexConsumer builder = bufferIn.getBuffer(RenderType.translucent());
            Matrix4f mat = matrixStackIn.last().pose();
            builder.addVertex(mat, 0.375f, 0.46875f, 0.375f).setColor(192, 16, 32, 224).setUv(water.getU(6), water.getV(6)).setLight(combinedLightIn).setNormal(0, 1, 0);
            builder.addVertex(mat, 0.375f, 0.46875f, 0.625f).setColor(192, 16, 32, 224).setUv(water.getU(10), water.getV(6)).setLight(combinedLightIn).setNormal(0, 1, 0);
            builder.addVertex(mat, 0.625f, 0.46875f, 0.625f).setColor(192, 16, 32, 224).setUv(water.getU(10), water.getV(10)).setLight(combinedLightIn).setNormal(0, 1, 0);
            builder.addVertex(mat, 0.625f, 0.46875f, 0.375f).setColor(192, 16, 32, 224).setUv(water.getU(6), water.getV(10)).setLight(combinedLightIn).setNormal(0, 1, 0);
        }
    }
}
