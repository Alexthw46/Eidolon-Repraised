package elucent.eidolon;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Config {
    // mobs
    public static ConfigValue<Integer> WRAITH_SPAWN_WEIGHT, ZOMBIE_BRUTE_SPAWN_WEIGHT, RAVEN_SPAWN_WEIGHT;

    // world
    public static ConfigValue<Integer> LEAD_MIN_Y, LEAD_MAX_Y, LEAD_VEIN_SIZE, LEAD_VEIN_COUNT;
    public static ConfigValue<Integer> SILVER_MIN_Y, SILVER_MAX_Y, SILVER_VEIN_SIZE, SILVER_VEIN_COUNT;
    public static ConfigValue<Double> LAB_RARITY, STRAY_TOWER_RARITY, CATACOMB_RARITY;
    public static ConfigValue<Boolean> LEAD_ENABLED, SILVER_ENABLED,
        LAB_ENABLED, STRAY_TOWER_ENABLED, CATACOMB_ENABLED;

    public Config(ForgeConfigSpec.Builder builder) {
        builder.comment("Mob settings").push("mobs");
        WRAITH_SPAWN_WEIGHT = builder.comment("Spawn weight for wraith entity. Set to zero to disable spawning.")
            .defineInRange("wraithSpawnWeight", 40, 0, 1000);
        ZOMBIE_BRUTE_SPAWN_WEIGHT = builder.comment("Spawn weight for zombie brute entity. Set to zero to disable spawning.")
            .defineInRange("zombieBruteSpawnWeight", 40, 0, 1000);
        RAVEN_SPAWN_WEIGHT = builder.comment("Spawn weight for raven entity. Set to zero to disable spawning.")
            .defineInRange("ravenSpawnWeight", 6, 0, 1000);
        builder.pop();

        builder.comment("World generation settings").push("world");
        LEAD_ENABLED = builder.comment("Whether lead ore is enabled. Set to false to disable spawning.")
            .define("leadEnabled", true);
        LEAD_MIN_Y = builder.comment("Minimum Y value for lead ore veins")
            .defineInRange("leadOreMinY", -27, 0, 254);
        LEAD_MAX_Y = builder.comment("Maximum Y value for lead ore veins")
            .defineInRange("leadOreMaxY", 41, 1, 255);
        LEAD_VEIN_SIZE = builder.comment("Maximum number of blocks per lead ore vein")
            .defineInRange("leadOreVeinSize", 6, 1, 255);
        LEAD_VEIN_COUNT = builder.comment("Number of lead ore veins per chunk")
            .defineInRange("leadOreVeinCount", 6, 0, 255);
        SILVER_ENABLED = builder.comment("Whether silver ore is enabled. Set to false to disable spawning.")
            .define("silverEnabled", true);
        SILVER_MIN_Y = builder.comment("Minimum Y value for silver ore veins")
            .defineInRange("silverOreMinY", -60, 0, 254);
        SILVER_MAX_Y = builder.comment("Maximum Y value for silver ore veins")
            .defineInRange("silverOreMaxY", 33, 1, 255);
        SILVER_VEIN_SIZE = builder.comment("Maximum number of blocks per silver ore vein")
            .defineInRange("silverOreVeinSize", 6, 1, 255);
        SILVER_VEIN_COUNT = builder.comment("Number of silver ore veins per chunk")
            .defineInRange("silverOreVeinCount", 5, 0, 255);
        LAB_ENABLED = builder.comment("Whether the lab structure is enabled. Set to false to disable spawning.")
            .define("labEnabled", true);
        LAB_RARITY = builder.comment("Rarity of the lab structure. Higher numbers mean rarer structures.")
            .defineInRange("labRarity", 4.0f, 1.0f, 1000.0f);
        STRAY_TOWER_ENABLED = builder.comment("Whether the stray tower structure is enabled. Set to false to disable spawning.")
            .define("strayTowerEnabled", true);
        STRAY_TOWER_RARITY = builder.comment("Rarity of the stray tower structure. Higher numbers mean rarer structures.")
            .defineInRange("strayTowerRarity", 4.0f, 1.0f, 1000f);
        CATACOMB_ENABLED = builder.comment("Whether the catacomb structure is enabled. Set to false to disable spawning.")
            .define("catacombEnabled", true);
        CATACOMB_RARITY = builder.comment("Rarity of the catacomb structure. Higher numbers mean rarer structures.")
            .defineInRange("catacombRarity", 3.0f, 1.0f, 1000f);
        builder.pop();
    }

    public static final Config INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        SPEC = specPair.getRight();
        INSTANCE = specPair.getLeft();
    }
}
