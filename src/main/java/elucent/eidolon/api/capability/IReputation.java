package elucent.eidolon.api.capability;

import elucent.eidolon.api.deity.Deity;
import elucent.eidolon.capability.ReputationEntry;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.common.spell.PrayerSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IReputation {

    double getReputation(UUID player, ResourceLocation deity);
    void addReputation(UUID player, ResourceLocation deity, double amount);
    void subtractReputation(UUID player, ResourceLocation deity, double amount);
    void setReputation(UUID player, ResourceLocation deity, double amount);
    boolean isLocked(UUID player, ResourceLocation deity);
    boolean hasLock(UUID player, ResourceLocation deity, ResourceLocation lock);
    void lock(UUID player, ResourceLocation deity, ResourceLocation key);
    boolean unlock(UUID player, ResourceLocation deity, ResourceLocation key);

    void pray(UUID player, PrayerSpell spell, long time);

    boolean canPray(UUID player, PrayerSpell spell, long time);

    default double getReputation(Player player, ResourceLocation deity) {
        return getReputation(player.getUUID(), deity);
    }

    default void considerChange(Player player, ResourceLocation deity, double prev) {
        double amount = getReputation(player, deity);
        Deity d = Deities.find(deity);
        if (d != null) d.onReputationChange(player, this, prev, amount);
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

    default void pray(Player player, PrayerSpell spell, long time) {
        pray(player.getUUID(), spell, time);
    }

    default boolean canPray(Player player, PrayerSpell spell, long time) {
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
