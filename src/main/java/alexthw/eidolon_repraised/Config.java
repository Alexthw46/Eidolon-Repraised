package alexthw.eidolon_repraised;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;


public class Config {
    // Generic Settings
    public static ModConfigSpec.ConfigValue<Integer> CRUCIBLE_STEP_DURATION, CRUCIBLE_STEP_BACKOFF, MAX_ETHEREAL_HEALTH;
    public static ModConfigSpec.ConfigValue<Boolean> TURN_BASED_CRUCIBLE;

    // Soul Enchanter
    public static ModConfigSpec.ConfigValue<Integer> SOUL_ENCHANTER_MAXIMUM_USES;
    public static ModConfigSpec.ConfigValue<Integer> SOUL_ENCHANTER_MAXIMUM_ENCHANTMENTS;

    public Config(ModConfigSpec.Builder builder) {
        builder.comment("Generic settings").push("generic");
        CRUCIBLE_STEP_DURATION = builder.comment("Duration in ticks of each step of a crucible recipe.").defineInRange("crucibleStepDuration", 100, 20, 1200);
        TURN_BASED_CRUCIBLE = builder.comment("Makes it so that the Crucible will not fizzle out unless the recipe has failed, giving players more time to organize and plan their next step and behave more like a turn-based recipe.").define("turnBasedCrucible", false);
        CRUCIBLE_STEP_BACKOFF = builder.comment("For turn-based Crucible, duration in ticks between each recipe check once the step duration expired.").define("crucibleBackoff", 40);
        MAX_ETHEREAL_HEALTH = builder.comment("Maximum amount of ethereal health (soul half-hearts) an entity can have at once.").defineInRange("maxEtherealHealth", 60, 0, 1000);
        builder.pop();

        builder.comment("Soul Enchanter").push("soulEnchanter");
        SOUL_ENCHANTER_MAXIMUM_USES = builder.comment("How often the Soul Enchanter can apply enchantments on an item (a value below 0 means unlimited)").define("soulEnchanterMaximumUses", -1);
        SOUL_ENCHANTER_MAXIMUM_ENCHANTMENTS = builder.comment("How many enchantments the item is allowed to have to be applicable for soul enchanting (a value below 0 means unlimited)").define("soulEnchanterMaximumEnchantments", -1);
        builder.pop();
    }

    public static final Config INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        final Pair<Config, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Config::new);
        SPEC = specPair.getRight();
        INSTANCE = specPair.getLeft();
    }

}
