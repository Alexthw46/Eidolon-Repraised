package alexthw.eidolon_repraised.mixin.compat;

import alexthw.eidolon_repraised.util.EntityUtil;
import alexthw.eidolon_repraised.util.TargetMode;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
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
