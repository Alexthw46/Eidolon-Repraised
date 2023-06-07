package elucent.eidolon.common.item.curio;

import elucent.eidolon.common.entity.SpellProjectileEntity;
import elucent.eidolon.registries.Registry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public class VoidAmuletItem extends EidolonCurio {
    public VoidAmuletItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.addListener(VoidAmuletItem::onDamage);
    }

    static int getCooldown(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null && tag.contains("cooldown")) {
            return tag.getInt("cooldown");
        }
        return 0;
    }

    static void setCooldown(ItemStack stack, int cooldown) {
        stack.getOrCreateTag().putInt("cooldown", cooldown);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!slotContext.entity().level.isClientSide) {
            if (getCooldown(stack) > 0) setCooldown(stack, getCooldown(stack) - 1);
        }
    }


    @SubscribeEvent
    public static void onDamage(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            CuriosApi.getCuriosHelper().findFirstCurio(player, Registry.VOID_AMULET.get()).ifPresent((stack) -> {
                if (getCooldown(stack.stack()) == 0) {
                    if (event.getSource().getDirectEntity() instanceof Projectile
                        || event.getSource().getDirectEntity() instanceof SpellProjectileEntity) {
                        event.setCanceled(true);
                        if (!event.getEntity().getCommandSenderWorld().isClientSide)
                            event.getEntity().getCommandSenderWorld().playSound(null, event.getEntity().blockPosition(), SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.0f, 0.75f);
                        setCooldown(stack.stack(), 20 * 5);
                    }
                }
            });
        }
    }
}
