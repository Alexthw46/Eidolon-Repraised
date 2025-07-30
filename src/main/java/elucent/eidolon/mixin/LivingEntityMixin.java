package elucent.eidolon.mixin;

import elucent.eidolon.api.capability.IPlayerData;
import elucent.eidolon.registries.EidolonCapabilities;
import elucent.eidolon.util.EntityUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import static elucent.eidolon.util.EntityUtil.THRALL_KEY;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("TAIL"), cancellable = true)
    public void eidolonrepraised$canAttack(LivingEntity pTarget, CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this) instanceof LivingEntity living && pTarget != null) {
            if (EntityUtil.isEnthralled(living)) {
                if (living.isAlliedTo(pTarget)) cir.setReturnValue(false);
                UUID master = living.getPersistentData().getUUID(THRALL_KEY);
                if (living.level().getPlayerByUUID(master) instanceof ServerPlayer player) {
                    LivingEntity lastHurt = player.getLastHurtMob();
                    LivingEntity lastHurtBy = player.getLastHurtByMob();
                    // if the target is not one of the player's last hurt mobs, don't attack
                    if (lastHurt != pTarget && lastHurtBy != pTarget) cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    public void eidolonrepraised$isFallFlying(CallbackInfoReturnable<Boolean> ci) {
        if ((LivingEntity) (Object) this instanceof Player p) {
            IPlayerData wingsData = p.getCapability(EidolonCapabilities.WINGS_CAPABILITY);
            if (wingsData != null && wingsData.isDashing(p)) ci.setReturnValue(true);
        }
    }
}
