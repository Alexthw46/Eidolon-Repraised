package elucent.eidolon.compat;

import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.fml.ModList;

public class Apotheosis {
    public static final boolean IS_LOADED = ModList.get().isLoaded("apotheosis");

    public static boolean isTreasureOnly(final Enchantment enchantment) {
        return EnchHooks.isTreasureOnly(enchantment);
    }

    public static int getMaxLevel(final Enchantment enchantment) {
        return EnchHooks.getMaxLevel(enchantment);
    }

}
