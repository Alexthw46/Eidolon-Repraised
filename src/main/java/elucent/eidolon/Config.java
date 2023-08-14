package elucent.eidolon;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
	// generic
	public static ConfigValue<Integer> CRUCIBLE_STEP_DURATION, MAX_ETHEREAL_HEALTH;

    public Config(ForgeConfigSpec.Builder builder) {
        builder.comment("Generic settings").push("generic");
        CRUCIBLE_STEP_DURATION = builder.comment("Duration in ticks of each step of a crucible recipe.")
        		.defineInRange("crucibleStepDuration", 100, 20, 1200);
        MAX_ETHEREAL_HEALTH = builder.comment("Maximum amount of ethereal health (soul half-hearts) an entity can have at once.")
        		.defineInRange("maxEtherealHealth", 80, 0, 1000);
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
