package alexthw.eidolon_repraised.common.tile;

import alexthw.eidolon_repraised.client.ClientRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class CrucibleTileRenderer implements BlockEntityRenderer<CrucibleTileEntity> {
    private final ModelPart stirrer;
    public static final ResourceLocation STIRRER_TEXTURE = ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "textures/block/crucible_stirrer.png");

    public static LayerDefinition createModelLayer() {
        MeshDefinition mesh = new MeshDefinition();

        PartDefinition root = mesh.getRoot();
        PartDefinition stirrer = root.addOrReplaceChild("stirrer", CubeListBuilder.create()
                .texOffs(0, 8).addBox(-1.5f, 0.0F, -1.5f, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0f, 3.0F, -1.0f, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    public CrucibleTileRenderer() {
        this.stirrer = Minecraft.getInstance().getEntityModels().bakeLayer(ClientRegistry.CRUCIBLE_STIRRER_LAYER).getChild("stirrer");
    }

    @Override
    public void render(CrucibleTileEntity tile, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, STIRRER_TEXTURE);
        float coeff = tile.stirTicks == 0 ? 0 : (tile.stirTicks - partialTicks) / 20.0f;
        if (!tile.getLevel().getBlockState(tile.getBlockPos().above()).isFaceSturdy(tile.getLevel(), tile.getBlockPos().above(), Direction.DOWN)) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5, 0.625, 0.5);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(45 + coeff * 360));
            matrixStackIn.translate(0, -0.125 * Math.sin(coeff * 3.141592653589793), 0.125);
            stirrer.xRot = 0.3926991f * (1.0f - (float) Math.sin(coeff * 3.141592653589793));
            stirrer.render(matrixStackIn, bufferIn.getBuffer(RenderType.entitySolid(STIRRER_TEXTURE)), combinedLightIn, combinedOverlayIn);
            matrixStackIn.popPose();
        }
        if (tile.hasWater) {
            TextureAtlasSprite water = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(IClientFluidTypeExtensions.of(Fluids.WATER).getStillTexture());
            VertexConsumer builder = bufferIn.getBuffer(RenderType.translucentMovingBlock());
            Matrix4f mat = matrixStackIn.last().pose();
            int color = BiomeColors.getAverageWaterColor(tile.getLevel(), tile.getBlockPos());
            int r = ARGB32.red(color), g = ARGB32.green(color),
                    b = ARGB32.blue(color), a = ARGB32.alpha(color);

            if (!tile.steps.isEmpty()) {
                r = (int) (tile.getRed() * 255);
                g = (int) (tile.getGreen() * 255);
                b = (int) (tile.getBlue() * 255);
            }
            builder.addVertex(mat, 0.125f, 0.75f, 0.125f).setColor(r, g, b, 192).setUv(water.getU(0.125f), water.getV(0.125f)).setLight(combinedLightIn).setNormal(0, 1, 0);
            builder.addVertex(mat, 0.125f, 0.75f, 0.875f).setColor(r, g, b, 192).setUv(water.getU(0.875f), water.getV(0.125f)).setLight(combinedLightIn).setNormal(0, 1, 0);
            builder.addVertex(mat, 0.875f, 0.75f, 0.875f).setColor(r, g, b, 192).setUv(water.getU(0.875f), water.getV(0.875f)).setLight(combinedLightIn).setNormal(0, 1, 0);
            builder.addVertex(mat, 0.875f, 0.75f, 0.125f).setColor(r, g, b, 192).setUv(water.getU(0.125f), water.getV(0.875f)).setLight(combinedLightIn).setNormal(0, 1, 0);
        }
    }
}
