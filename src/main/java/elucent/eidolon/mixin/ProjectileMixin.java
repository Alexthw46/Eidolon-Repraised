package elucent.eidolon.mixin;

import elucent.eidolon.util.EntityUtil;
import elucent.eidolon.util.TargetMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity implements TargetMode {
    public ProjectileMixin(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Unique
    private Predicate<Entity> eidolonrepraised$targetMode = null;

    @Override
    public void eidolonrepraised$setMode(final Predicate<Entity> targetMode) {
        this.eidolonrepraised$targetMode = targetMode;
    }

    @Override
    public @Nullable Predicate<Entity> eidolonrepraised$getMode() {
        return eidolonrepraised$targetMode;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void eidolonrepraised$moveTowardsTarget(CallbackInfo ci) {
        if (eidolonrepraised$targetMode != null && !onGround()) {
            EntityUtil.moveTowardsTarget(this);
        }
    }
}
