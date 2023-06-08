package elucent.eidolon.common.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import elucent.eidolon.ClientRegistry;
import elucent.eidolon.Eidolon;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RavenRenderer extends MobRenderer<RavenEntity, RavenModel> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Eidolon.MODID, "textures/entity/raven.png");
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
