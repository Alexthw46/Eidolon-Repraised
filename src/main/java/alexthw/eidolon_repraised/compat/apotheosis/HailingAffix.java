package alexthw.eidolon_repraised.compat.apotheosis;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HailingAffix extends Affix implements StepScalingAffix {
    public static final Codec<HailingAffix> CODEC = RecordCodecBuilder.create((inst) -> inst.group(affixDef(), LootRarity.mapCodec(StepFunction.CODEC).fieldOf("values").forGetter((a) -> a.values)).apply(inst, HailingAffix::new));

    protected final Map<LootRarity, StepFunction> values;

    public HailingAffix(AffixDefinition def, Map<LootRarity, StepFunction> values) {
        super(def);
        this.values = values;
    }

    @Override
    public boolean canApplyTo(final ItemStack stack, final LootCategory category, final LootRarity rarity) {
        return category == ApotheosisCompat.WAND && this.values.containsKey(rarity);
    }

    @Override
    public MutableComponent getDescription(AffixInstance inst, AttributeTooltipContext ctx) {
        return Component.translatable("affix." + this.id() + ".desc", fmt(affixToAmount(inst.rarity().get(), inst.level())));
    }
    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull Map<LootRarity, StepFunction> getValues() {
        return values;
    }
}