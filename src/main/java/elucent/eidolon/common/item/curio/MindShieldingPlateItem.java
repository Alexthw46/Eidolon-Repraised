package elucent.eidolon.common.item.curio;

import elucent.eidolon.common.item.ItemBase;
import elucent.eidolon.registries.Registry;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class MindShieldingPlateItem extends ItemBase {
    public MindShieldingPlateItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(MindShieldingPlateItem::onPotion);
        NeoForge.EVENT_BUS.addListener(MindShieldingPlateItem::onClone);
        NeoForge.EVENT_BUS.addListener(MindShieldingPlateItem::onDropXP);
    }

    @SubscribeEvent
    public static void onPotion(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance().getEffect() == MobEffects.CONFUSION && CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), Registry.MIND_SHIELDING_PLATE.get()).isPresent()) {
            event.setResult(Event.Result.DENY);
        }
    }

    static final int LEVEL_FLAG = 1 << 30;

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if ((event.getOriginal().experienceLevel & LEVEL_FLAG) != 0) {
            event.getEntity().experienceLevel = event.getOriginal().experienceLevel & ~LEVEL_FLAG;
            event.getEntity().experienceProgress = event.getOriginal().experienceProgress;
        }
    }

    @SubscribeEvent
    public static void onDropXP(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof Player player && CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), Registry.MIND_SHIELDING_PLATE.get()).isPresent()) {
            player.experienceLevel |= LEVEL_FLAG;
            event.setCanceled(true);
        }
    }

}
