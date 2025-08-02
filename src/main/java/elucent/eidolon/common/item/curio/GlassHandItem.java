package elucent.eidolon.common.item.curio;

import elucent.eidolon.common.item.ItemBase;
import elucent.eidolon.registries.Registry;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class GlassHandItem extends ItemBase {
    public GlassHandItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(GlassHandItem::onHurt);
    }

    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Pre event) {
        if (CuriosApi.getCuriosInventory(event.getEntity()).flatMap(i -> i.findFirstCurio(Registry.GLASS_HAND.get())).isPresent()) {
            event.setNewDamage(event.getNewDamage() * 5);
        }
        if (event.getSource().getEntity() instanceof LivingEntity living &&
                CuriosApi.getCuriosInventory(living).flatMap(i -> i.findFirstCurio(Registry.GLASS_HAND.get())).isPresent()) {
            event.setNewDamage(event.getNewDamage() * 2);
        }
    }

}
