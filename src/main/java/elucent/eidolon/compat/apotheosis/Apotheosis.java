package elucent.eidolon.compat.apotheosis;

import com.ibm.icu.impl.Pair;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import elucent.eidolon.Eidolon;
import elucent.eidolon.common.item.WandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;


public class Apotheosis {
    public static final LootCategory WAND = LootCategory.register(LootCategory.SWORD, "wand", itemStack -> itemStack.getItem() instanceof WandItem, toArray(EquipmentSlot.MAINHAND));

    public static boolean isTreasureOnly(final Enchantment enchantment) {
        return EnchHooks.isTreasureOnly(enchantment);
    }

    public static int getMaxLevel(final Enchantment enchantment) {
        return EnchHooks.getMaxLevel(enchantment);
    }

    public static int rarityToNumber(final LootRarity rarity) {
        return rarity.ordinal();
    }

    public static int affixToAmount(final LootRarity affixRarity, final LootRarity minRarity) {
        // This way the amount starts at the minimum rarity with 1
        return 1 + Apotheosis.rarityToNumber(affixRarity) - Apotheosis.rarityToNumber(minRarity);
    }


    private static EquipmentSlot[] toArray(final EquipmentSlot... equipmentSlots) {
        return equipmentSlots;
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

            if (affix.get() instanceof HailingAffix hailingAffix) {
                projectileAmount += affixToAmount(affixInstance.rarity().get(), hailingAffix.getMinRarity());
            } else if (affix.get() instanceof TrackingAffix trackingAffix) {
                trackingAmount += affixToAmount(affixInstance.rarity().get(), trackingAffix.getMinRarity());
            }
        }

        return Pair.of(projectileAmount, trackingAmount);
    }
}
