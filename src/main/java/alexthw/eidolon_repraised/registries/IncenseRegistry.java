package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.api.ritual.IncenseRitual;
import alexthw.eidolon_repraised.common.incense.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static alexthw.eidolon_repraised.Eidolon.prefix;

public class IncenseRegistry {

    static final Map<ResourceLocation, Supplier<IncenseRitual>> incenses = new HashMap<>();
    static final Map<Item, ResourceLocation> itemToIncense = new HashMap<>();
    public static final ResourceLocation PRAYER_INCENSE = prefix("prayer_incense"); // light prayer, passive regen
    public static final ResourceLocation RESTORATION_INCENSE = prefix("restoration_incense"); // heals living, damages undead
    public static final ResourceLocation GLOOM_INCENSE = prefix("gloom_incense"); // heals undead, damages living
    public static final ResourceLocation DEATH_BANE_INCENSE = prefix("death_bane_incense"); // scares away undead
    public static final ResourceLocation TOUGH_INCENSE = prefix("tough_incense"); // defense up (Reinforced)
    public static final ResourceLocation FRAIL_INCENSE = prefix("frail_incense"); // defense down (Vulnerable)
    public static final ResourceLocation FROSTBIND_INCENSE = prefix("frostbind_incense"); // applies Chilled
    public static final ResourceLocation TETHER_INCENSE = prefix("tether_incense"); // prevents teleporting (Anchored)
    public static final ResourceLocation PURITY_INCENSE = prefix("purity_incense"); // cures negative effects
    public static final ResourceLocation QUICKEN_INCENSE = prefix("quicken_incense"); // movement speed boost
    public static final ResourceLocation BLOODLUST_INCENSE = prefix("bloodlust_incense"); // melee damage boost
    public static final ResourceLocation SOUL_HARVEST_INCENSE = prefix("soul_harvest_incense"); // looting/xp boost
    public static final ResourceLocation WARDING_INCENSE = prefix("warding_incense"); // reduces magic/projectile damage
    public static final ResourceLocation UNDEATH_INCENSE = prefix("undeath_incense"); // gives Undeath effect
    public static final ResourceLocation GROWTH_INCENSE = prefix("growth_incense"); // growth boost

    public static void register(Item incense, ResourceLocation name, Supplier<IncenseRitual> incenseRitualSupplier) {
        assert name != null;
        itemToIncense.put(incense, name);
        incenses.put(name, incenseRitualSupplier);
    }

    public static IncenseRitual getIncenseRitual(Item item) {
        if (!itemToIncense.containsKey(item)) return null;
        return getIncenseRitual(itemToIncense.get(item));
    }

    public static IncenseRitual getIncenseRitual(ResourceLocation name) {
        return incenses.get(name).get();
    }

    public static void init() {
        register(Registry.OFFERING_INCENSE.get(), PRAYER_INCENSE, () -> new PrayerIncense(PRAYER_INCENSE));
        register(Registry.GLOOM_INCENSE.get(), GLOOM_INCENSE, () -> new GloomIncense(GLOOM_INCENSE));
        register(Registry.RESTORATION_INCENSE.get(), RESTORATION_INCENSE, () -> new RestorationIncense(RESTORATION_INCENSE));
        register(Registry.DEATH_BANE_INCENSE.get(), DEATH_BANE_INCENSE, () -> new DeathBaneIncense(DEATH_BANE_INCENSE));
        register(Registry.UNDEATH_INCENSE.get(), UNDEATH_INCENSE, () -> new UndeathIncense(UNDEATH_INCENSE));
        register(Registry.FROSTBIND_INCENSE.get(), FROSTBIND_INCENSE, () -> new FrostbindIncense(FROSTBIND_INCENSE));
        register(Registry.TETHER_INCENSE.get(), TETHER_INCENSE, () -> new TetherIncense(TETHER_INCENSE));
        register(Registry.PURITY_INCENSE.get(), PURITY_INCENSE, () -> new PurityIncense(PURITY_INCENSE));
        register(Registry.QUICKEN_INCENSE.get(), QUICKEN_INCENSE, () -> new QuickenIncense(QUICKEN_INCENSE));
        register(Registry.BLOODLUST_INCENSE.get(), BLOODLUST_INCENSE, () -> new BloodlustIncense(BLOODLUST_INCENSE));
        register(Registry.SOUL_HARVEST_INCENSE.get(), SOUL_HARVEST_INCENSE, () -> new SoulHarvestIncense(SOUL_HARVEST_INCENSE));
        //register(Registry.WARDING_INCENSE.get(), WARDING_INCENSE, () -> new WardingIncense(WARDING_INCENSE));
        register(Registry.FRAIL_INCENSE.get(), FRAIL_INCENSE, () -> new FrailIncense(FRAIL_INCENSE));
        register(Registry.TOUGH_INCENSE.get(), TOUGH_INCENSE, () -> new ToughIncense(TOUGH_INCENSE));
    }

}
