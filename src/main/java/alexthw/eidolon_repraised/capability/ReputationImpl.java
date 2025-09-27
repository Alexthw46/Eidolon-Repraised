package alexthw.eidolon_repraised.capability;

import alexthw.eidolon_repraised.api.capability.IReputation;
import alexthw.eidolon_repraised.common.spell.PrayerSpell;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.ReputationUpdatePacket;
import alexthw.eidolon_repraised.registries.EidolonAttachments;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ReputationImpl implements IReputation {
    public static class ReputationData implements INBTSerializable<CompoundTag> {
        private final Map<ResourceLocation, ReputationEntry> reputationMap = new HashMap<>();
        private final Map<ResourceLocation, Long> prayerTimes = new HashMap<>();

        public double getReputation(ResourceLocation deity) {
            return reputationMap.computeIfAbsent(deity, k -> new ReputationEntry()).reputation;
        }

        public void addReputation(ResourceLocation deity, double amount) {
            ReputationEntry entry = reputationMap.computeIfAbsent(deity, k -> new ReputationEntry());
            if (entry.lock == null) entry.reputation += amount;
        }

        public void subtractReputation(ResourceLocation deity, double amount) {
            ReputationEntry entry = reputationMap.computeIfAbsent(deity, k -> new ReputationEntry());
            entry.reputation = Math.max(0, entry.reputation - amount);
        }

        public void setReputation(ResourceLocation deity, double amount) {
            ReputationEntry entry = reputationMap.computeIfAbsent(deity, k -> new ReputationEntry());
            if (entry.lock == null || amount < 0) {
                entry.reputation = amount;
            }
        }

        public boolean isLocked(ResourceLocation deity) {
            return reputationMap.computeIfAbsent(deity, k -> new ReputationEntry()).lock != null;
        }

        public boolean hasLock(ResourceLocation deity, ResourceLocation lock) {
            ResourceLocation l = reputationMap.computeIfAbsent(deity, k -> new ReputationEntry()).lock;
            return l != null && l.equals(lock);
        }

        public void lock(ResourceLocation deity, ResourceLocation key) {
            reputationMap.computeIfAbsent(deity, k -> new ReputationEntry()).lock = key;
        }

        public boolean unlock(ResourceLocation deity, ResourceLocation key) {
            ReputationEntry entry = reputationMap.computeIfAbsent(deity, k -> new ReputationEntry());
            if (entry.lock != null && entry.lock.equals(key)) {
                entry.lock = null;
                return true;
            }
            return false;
        }

        public void pray(PrayerSpell spell, long time) {
            prayerTimes.put(spell.getRegistryName(), time);
        }

        public boolean canPray(PrayerSpell spell, long time) {
            return !prayerTimes.containsKey(spell.getRegistryName()) ||
                    prayerTimes.get(spell.getRegistryName()) < time - spell.getCooldown();
        }

        public Map<ResourceLocation, ReputationEntry> getReputationMap() {
            return reputationMap;
        }

        public Map<ResourceLocation, Long> getPrayerTimes() {
            return prayerTimes;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag data = new CompoundTag();
            CompoundTag reps = new CompoundTag();
            for (Entry<ResourceLocation, ReputationEntry> e : reputationMap.entrySet()) {
                CompoundTag entry = new CompoundTag();
                entry.putDouble("rep", e.getValue().reputation);
                if (e.getValue().lock != null) entry.putString("lock", e.getValue().lock.toString());
                reps.put(e.getKey().toString(), entry);
            }
            CompoundTag times = new CompoundTag();
            for (Entry<ResourceLocation, Long> e : prayerTimes.entrySet()) {
                times.putLong(e.getKey().toString(), e.getValue());
            }
            data.put("reps", reps);
            data.put("times", times);
            return data;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
            reputationMap.clear();
            prayerTimes.clear();
            if (nbt.contains("reps")) {
                CompoundTag reps = nbt.getCompound("reps");
                for (String deity : reps.getAllKeys()) {
                    CompoundTag entry = reps.getCompound(deity);
                    double rep = entry.getDouble("rep");
                    ResourceLocation lock = entry.contains("lock") ? ResourceLocation.parse(entry.getString("lock")) : null;
                    reputationMap.put(ResourceLocation.parse(deity), new ReputationEntry(rep, lock));
                }
            }
            if (nbt.contains("times")) {
                CompoundTag times = nbt.getCompound("times");
                for (String rl : times.getAllKeys()) {
                    prayerTimes.put(ResourceLocation.parse(rl), times.getLong(rl));
                }
            }
        }
    }

    ReputationData reputationData;
    Player player;

    public ReputationImpl(Player player) {
        this.player = player;
        this.reputationData = player.getData(EidolonAttachments.REPUTATION_ATTACHMENT);
    }

    @Override
    public double getReputation(ResourceLocation deity) {
        return reputationData.getReputation(deity);
    }

    @Override
    public void addReputation(ResourceLocation deity, double amount) {
        reputationData.addReputation(deity, amount);
        if (player instanceof ServerPlayer serverPlayer)
            Networking.sendToPlayerClient(new ReputationUpdatePacket(player, false), serverPlayer);
    }

    @Override
    public void subtractReputation(ResourceLocation deity, double amount) {
        reputationData.subtractReputation(deity, amount);
        if (player instanceof ServerPlayer serverPlayer)
            Networking.sendToPlayerClient(new ReputationUpdatePacket(player, false), serverPlayer);
    }

    @Override
    public void setReputation(ResourceLocation deity, double amount) {
        reputationData.setReputation(deity, amount);
        if (player instanceof ServerPlayer serverPlayer)
            Networking.sendToPlayerClient(new ReputationUpdatePacket(player, false), serverPlayer);
    }

    @Override
    public boolean isLocked(Player player, ResourceLocation deity) {
        return reputationData.isLocked(deity);
    }

    @Override
    public boolean hasLock(Player player, ResourceLocation deity, ResourceLocation lock) {
        return reputationData.hasLock(deity, lock);
    }

    @Override
    public void lock(Player player, ResourceLocation deity, ResourceLocation key) {
        reputationData.lock(deity, key);
    }

    @Override
    public boolean unlock(Player player, ResourceLocation deity, ResourceLocation key) {
        return reputationData.unlock(deity, key);
    }

    @Override
    public void pray(PrayerSpell spell, long time) {
        reputationData.pray(spell, time);
    }

    @Override
    public boolean canPray(PrayerSpell spell, long time) {
        return reputationData.canPray(spell, time);
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return reputationData.serializeNBT(provider);
    }

    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        reputationData.deserializeNBT(provider, nbt);
        player.setData(EidolonAttachments.REPUTATION_ATTACHMENT.get(), reputationData);
    }
}
