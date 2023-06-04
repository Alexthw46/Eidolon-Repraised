package elucent.eidolon.common.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import elucent.eidolon.ClientRegistry;
import elucent.eidolon.registries.Entities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class RavenVariantLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
	private RavenModel model = null;
	
    public RavenVariantLayer(RenderLayerParent<T, PlayerModel<T>> rendererIn) {
        super(rendererIn);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderRaven(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, netHeadYaw, headPitch, true);
        this.renderRaven(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, netHeadYaw, headPitch, false);
    }

    private void renderRaven(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, boolean leftShoulderIn) {
        if (model == null) {
            model = new RavenModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientRegistry.RAVEN_LAYER));
        }
    	CompoundTag compoundnbt = leftShoulderIn ? entitylivingbaseIn.getShoulderEntityLeft() : entitylivingbaseIn.getShoulderEntityRight();
        EntityType.byString(compoundnbt.getString("id")).filter((p_215344_0_) -> p_215344_0_ == Entities.RAVEN.get()).ifPresent((p_229137_11_) -> {
            matrixStackIn.pushPose();
            matrixStackIn.translate(leftShoulderIn ? (double)0.4F : (double)-0.4F, entitylivingbaseIn.isCrouching() ? (double)-1.3F : -1.5D, 0.0D);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(model.renderType(RavenRenderer.TEXTURE));
            model.renderOnShoulder(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, limbSwing, limbSwingAmount, netHeadYaw, headPitch, entitylivingbaseIn.tickCount);
            matrixStackIn.popPose();
        });
    }
}
