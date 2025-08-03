package alexthw.eidolon_repraised.common.item.curio;

import alexthw.eidolon_repraised.common.entity.SpellProjectileEntity;
import alexthw.eidolon_repraised.registries.EidolonDataComponents;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public class VoidAmuletItem extends EidolonCurio {
    public VoidAmuletItem(Properties properties) {
        super(properties.component(EidolonDataComponents.COOLDOWN, 0));
        NeoForge.EVENT_BUS.addListener(VoidAmuletItem::onDamage);
    }

    static int getCooldown(ItemStack stack) {
        return stack.getOrDefault(EidolonDataComponents.COOLDOWN, 0);

    }

    static void setCooldown(ItemStack stack, int cooldown) {
        stack.set(EidolonDataComponents.COOLDOWN, cooldown);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide) {
            if (getCooldown(stack) > 0) setCooldown(stack, getCooldown(stack) - 1);
        }
    }


    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            CuriosApi.getCuriosInventory(player).flatMap(inventory -> inventory.findFirstCurio(
                    Registry.VOID_AMULET.get())).ifPresent((slots) -> {
                ItemStack stack = slots.stack();
                if (getCooldown(stack) == 0 && (event.getSource().getDirectEntity() instanceof Projectile
                        || event.getSource().getDirectEntity() instanceof SpellProjectileEntity)) {
                    event.setCanceled(true);
                    if (!event.getEntity().getCommandSenderWorld().isClientSide)
                        event.getEntity().getCommandSenderWorld().playSound(null, event.getEntity().blockPosition(), SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.0f, 0.75f);
                    setCooldown(stack, 20 * 5);
                }
            });
        }
    }
}
