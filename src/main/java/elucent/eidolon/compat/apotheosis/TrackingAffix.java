package elucent.eidolon.compat.apotheosis;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.json.PSerializer;
import shadows.placebo.util.StepFunction;

import java.util.Map;
import java.util.function.Consumer;

public class TrackingAffix extends Affix implements Apotheosis.StepScalingAffix {

    public static final Codec<TrackingAffix> CODEC = RecordCodecBuilder.create(inst -> inst.group(GemBonus.VALUES_CODEC.fieldOf("values").forGetter((a) -> a.values)).apply(inst, TrackingAffix::new));

    public @NotNull Map<LootRarity, StepFunction> getValues() {
        return values;
    }

    protected final Map<LootRarity, StepFunction> values;


    public static final PSerializer<TrackingAffix> SERIALIZER = PSerializer.fromCodec("Tracking Affix", CODEC);

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

    @Override
    public PSerializer<? extends Affix> getSerializer() {
        return SERIALIZER;
    }
}
