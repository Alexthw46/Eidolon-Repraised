package elucent.eidolon.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow
    public abstract CompoundTag getShoulderEntityLeft();

    @Shadow
    public abstract CompoundTag getShoulderEntityRight();

    @Inject(at = @At("HEAD"), method = "removeEntitiesOnShoulder", cancellable = true)
    void eidolonrepraised$removeEntitiesOnShoulder(CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            CompoundTag leftShoulder = getShoulderEntityLeft(), rightShoulder = getShoulderEntityRight();
            if (leftShoulder != null) {
                if (leftShoulder.getString("id").equals("eidolon:raven") && !player.isShiftKeyDown()) {
                    ci.cancel();
                }
            }
            if (rightShoulder != null) {
                if (rightShoulder.getString("id").equals("eidolon:raven") && !player.isShiftKeyDown()) {
                    ci.cancel();
                }
            }
        }
    }

}
