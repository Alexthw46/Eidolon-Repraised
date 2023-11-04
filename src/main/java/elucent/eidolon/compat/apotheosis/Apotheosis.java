package elucent.eidolon.compat.apotheosis;


import com.ibm.icu.impl.Pair;
import elucent.eidolon.Eidolon;
import elucent.eidolon.common.item.WandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.AffixInstance;
import shadows.apotheosis.adventure.affix.AffixManager;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.apotheosis.ench.asm.EnchHooks;
import shadows.placebo.util.StepFunction;

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
        if (affixInstance.affix() instanceof StepScalingAffix scalingAffix) {
            return (int) scalingAffix.affixToAmount(affixInstance);
        }
        return 0;
    }

    public static void initialize() {
        AffixManager.INSTANCE.registerSerializer(new ResourceLocation(Eidolon.MODID, "tracking"), TrackingAffix.SERIALIZER);
        AffixManager.INSTANCE.registerSerializer(new ResourceLocation(Eidolon.MODID, "hailing"), HailingAffix.SERIALIZER);
    }

    public static Pair<Integer, Integer> handleWandAffix(final ItemStack stack) {
        int projectileAmount = 1;
        int trackingAmount = 0;

        Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(stack);

        for (Affix affix : affixes.keySet()) {
            AffixInstance affixInstance = affixes.get(affix);

            if (affix instanceof HailingAffix) {
                projectileAmount += affixToAmount(affixInstance);
            } else if (affix instanceof TrackingAffix) {
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
            return affixToAmount(affixInstance.rarity(), affixInstance.level());
        }
    }

}
