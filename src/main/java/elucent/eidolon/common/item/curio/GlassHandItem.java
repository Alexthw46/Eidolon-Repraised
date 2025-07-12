package elucent.eidolon.common.item.curio;

import elucent.eidolon.common.item.ItemBase;
import elucent.eidolon.registries.Registry;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class GlassHandItem extends ItemBase {
    public GlassHandItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(GlassHandItem::onHurt);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), Registry.GLASS_HAND.get()).isPresent()) {
            event.setAmount(event.getAmount() * 5);
        }
        if (event.getSource().getEntity() instanceof LivingEntity living &&
            CuriosApi.getCuriosHelper().findFirstCurio(living, Registry.GLASS_HAND.get()).isPresent()) {
            event.setAmount(event.getAmount() * 2);
        }
    }

}
