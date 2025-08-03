package alexthw.eidolon_repraised.common.item.curio;

import alexthw.eidolon_repraised.api.capability.ISoul;
import alexthw.eidolon_repraised.common.item.ItemBase;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.SoulUpdatePacket;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.Registry;
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
