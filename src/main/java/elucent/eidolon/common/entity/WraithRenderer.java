package elucent.eidolon.common.entity;

import elucent.eidolon.ClientRegistry;
import elucent.eidolon.Eidolon;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class WraithRenderer extends MobRenderer<WraithEntity, WraithModel> {
    public WraithRenderer(Context erm) {
        super(erm, new WraithModel(erm.bakeLayer(ClientRegistry.WRAITH_LAYER)), 0.45f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull WraithEntity entity) {
        return new ResourceLocation(Eidolon.MODID, "textures/entity/wraith.png");
    }
}
