package elucent.eidolon.common.item.curio;

import elucent.eidolon.registries.Registry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class TerminusMirrorItem extends EidolonCurio {
    public TerminusMirrorItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(TerminusMirrorItem::onDamage);
    }

    @SubscribeEvent
    public static void onDamage(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player) {
            CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), Registry.TERMINUS_MIRROR.get()).ifPresent((slots) -> {
                ItemStack stack = slots.stack();
                if (event.getSource().getDirectEntity() instanceof Projectile) {
                    event.setCanceled(true);
                    if (!event.getEntity().getCommandSenderWorld().isClientSide)
                        event.getEntity().getCommandSenderWorld().playSound(null, event.getEntity().blockPosition(), SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.0f, 0.75f);
                }
            });
        }
    }
}
