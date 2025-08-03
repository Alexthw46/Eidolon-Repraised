package alexthw.eidolon_repraised.client.renderer;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.ClientRegistry;
import alexthw.eidolon_repraised.client.model.ZombieBruteModel;
import alexthw.eidolon_repraised.common.entity.ZombieBruteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ZombieBruteRenderer extends HumanoidMobRenderer<ZombieBruteEntity, ZombieBruteModel> {
    public ZombieBruteRenderer(Context erm) {
        super(erm, new ZombieBruteModel(erm.bakeLayer(ClientRegistry.ZOMBIE_BRUTE_LAYER)), 0.6f);
        this.addLayer(new HumanoidArmorLayer<>(this, new ZombieBruteModel(erm.bakeLayer(ClientRegistry.ZOMBIE_BRUTE_LAYER)), new ZombieBruteModel(erm.bakeLayer(ClientRegistry.ZOMBIE_BRUTE_LAYER)), erm.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ZombieBruteEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/entity/zombie_brute.png" );
    }
}
