package elucent.eidolon.common.potion;

import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMobEffectExtension;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

public class ChilledEffect extends MobEffect implements IMobEffectExtension {
    static int packColor(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public ChilledEffect() {
        super(MobEffectCategory.HARMFUL, packColor(255, 147, 189, 245));
        NeoForge.EVENT_BUS.addListener(this::chill);
    }

    @SubscribeEvent
    public void chill(LivingHealEvent event) {
        LivingEntity e = event.getEntity();
        if (e.hasEffect(EidolonPotions.CHILLED_EFFECT)) {
            event.setCanceled(true);
        }
    }
}
