package elucent.eidolon.capability;

import elucent.eidolon.api.deity.Deity;
import elucent.eidolon.common.deity.Deities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IReputation {
    Capability<IReputation> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        final ReputationImpl impl = new ReputationImpl();

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            if (cap == INSTANCE) return (LazyOptional<T>) LazyOptional.of(() -> impl);
            else return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return impl.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            impl.deserializeNBT(nbt);
        }
    }

    double getReputation(UUID player, ResourceLocation deity);
    void addReputation(UUID player, ResourceLocation deity, double amount);
    void subtractReputation(UUID player, ResourceLocation deity, double amount);
    void setReputation(UUID player, ResourceLocation deity, double amount);
    boolean isLocked(UUID player, ResourceLocation deity);
    boolean hasLock(UUID player, ResourceLocation deity, ResourceLocation lock);
    void lock(UUID player, ResourceLocation deity, ResourceLocation key);
    boolean unlock(UUID player, ResourceLocation deity, ResourceLocation key);
    void pray(UUID player, ResourceLocation spell, long time);
    boolean canPray(UUID player, ResourceLocation spell, long time);

    default double getReputation(Player player, ResourceLocation deity) {
        return getReputation(player.getUUID(), deity);
    }

    default void considerChange(Player player, ResourceLocation deity, double prev) {
        double amount = getReputation(player, deity);
        Deity d = Deities.find(deity);
        if (d != null && amount != prev) d.onReputationChange(player, this, prev, amount);
    }

    default void addReputation(Player player, ResourceLocation deity, double amount) {
        double prev = getReputation(player.getUUID(), deity);
        addReputation(player.getUUID(), deity, amount);
        considerChange(player, deity, prev);
    }

    default void subtractReputation(Player player, ResourceLocation deity, double amount) {
        double prev = getReputation(player.getUUID(), deity);
        subtractReputation(player.getUUID(), deity, amount);
        considerChange(player, deity, prev);
    }

    default void setReputation(Player player, ResourceLocation deity, double amount) {
        double prev = getReputation(player.getUUID(), deity);
        setReputation(player.getUUID(), deity, amount);
        considerChange(player, deity, prev);
    }

    default void pray(Player player, ResourceLocation spell, long time) {
        pray(player.getUUID(), spell, time);
    }

    default boolean canPray(Player player, ResourceLocation spell, long time) {
        return player.isCreative() || canPray(player.getUUID(), spell, time);
    }

    Map<UUID, Map<ResourceLocation, Long>> getPrayerTimes();
    Map<UUID, Map<ResourceLocation, ReputationEntry>> getReputationMap();
    default Map<ResourceLocation, Long> getPrayerTimeMap(UUID player) {
        return getPrayerTimes().computeIfAbsent(player, (k) -> new HashMap<>());
    }
    default Map<ResourceLocation, ReputationEntry> getReputationMap(UUID player) {
        return getReputationMap().computeIfAbsent(player, (k) -> new HashMap<>());
    }
}
