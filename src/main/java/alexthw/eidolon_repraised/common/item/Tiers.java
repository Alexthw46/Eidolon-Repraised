package alexthw.eidolon_repraised.common.item;

import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class Tiers {
    public static class PewterTier implements Tier {
        @Override
        public int getUses() {
            return 325;
        }

        @Override
        public float getSpeed() {
            return 6.5f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 2;
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_IRON_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 8;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(new ItemStack(Registry.PEWTER_INGOT.get()));
        }

        public static final PewterTier INSTANCE = new PewterTier();
    }

    public static class MagicToolTier implements Tier {
        @Override
        public int getUses() {
            return 1170;
        }

        @Override
        public float getSpeed() {
            return 7.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 3;
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 30;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        public static final MagicToolTier INSTANCE = new MagicToolTier();
    }

    public static class SilverTier implements Tier {
        @Override
        public int getUses() {
            return 193;
        }

        @Override
        public float getSpeed() {
            return 7.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 2;
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_IRON_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 20;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(new ItemStack(Registry.SILVER_INGOT.get()));
        }

        public static final SilverTier INSTANCE = new SilverTier();
    }

    public static class SanguineTier implements Tier {
        @Override
        public int getUses() {
            return 507;
        }

        @Override
        public float getSpeed() {
            return 8.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 3;
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 20;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        public static final SanguineTier INSTANCE = new SanguineTier();
    }

    public static class NecroticTier implements Tier {
        @Override
        public int getUses() {
            return 2161;
        }

        @Override
        public float getSpeed() {
            return 8.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 3;
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_IRON_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 30;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Registry.DEATH_ESSENCE.get());
        }

        public static final MagicToolTier INSTANCE = new MagicToolTier();
    }
}
