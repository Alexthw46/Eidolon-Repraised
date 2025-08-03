package alexthw.eidolon_repraised.common.item.curio;

import alexthw.eidolon_repraised.common.item.ItemBase;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class WardedMailItem extends ItemBase {
    public WardedMailItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(WardedMailItem::onDamage);
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO) && event.getEntity() instanceof Player player) {
            CuriosApi.getCuriosInventory(player).flatMap(inventory -> inventory.findFirstCurio(
                    Registry.WARDED_MAIL.get())).ifPresent((slots) -> {
                event.setCanceled(true);
                event.getEntity().hurt(new DamageSource(event.getEntity().damageSources().generic().typeHolder()), event.getAmount());
            });
        }
    }
}
