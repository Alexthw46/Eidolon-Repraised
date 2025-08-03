package alexthw.eidolon_repraised.api.capability;

import alexthw.eidolon_repraised.api.deity.Deity;
import alexthw.eidolon_repraised.common.deity.Deities;
import alexthw.eidolon_repraised.common.spell.PrayerSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface IReputation {

    double getReputation(ResourceLocation deity);

    void addReputation(ResourceLocation deity, double amount);

    void subtractReputation(ResourceLocation deity, double amount);

    void setReputation(ResourceLocation deity, double amount);

    boolean isLocked(Player player, ResourceLocation deity);

    boolean hasLock(Player player, ResourceLocation deity, ResourceLocation lock);

    void lock(Player player, ResourceLocation deity, ResourceLocation key);

    boolean unlock(Player player, ResourceLocation deity, ResourceLocation key);

    void pray(PrayerSpell spell, long time);

    boolean canPray(PrayerSpell spell, long time);

    default void considerChange(Player player, ResourceLocation deity, double prev) {
        double amount = getReputation(deity);
        Deity d = Deities.find(deity);
        if (d != null) d.onReputationChange(player, this, prev, amount);
    }
}
