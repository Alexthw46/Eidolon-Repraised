package elucent.eidolon;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Config {
	// generic
	public static ConfigValue<Integer> CRUCIBLE_STEP_DURATION, MAX_ETHEREAL_HEALTH;

    public static ConfigValue<Double> LAB_RARITY, STRAY_TOWER_RARITY, CATACOMB_RARITY;

    public Config(ForgeConfigSpec.Builder builder) {
        builder.comment("Generic settings").push("generic");
        CRUCIBLE_STEP_DURATION = builder.comment("Duration in ticks of each step of a crucible recipe.")
        		.defineInRange("crucibleStepDuration", 100, 20, 1200);
        MAX_ETHEREAL_HEALTH = builder.comment("Maximum amount of ethereal health (soul half-hearts) an entity can have at once.")
        		.defineInRange("maxEtherealHealth", 80, 0, 1000);
        builder.pop();


        builder.comment("World generation settings").push("world");
        LAB_RARITY = builder.comment("Rarity of the lab structure. Higher numbers mean rarer structures.")
            .defineInRange("labRarity", 4.0f, 1.0f, 1000.0f);
        STRAY_TOWER_RARITY = builder.comment("Rarity of the stray tower structure. Higher numbers mean rarer structures.")
            .defineInRange("strayTowerRarity", 4.0f, 1.0f, 1000f);
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
