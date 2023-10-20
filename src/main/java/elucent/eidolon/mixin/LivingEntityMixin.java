package elucent.eidolon.mixin;

import elucent.eidolon.Eidolon;
import elucent.eidolon.capability.IPlayerData;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "getMobType", at = @At("HEAD"), cancellable = true)
    public void eidolonrepraised$getMobType(CallbackInfoReturnable<MobType> ci) {
        //doublecast to not make the IDE freak out, activeEffectsMaps can be not initialized for reasons
        if (((Entity) (Object) this) instanceof LivingEntity living && living.getActiveEffectsMap() != null && living.hasEffect(EidolonPotions.UNDEATH_EFFECT.get()) && !Eidolon.trueMobType) {
            ci.setReturnValue(MobType.UNDEAD);
        }
    }

    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    public void eidolonrepraised$isFallFlying(CallbackInfoReturnable<Boolean> ci) {
       if ((LivingEntity)(Object)this instanceof Player p) {
    	   Optional<IPlayerData> opt = p.getCapability(IPlayerData.INSTANCE).resolve();
    	   if (opt.isPresent() && opt.get().isDashing(p)) ci.setReturnValue(true);
       }
    }
}
