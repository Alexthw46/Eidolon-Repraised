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
    private Predicate<Entity> eidolon$targetMode;

    @Override
    public void eidolon$setMode(final Predicate<Entity> targetMode) {
        this.eidolon$targetMode = targetMode;
    }

    @Override
    public @Nullable Predicate<Entity> eidolon$getMode() {
        return eidolon$targetMode;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void moveTowardsTarget(CallbackInfo ci) {
        if (!isOnGround()) {
            EntityUtil.moveTowardsTarget(this);
        }
    }
}
