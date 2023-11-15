package elucent.eidolon.compat.apotheosis;

import com.mojang.datafixers.util.Pair;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.StepFunction;
import elucent.eidolon.Eidolon;
import elucent.eidolon.common.item.WandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class Apotheosis {
    private static final EquipmentSlot[] wandSlots = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    public static final LootCategory WAND = LootCategory.register(LootCategory.SWORD, "wand", itemStack -> itemStack.getItem() instanceof WandItem, wandSlots);

    public static boolean isTreasureOnly(final Enchantment enchantment) {
        return EnchHooks.isTreasureOnly(enchantment);
    }

    public static int getMaxLevel(final Enchantment enchantment) {
        return EnchHooks.getMaxLevel(enchantment);
    }

    public static int affixToAmount(final AffixInstance affixInstance) {
        if (affixInstance.affix().get() instanceof StepScalingAffix scalingAffix) {
            return (int) scalingAffix.affixToAmount(affixInstance);
        }
        return 0;
    }

    public static void initialize() {
        AffixRegistry.INSTANCE.registerSerializer(new ResourceLocation(Eidolon.MODID, "tracking"), TrackingAffix.SERIALIZER);
        AffixRegistry.INSTANCE.registerSerializer(new ResourceLocation(Eidolon.MODID, "hailing"), HailingAffix.SERIALIZER);
    }

    public static Pair<Integer, Integer> handleWandAffix(final ItemStack stack) {
        int projectileAmount = 1;
        int trackingAmount = 0;

        Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);

        for (DynamicHolder<? extends Affix> affix : affixes.keySet()) {
            AffixInstance affixInstance = affixes.get(affix);

            if (affix.get() instanceof HailingAffix) {
                projectileAmount += affixToAmount(affixInstance);
            } else if (affix.get() instanceof TrackingAffix) {
                trackingAmount += affixToAmount(affixInstance);
            }
        }

        return Pair.of(projectileAmount, trackingAmount);
    }

    public interface StepScalingAffix {

        @NotNull Map<LootRarity, StepFunction> getValues();

        default float affixToAmount(LootRarity rarity, float level) {
            return getValues().get(rarity).get(level);

        }

        default float affixToAmount(final AffixInstance affixInstance) {
            return affixToAmount(affixInstance.rarity().get(), affixInstance.level());
        }
    }

}
