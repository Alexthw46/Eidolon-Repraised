package alexthw.eidolon_repraised.compat.apotheosis;

import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.util.StepFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface StepScalingAffix {

    @NotNull
    Map<LootRarity, StepFunction> getValues();

    default float affixToAmount(LootRarity rarity, float level) {
        return getValues().get(rarity).get(level);
    }

}