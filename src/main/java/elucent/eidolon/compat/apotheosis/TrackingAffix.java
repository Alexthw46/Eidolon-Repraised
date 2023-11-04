package elucent.eidolon.compat.apotheosis;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.json.PSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class TrackingAffix extends Affix {
    public static final Codec<TrackingAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    LootRarity.CODEC.fieldOf("min_rarity").forGetter(TrackingAffix::getMinRarity))
            .apply(inst, TrackingAffix::new));


    public static final PSerializer<TrackingAffix> SERIALIZER = PSerializer.fromCodec("Tracking Affix", CODEC);

    private final LootRarity minRarity;

    public TrackingAffix(final LootRarity minRarity) {
        super(AffixType.ABILITY);
        this.minRarity = minRarity;
    }

    public LootRarity getMinRarity() {
        return minRarity;
    }

    @Override
    public boolean canApplyTo(final ItemStack stack, final LootCategory category, final LootRarity rarity) {
        return category == Apotheosis.WAND && rarity.isAtLeast(this.minRarity);
    }

    @Override
    public void addInformation(final ItemStack stack, final LootRarity rarity, float level, final Consumer<Component> list) {
        list.accept(Component.translatable("affix." + this.getId() + ".desc", Apotheosis.affixToAmount(rarity, getMinRarity())));
    }

    @Override
    public PSerializer<? extends Affix> getSerializer() {
        return SERIALIZER;
    }
}
