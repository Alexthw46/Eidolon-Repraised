package elucent.eidolon.common.item.curio;

import elucent.eidolon.common.item.ItemBase;
import elucent.eidolon.registries.Registry;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.CuriosApi;

public class WardedMailItem extends ItemBase {
    public WardedMailItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(WardedMailItem::onDamage);
    }

    @SubscribeEvent
    public static void onDamage(LivingAttackEvent event) {
        if (event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), Registry.WARDED_MAIL.get()).ifPresent((slots) -> {

                event.setCanceled(true);
                event.getEntity().hurt(new DamageSource(event.getEntity().damageSources().generic().typeHolder()), event.getAmount());

            });
        }
    }
}
