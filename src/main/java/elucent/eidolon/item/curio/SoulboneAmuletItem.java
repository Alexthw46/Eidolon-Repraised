package elucent.eidolon.item.curio;

import elucent.eidolon.Registry;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.item.ItemBase;
import elucent.eidolon.network.Networking;
import elucent.eidolon.network.SoulUpdatePacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.world.item.Item.Properties;

public class SoulboneAmuletItem extends ItemBase {
    public SoulboneAmuletItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.addListener(SoulboneAmuletItem::onKill);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
        return new EidolonCurio(stack) {
            @Override
            public boolean canRightClickEquip() {
                return true;
            }
        };
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity e) {
            if (CuriosApi.getCuriosHelper().findEquippedCurio(Registry.SOULBONE_AMULET.get(), e).isPresent()) {
                e.getCapability(ISoul.INSTANCE).ifPresent((cap) -> {
                    cap.setMaxEtherealHealth(Math.max(Math.min(ISoul.getPersistentHealth(e), cap.getMaxEtherealHealth()), 2 * (int)Math.floor((cap.getEtherealHealth() + 3) / 2)));
                    cap.setEtherealHealth(cap.getEtherealHealth() + 2);
                    if (!e.level.isClientSide) Networking.sendToTracking(e.level, e.getOnPos(), new SoulUpdatePacket(e));
                });
            }
        }
    }
}
