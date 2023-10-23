package elucent.eidolon.compat;

import elucent.eidolon.common.item.WandItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.ench.asm.EnchHooks;

public class Apotheosis {
    public static final LootCategory WAND = LootCategory.register(LootCategory.SWORD, "wand", itemStack -> itemStack.getItem() instanceof WandItem, toArray(EquipmentSlot.MAINHAND));

    public static boolean isTreasureOnly(final Enchantment enchantment) {
        return EnchHooks.isTreasureOnly(enchantment);
    }

    public static int getMaxLevel(final Enchantment enchantment) {
        return EnchHooks.getMaxLevel(enchantment);
    }

    private static EquipmentSlot[] toArray(final EquipmentSlot... equipmentSlots) {
        return equipmentSlots;
    }

    public static void initialize() { /* Nothing to do */ }
}
