package elucent.eidolon.compat.apotheosis;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class TrackingAffix extends Affix implements Apotheosis.StepScalingAffix {
    public static final Codec<TrackingAffix> CODEC = RecordCodecBuilder.create(inst -> inst.group(GemBonus.VALUES_CODEC.fieldOf("values").forGetter((a) -> a.values)).apply(inst, TrackingAffix::new));

    protected final Map<LootRarity, StepFunction> values;

    public TrackingAffix(Map<LootRarity, StepFunction> values) {
        super(AffixType.ABILITY);
        this.values = values;
    }

    @Override
    public boolean canApplyTo(final ItemStack stack, final LootCategory category, final LootRarity rarity) {
        return category == Apotheosis.WAND && this.values.containsKey(rarity);
    }

    @Override
    public void addInformation(final ItemStack stack, final LootRarity rarity, float level, final Consumer<Component> list) {
        list.accept(Component.translatable("affix." + this.getId() + ".desc", fmt(affixToAmount(rarity, level))));
    }

    public @NotNull Map<LootRarity, StepFunction> getValues() {
        return values;
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
