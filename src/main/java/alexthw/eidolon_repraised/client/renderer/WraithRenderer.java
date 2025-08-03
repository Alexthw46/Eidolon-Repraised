package alexthw.eidolon_repraised.client.renderer;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.ClientRegistry;
import alexthw.eidolon_repraised.client.model.WraithModel;
import alexthw.eidolon_repraised.common.entity.WraithEntity;
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
        return ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/entity/wraith.png" );
    }
}
