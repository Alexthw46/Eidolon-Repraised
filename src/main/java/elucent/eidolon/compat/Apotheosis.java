package elucent.eidolon.compat;

import elucent.eidolon.Eidolon;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Apotheosis {
    public static final boolean IS_LOADED = ModList.get().isLoaded("apotheosis");

    private static Method getMaxLevel;
    private static Method isTreasureOnly;

    public static boolean isTreasureOnly(final Enchantment enchantment) {
        initialize();

        if (IS_LOADED && isInitialized()) {
            try {
                return (Boolean) isTreasureOnly.invoke(null, enchantment);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                Eidolon.LOG.error("An error occurred while calling Apotheosis isTreasureOnly method", exception);
            }
        }

        return enchantment.isTreasureOnly();
    }

    public static int getMaxLevel(final Enchantment enchantment) {
        initialize();

        if (IS_LOADED && isInitialized()) {
            try {
                return (Integer) getMaxLevel.invoke(null, enchantment);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                Eidolon.LOG.error("An error occurred while calling Apotheosis isTreasureOnly method", exception);
            }
        }

        return enchantment.getMaxLevel();
    }

    public static void initialize() {
        if (!IS_LOADED || isInitialized()) {
            return;
        }

        try {
            Class<?> enchHooks = Class.forName("shadows.apotheosis.ench.asm.EnchHooks");
            getMaxLevel = ObfuscationReflectionHelper.findMethod(enchHooks, "getMaxLevel", Enchantment.class);
            isTreasureOnly = ObfuscationReflectionHelper.findMethod(enchHooks, "isTreasureOnly", Enchantment.class);
        } catch (ClassNotFoundException exception) {
            Eidolon.LOG.error("Apotheosis compatibility could not be loaded", exception);
            getMaxLevel = null;
            isTreasureOnly = null;
        }
    }

    public static boolean isInitialized() {
        return getMaxLevel != null && isTreasureOnly != null;
    }
}
