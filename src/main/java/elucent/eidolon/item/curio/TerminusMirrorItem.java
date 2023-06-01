package elucent.eidolon.item.curio;

import elucent.eidolon.Registry;
import elucent.eidolon.entity.SpellProjectileEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class TerminusMirrorItem extends EidolonCurio {
    public TerminusMirrorItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.addListener(TerminusMirrorItem::onDamage);
    }

    @SubscribeEvent
    public static void onDamage(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player) {
            CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), Registry.TERMINUS_MIRROR.get()).ifPresent((slots) -> {
                ItemStack stack = slots.stack();
                if ((event.getSource().getDirectEntity() instanceof Projectile
                     || event.getSource().getDirectEntity() instanceof SpellProjectileEntity)) {
                    event.setCanceled(true);
                    if (!event.getEntity().getCommandSenderWorld().isClientSide)
                        event.getEntity().getCommandSenderWorld().playSound(null, event.getEntity().blockPosition(), SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.0f, 0.75f);
                }
            });
        }
    }
}
