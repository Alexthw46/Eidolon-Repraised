package elucent.eidolon.client.renderer;

import elucent.eidolon.Eidolon;
import elucent.eidolon.client.model.BruteSkeletonModel;
import elucent.eidolon.common.entity.GiantSkeletonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static elucent.eidolon.client.ClientRegistry.GIANT_SKEL_LAYER;

public class GiantSkeletonRenderer extends HumanoidMobRenderer<GiantSkeletonEntity, BruteSkeletonModel> {

    public GiantSkeletonRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new BruteSkeletonModel(pContext.bakeLayer(GIANT_SKEL_LAYER)), 0.6f);
        this.addLayer(new HumanoidArmorLayer<>(this, new BruteSkeletonModel(pContext.bakeLayer(GIANT_SKEL_LAYER)), new BruteSkeletonModel(pContext.bakeLayer(GIANT_SKEL_LAYER)), pContext.getModelManager()));

    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GiantSkeletonEntity pEntity) {
        return new ResourceLocation(Eidolon.MODID, "textures/entity/giant_skeleton.png");
    }
}
