package elucent.eidolon.client.renderer;

import elucent.eidolon.Eidolon;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.client.model.WraithModel;
import elucent.eidolon.common.entity.WraithEntity;
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
