package elucent.eidolon.common.item.curio;

import elucent.eidolon.common.item.ItemBase;
import elucent.eidolon.registries.Registry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class WardedMailItem extends ItemBase {
    public WardedMailItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.addListener(WardedMailItem::onDamage);
    }

    @SubscribeEvent
    public static void onDamage(LivingAttackEvent event) {
        if (event.getSource().isMagic()) {
            CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), Registry.WARDED_MAIL.get()).ifPresent((slots) -> {

                event.setCanceled(true);
                event.getEntity().hurt(new DamageSource(event.getSource().getMsgId()), event.getAmount());

            });
        }
    }
}
