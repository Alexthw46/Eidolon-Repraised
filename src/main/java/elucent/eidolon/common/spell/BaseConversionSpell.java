package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.network.MagicBurstEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.recipe.ChantConversionRecipe;
import elucent.eidolon.registries.EidolonRecipes;
import elucent.eidolon.registries.Signs;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BaseConversionSpell extends StaticSpell {

    public BaseConversionSpell(ResourceLocation name, int cost, Sign... signs) {
        super(name, cost, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() != 1) return false;
        ItemStack stack = items.get(0).getItem();
        return canTouch(stack, world, player);
    }

    boolean canTouch(ItemStack stack, Level world, Player player) {
        if (stack.isDamageableItem() && stack.getMaxStackSize() == 1) return true;
        var conversions = world.getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_CONVERSION_TYPE.get());
        return conversions.stream().anyMatch(
                r -> r.input.test(stack) && r.deity == null
        );
    }

    protected ItemStack touchResult(ItemStack stack, Player player) { // assumes canTouch is true
        var mana = player.getCapability(ISoul.INSTANCE).resolve().orElse(null);
        if (mana == null) return stack;

        for (ChantConversionRecipe r : player.level().getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_CONVERSION_TYPE.get())) {
            // This type of conversion spell only works for non-deity-specific conversions, but ignores devotion requirements.
            if (r.input.test(stack) && r.deity == null) {
                float conversionCost = r.conversionCost >= 0 ? r.conversionCost : getCost();
                int maxConversionCount = conversionCost != 0 ? (int) Math.min(stack.getCount(), mana.getMagic() / conversionCost) : stack.getCount();
                if (maxConversionCount <= 0) continue;
                ISoul.expendMana(player, (int) (conversionCost * maxConversionCount));
                ItemStack result = r.getResultItem(player.level().registryAccess());
                result.setCount(maxConversionCount);
                return result;
            }
        }
        return stack;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() == 1) {
            if (!world.isClientSide) {
                ItemStack stack = items.get(0).getItem();
                if (canTouch(stack, world, player)) {
                    ItemStack result = touchResult(stack, player);
                    if (result.getCount() == stack.getCount()) {
                        items.get(0).setItem(result);
                    } else {
                        // spawn new item entity
                        ItemEntity newItem = new ItemEntity(world, items.get(0).getX(), items.get(0).getY(), items.get(0).getZ(), result);
                        world.addFreshEntity(newItem);
                        // update old item entity
                        stack.shrink(result.getCount());
                        items.get(0).setItem(stack);
                    }
                    Vec3 p = items.get(0).position();
                    items.get(0).setDefaultPickUpDelay();
                    Networking.sendToTracking(world, items.get(0).blockPosition(), new MagicBurstEffectPacket(p.x, p.y, p.z, Signs.WICKED_SIGN.getColor(), Signs.BLOOD_SIGN.getColor()));
                }
            } else {
                world.playSound(player, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1.0F, 0.6F + world.random.nextFloat() * 0.2F);
            }
        }
    }

}

