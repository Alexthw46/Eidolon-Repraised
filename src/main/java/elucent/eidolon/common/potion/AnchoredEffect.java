package elucent.eidolon.common.potion;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.EidolonPotions;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMobEffectExtension;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class AnchoredEffect extends MobEffect implements IMobEffectExtension {
    public AnchoredEffect() {
        super(MobEffectCategory.BENEFICIAL, ColorUtil.packColor(255, 154, 58, 232));
        NeoForge.EVENT_BUS.addListener(this::anchor);
    }

    @SubscribeEvent
    public void anchor(EntityTeleportEvent event) {
        if (event instanceof EntityTeleportEvent.TeleportCommand) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity living && living.hasEffect(EidolonPotions.ANCHORED_EFFECT)) {
            event.setCanceled(true);
        }
    }

    protected static final ResourceLocation EFFECT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "textures/mob_effect/anchored.png");

}
