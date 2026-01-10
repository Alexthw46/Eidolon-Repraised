package alexthw.eidolon_repraised.compat.apotheosis;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.common.item.WandItem;
import com.mojang.datafixers.util.Pair;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import dev.shadowsoffire.apothic_attributes.modifiers.EntitySlotGroup;
import dev.shadowsoffire.apothic_enchanting.asm.EnchHooks;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;
import java.util.function.Predicate;


public class ApotheosisCompat {

    public static LootCategory WAND;

    public static void initialize() {
        WAND = register("eidolon_wand", (s) -> s.getItem() instanceof WandItem, ALObjects.EquipmentSlotGroups.HAND, 1000);
        AffixRegistry.INSTANCE.registerCodec(Eidolon.prefix("hailing"), HailingAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(Eidolon.prefix("tracking"), TrackingAffix.CODEC);
    }

    private static LootCategory register(String path, Predicate<ItemStack> filter, EntitySlotGroup slots, int priority) {
        return Apoth.R.custom(path, Apoth.BuiltInRegs.LOOT_CATEGORY.key(), new LootCategory(filter, slots, priority));
    }

    public static int getMaxLevel(Enchantment enchantment) {
        return EnchHooks.getMaxLevel(enchantment);
    }

    public static Pair<Integer, Integer> handleWandAffix(final ItemStack stack) {
        int projectileAmount = 1;
        int trackingAmount = 0;

        Map<DynamicHolder<Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);

        for (DynamicHolder<? extends Affix> affix : affixes.keySet()) {
            AffixInstance affixInstance = affixes.get(affix);
            if (affix.get() instanceof HailingAffix scalingAffix) {
                projectileAmount += (int) scalingAffix.affixToAmount(affixInstance.rarity().get(), affixInstance.level());
            } else if (affix.get() instanceof TrackingAffix scalingAffix) {
                trackingAmount += (int) scalingAffix.affixToAmount(affixInstance.rarity().get(), affixInstance.level());
            }
        }

        return Pair.of(projectileAmount, trackingAmount);
    }

}

