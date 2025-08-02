package elucent.eidolon.common.item.curio;

import elucent.eidolon.registries.Registry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class TerminusMirrorItem extends EidolonCurio {
    public TerminusMirrorItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(TerminusMirrorItem::onDamage);
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            CuriosApi.getCuriosInventory(player).flatMap(inventory -> inventory.findFirstCurio(
                    Registry.TERMINUS_MIRROR.get())).ifPresent((slots) -> {
                if (event.getSource().getDirectEntity() instanceof Projectile) {
                    event.setCanceled(true);
                    if (!event.getEntity().getCommandSenderWorld().isClientSide)
                        event.getEntity().getCommandSenderWorld().playSound(null, event.getEntity().blockPosition(), SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.0f, 0.75f);
                }
            });
        }
    }
}
