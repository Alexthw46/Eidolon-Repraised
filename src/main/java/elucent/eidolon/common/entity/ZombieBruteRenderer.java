package elucent.eidolon.common.entity;

import elucent.eidolon.ClientRegistry;
import elucent.eidolon.Eidolon;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ZombieBruteRenderer extends MobRenderer<ZombieBruteEntity, ZombieBruteModel> {
    public ZombieBruteRenderer(Context erm) {
        super(erm, new ZombieBruteModel(erm.bakeLayer(ClientRegistry.ZOMBIE_BRUTE_LAYER)), 0.6f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ZombieBruteEntity entity) {
        return new ResourceLocation(Eidolon.MODID, "textures/entity/zombie_brute.png");
    }
}
