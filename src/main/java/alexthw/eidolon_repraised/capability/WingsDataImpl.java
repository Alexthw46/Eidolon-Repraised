package alexthw.eidolon_repraised.capability;

import alexthw.eidolon_repraised.api.capability.IPlayerData;
import alexthw.eidolon_repraised.common.item.IWingsItem;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.WingsDashPacket;
import alexthw.eidolon_repraised.network.WingsFlapPacket;
import alexthw.eidolon_repraised.registries.EidolonAttachments;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class WingsDataImpl implements IPlayerData {
    WingsData wingsData;
    LivingEntity entity;

    public WingsDataImpl(LivingEntity entity) {
        this.entity = entity;
        this.wingsData = entity.getData(EidolonAttachments.WINGS_ATTACHMENT);
    }

    static public class WingsData implements INBTSerializable<CompoundTag> {

        public int wingCharges;
        public int dashTicks;
        public long lastFlapTime;
        public long flightStartTime;
        public boolean isFlying;

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("wingCharges", wingCharges);
            tag.putInt("dashTicks", dashTicks);
            tag.putLong("lastFlapTime", lastFlapTime);
            tag.putLong("flightStartTime", flightStartTime);
            tag.putBoolean("isFlying", isFlying);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
            wingCharges = nbt.getInt("wingCharges");
            dashTicks = nbt.getInt("dashTicks");
            lastFlapTime = nbt.getLong("lastFlapTime");
            flightStartTime = nbt.getLong("flightStartTime");
            isFlying = nbt.getBoolean("isFlying");
        }

    }

    @Override
    public int getWingCharges(Player player) {
        return wingsData.wingCharges;
    }

    @Override
    public void rechargeWings(Player player) {
        wingsData.wingCharges = getMaxWingCharges(player);
        entity.setData(EidolonAttachments.WINGS_ATTACHMENT, wingsData);
    }

    @Override
    public boolean tryFlapWings(Player player) {
        if (wingsData.wingCharges <= 0 || getWingsItem(player).isEmpty()) {
            wingsData.wingCharges = 0;
            entity.setData(EidolonAttachments.WINGS_ATTACHMENT, wingsData);
            return false;
        } else if (canFlap(player)) {
            wingsData.wingCharges--;
            ItemStack wings = getWingsItem(player);
            if (wings.getItem() instanceof IWingsItem i) {
                if (isDashing(player)) {
                    i.onDashFlap(player, player.level(), wings, getDashTicks(player));
                } else {
                    i.onFlap(player, player.level(), wings, getDashTicks(player));
                }
                wingsData.lastFlapTime = player.level().getGameTime();
                startFlying(player);
                if (player.level().isClientSide) Networking.sendToServer(new WingsFlapPacket(player));
            }
            entity.setData(EidolonAttachments.WINGS_ATTACHMENT, wingsData);
            return true;
        }
        return false;
    }

    public int getDashTicks(Player player) {
        return wingsData.dashTicks;
    }

    public void doDashTick(Player player) {
        if (wingsData.dashTicks > 0) {
            ItemStack wings = getWingsItem(player);
            if (wings.getItem() instanceof IWingsItem i) {
                i.onDashTick(player, player.level(), wings, wingsData.dashTicks);
            }
            wingsData.dashTicks--;
            if (wingsData.dashTicks == 0) {
                if (wings.getItem() instanceof IWingsItem i) {
                    i.onDashEnd(player, player.level(), wings);
                }
            }
            entity.setData(EidolonAttachments.WINGS_ATTACHMENT, wingsData);
        }
    }

    public boolean tryDash(Player player) {
        if (wingsData.wingCharges > 0 && canFlap(player)) {
            wingsData.wingCharges--;
            ItemStack wings = getWingsItem(player);
            if (wings.getItem() instanceof IWingsItem i) {
                i.onDashStart(player, player.level(), wings);
                wingsData.lastFlapTime = player.level().getGameTime();
                if (!wingsData.isFlying) wingsData.flightStartTime = wingsData.lastFlapTime;
                wingsData.isFlying = true;
                wingsData.dashTicks = i.getDashTicks(wings);
                entity.setData(EidolonAttachments.WINGS_ATTACHMENT, wingsData);
                if (player.level().isClientSide) Networking.sendToServer(new WingsDashPacket(player));
            }
            return true;
        } else return false;
    }

    @Override
    public long getLastFlapTime(Player player) {
        return wingsData.lastFlapTime;
    }

    @Override
    public boolean isFlying(Player player) {
        return wingsData.isFlying;
    }

    static final AttributeModifier WINGS_SLOWFALL = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "wings_falling"), -0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    @Override
    public void startFlying(Player player) {
        if (!wingsData.isFlying) {
            wingsData.flightStartTime = player.level().getGameTime();
            wingsData.isFlying = true;
            AttributeInstance attr = player.getAttribute(Attributes.GRAVITY);
            if (attr != null && !attr.hasModifier(WINGS_SLOWFALL.id())) attr.addTransientModifier(WINGS_SLOWFALL);
        }
    }

    @Override
    public void stopFlying(Player player) {
        if (wingsData.isFlying) {
            wingsData.isFlying = false;
            if (isDashing(player)) {
                setDashTicks(0);
                ItemStack wings = getWingsItem(player);
                if (wings.getItem() instanceof IWingsItem i) {
                    i.onDashEnd(player, player.level(), wings);
                }
            }
            wingsData.flightStartTime = player.level().getGameTime();
            AttributeInstance attr = player.getAttribute(Attributes.GRAVITY);
            if (attr != null && attr.hasModifier(WINGS_SLOWFALL.id())) attr.removeModifier(WINGS_SLOWFALL);
        }
    }

    @Override
    public void setDashTicks(int ticks) {
        wingsData.dashTicks = ticks;
        entity.setData(EidolonAttachments.WINGS_ATTACHMENT, wingsData);
    }

    @Override
    public void setLastFlapTime(long lastFlapTime) {
        wingsData.lastFlapTime = lastFlapTime;
        entity.setData(EidolonAttachments.WINGS_ATTACHMENT, wingsData);
    }

    @Override
    public long getFlightStartTime(Player player) {
        return wingsData.flightStartTime;
    }
}
