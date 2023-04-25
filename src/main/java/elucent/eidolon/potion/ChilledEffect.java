package elucent.eidolon.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMobEffect;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChilledEffect extends MobEffect implements IForgeMobEffect {
    static int packColor(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public ChilledEffect() {
        super(MobEffectCategory.HARMFUL, packColor(255, 147, 189, 245));
        MinecraftForge.EVENT_BUS.addListener(this::chill);
    }

    @SubscribeEvent
    public void chill(LivingHealEvent event) {
        LivingEntity e = event.getEntity();
        if (e.hasEffect(this)) {
            event.setCanceled(true);
        }
    }
}
