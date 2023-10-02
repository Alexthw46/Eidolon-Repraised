package elucent.eidolon.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import elucent.eidolon.Eidolon;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.client.model.SlimySlugModel;
import elucent.eidolon.common.entity.SlimySlugEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SlimySlugRenderer extends MobRenderer<SlimySlugEntity, SlimySlugModel> {
    protected static final ResourceLocation SLIMY_TEXTURE = new ResourceLocation(Eidolon.MODID, "textures/entity/slimy_slug.png");
    protected static final ResourceLocation BANANA_TEXTURE = new ResourceLocation(Eidolon.MODID, "textures/entity/banana_slug.png");
    protected static final ResourceLocation BROWN_TEXTURE = new ResourceLocation(Eidolon.MODID, "textures/entity/brown_slug.png");
    public SlimySlugRenderer(Context erm) {
        super(erm, new SlimySlugModel(erm.bakeLayer(ClientRegistry.SLUG_LAYER)), 0.4f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SlimySlugEntity entity) {
        return switch (entity.getEntityData().get(SlimySlugEntity.TYPE)) {
            case 1 -> BANANA_TEXTURE;
            case 2 -> BROWN_TEXTURE;
            default -> SLIMY_TEXTURE;
        };
    }

    @Override
    protected void scale(SlimySlugEntity e, PoseStack matrixStackIn, float partialTickTime) {
    	matrixStackIn.scale(2 / (1 + e.squishAmount), 2 / (1 + e.squishAmount), e.squishAmount);
    }
}
