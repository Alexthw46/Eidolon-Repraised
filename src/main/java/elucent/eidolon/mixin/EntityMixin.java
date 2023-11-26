package elucent.eidolon.mixin;

import elucent.eidolon.util.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At("TAIL"), cancellable = true)
    public void eidolonrepraised$isAlliedTo(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this) instanceof LivingEntity living && pEntity instanceof LivingEntity target) {
            if (EntityUtil.isEnthralled(living)) {
                if (EntityUtil.isEnthralledBy(living, target) || EntityUtil.sameMaster(living, target)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

}
