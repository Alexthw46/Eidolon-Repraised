package elucent.eidolon.common.item.curio;

import elucent.eidolon.api.capability.ISoul;
import elucent.eidolon.common.item.ItemBase;
import elucent.eidolon.network.Networking;
import elucent.eidolon.network.SoulUpdatePacket;
import elucent.eidolon.registries.EidolonCapabilities;
import elucent.eidolon.registries.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SoulboneAmuletItem extends ItemBase implements ICurioItem {
    public SoulboneAmuletItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(SoulboneAmuletItem::onKill);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity e) {
            if (CuriosApi.getCuriosInventory(e).flatMap(i -> i.findFirstCurio(Registry.SOULBONE_AMULET.get())).isPresent()) {
                var cap = e.getCapability(EidolonCapabilities.SOUL_HEART_CAPABILITY);
                if (cap == null) return;
                cap.setMaxEtherealHealth(Math.max(Math.min(ISoul.getPersistentHealth(e), cap.getMaxEtherealHealth()), 2 * Mth.floor((cap.getEtherealHealth() + 3) / 2)));
                cap.setEtherealHealth(cap.getEtherealHealth() + 2);
                if (e instanceof ServerPlayer serverPlayer)
                    Networking.sendToPlayerClient(new SoulUpdatePacket(e), serverPlayer);
            }
        }
    }
}
