package elucent.eidolon.mixin;

import elucent.eidolon.common.entity.RavenVariantLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructor(CallbackInfo ci) {
         ((PlayerRenderer)(Object)this).addLayer(new RavenVariantLayer<>(((PlayerRenderer)(Object)this)));
    }
}
