package elucent.eidolon.mixin.compat;

import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import elucent.eidolon.util.EntityUtil;
import elucent.eidolon.util.TargetMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySpellArrow.class)
public abstract class SpellArrowMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void eidolonrepraised$moveTowardsTarget(CallbackInfo ci) {
        if ((Entity) (Object) this instanceof Projectile p && this instanceof TargetMode t && t.eidolonrepraised$getMode() != null && !p.onGround()) {
            EntityUtil.moveTowardsTarget(p);
        }
    }


}
