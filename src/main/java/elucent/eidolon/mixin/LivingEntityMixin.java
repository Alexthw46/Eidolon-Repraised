package elucent.eidolon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import elucent.eidolon.Registry;
import elucent.eidolon.event.SpeedFactorEvent;
import elucent.eidolon.item.ReaperScytheItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "getSpeedFactor()F", at = @At("RETURN"), cancellable = true)
    private void customGetSpeedFactor(CallbackInfoReturnable<Float> cir) {
        float factor = cir.getReturnValue();
        SpeedFactorEvent event = new SpeedFactorEvent((Entity)(Object)this, factor);
        MinecraftForge.EVENT_BUS.post(event);
        cir.setReturnValue(event.getSpeedFactor());
    }

    @Inject(method = "dropFromLootTable", at = @At("HEAD"), cancellable = true)
    protected void customDropFromLootTable(DamageSource source, boolean hitRecently, CallbackInfo ci) {
        if (((LivingEntity)(Object)this).isInvertedHealAndHarm()
            && (source.getMsgId() == Registry.RITUAL_DAMAGE.getMsgId()
                || source.getEntity() instanceof LivingEntity
                    && ((LivingEntity) source.getEntity()).getMainHandItem().getItem() instanceof ReaperScytheItem)) {
            ci.cancel();
        }
    }
}
