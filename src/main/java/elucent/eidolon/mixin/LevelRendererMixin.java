package elucent.eidolon.mixin;

import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 1))
    private void eidolonrepraised$customRenderLevel(CallbackInfo ci) {
        ClientEvents.getDelayedRender().endBatch(RenderUtil.VAPOR_TRANSLUCENT);
        ClientEvents.getDelayedRender().endBatch(RenderUtil.GLOWING_BLOCK_PARTICLE);
    }
    
//    @Inject(method = "renderLevel", at = @At(value = "INVOKE",
//        target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(net/minecraft/client/Camera;)V", ordinal = 0))
//    private void customRenderLevel2(CallbackInfo ci) {
//        ClientEvents.getDelayedRender().endBatch(RenderUtil.VAPOR_TRANSLUCENT);
//        ClientEvents.getDelayedRender().endBatch(RenderUtil.GLOWING_BLOCK_PARTICLE);
//    }
}
