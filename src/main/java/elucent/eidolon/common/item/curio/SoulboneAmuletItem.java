package elucent.eidolon.common.item.curio;

import elucent.eidolon.Registry;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.common.item.ItemBase;
import elucent.eidolon.network.Networking;
import elucent.eidolon.network.SoulUpdatePacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SoulboneAmuletItem extends ItemBase implements ICurioItem {
    public SoulboneAmuletItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.addListener(SoulboneAmuletItem::onKill);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity e) {
            if (CuriosApi.getCuriosHelper().findFirstCurio(e, Registry.SOULBONE_AMULET.get()).isPresent()) {
                e.getCapability(ISoul.INSTANCE).ifPresent((cap) -> {
                    cap.setMaxEtherealHealth(Math.max(Math.min(ISoul.getPersistentHealth(e), cap.getMaxEtherealHealth()), 2 * (int) Math.floor((cap.getEtherealHealth() + 3) / 2)));
                    cap.setEtherealHealth(cap.getEtherealHealth() + 2);
                    if (!e.level.isClientSide)
                        Networking.sendToTracking(e.level, e.getOnPos(), new SoulUpdatePacket(e));
                });
            }
        }
    }
}
