package alexthw.eidolon_repraised.client.renderer;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.ClientRegistry;
import alexthw.eidolon_repraised.client.model.RavenModel;
import alexthw.eidolon_repraised.common.entity.RavenEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RavenRenderer extends MobRenderer<RavenEntity, RavenModel> {
    protected static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/entity/raven.png" );
    public RavenRenderer(Context erm) {
        super(erm, new RavenModel(erm.bakeLayer(ClientRegistry.RAVEN_LAYER)), 0.25f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RavenEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(RavenEntity entitylivingbaseIn, @NotNull PoseStack matrixStackIn, float partialTickTime) {
        float f = 1;
        if (entitylivingbaseIn.isBaby()) {
            f *= 0.5f;
            this.shadowRadius = 0.125F;
        } else {
            this.shadowRadius = 0.25F;
        }

        matrixStackIn.scale(f, f, f);
    }
}
