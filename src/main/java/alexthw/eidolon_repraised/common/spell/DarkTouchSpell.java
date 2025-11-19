package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.capability.IMana;
import alexthw.eidolon_repraised.api.capability.IReputation;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.deity.Deities;
import alexthw.eidolon_repraised.network.MagicBurstEffectPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.recipe.ChantConversionRecipe;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.EidolonDataComponents;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.DamageTypeData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class DarkTouchSpell extends StaticSpell {

    public DarkTouchSpell(ResourceLocation name, Sign... signs) {
        super(name, 20, signs);

        NeoForge.EVENT_BUS.addListener(DarkTouchSpell::onHurt);
    }

    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof LivingEntity living && !event.getSource().is(DamageTypes.WITHER)) {
            ItemStack itemStack = living.getMainHandItem();
            if (itemStack.isEmpty()) return;
            if (itemStack.has(EidolonDataComponents.NECROTIC) && itemStack.getOrDefault(EidolonDataComponents.NECROTIC, 0) > 0) {
                float amount = Math.min(1, event.getNewDamage());
                event.setNewDamage(event.getNewDamage() - amount);
                int prevHurtResist = event.getEntity().invulnerableTime;
                event.getEntity().invulnerableTime = 0;
                if (event.getEntity().hurt(DamageTypeData.source(living.level(), DamageTypes.WITHER, living, null), amount)) {
                    itemStack.set(EidolonDataComponents.NECROTIC, itemStack.getOrDefault(EidolonDataComponents.NECROTIC, 1) - 1);
                    living.invulnerableTime = prevHurtResist;
                }
            }
        }
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        if (reputation == null) return false;
        if (reputation.getReputation(Deities.DARK_DEITY.getId()) < 10.0) {
            player.displayClientMessage(Component.translatable("eidolon_repraised.message.not_enough_reputation"), true);
            return false;
        }

        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() != 1) return false;
        ItemStack stack = items.getFirst().getItem();
        return canTouch(stack, world, player);
    }

    boolean canTouch(ItemStack stack, Level world, Player player) {
        if (stack.isDamageableItem() && stack.getMaxStackSize() == 1) return true;
        List<RecipeHolder<ChantConversionRecipe>> conversions = world.getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_CONVERSION_TYPE.get());
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        if (reputation == null) return false;
        double darkRep = reputation.getReputation(Deities.DARK_DEITY_ID);
        return conversions.stream().map(RecipeHolder::value).filter(
                r -> r.input.test(stack) && (Deities.DUMMY_ID.equals(r.deity) || Deities.DARK_DEITY_ID.equals(r.deity))
        ).anyMatch(r -> darkRep >= r.minDevotion);
    }

    protected ItemStack touchResult(ItemStack stack, Player player) { // assumes canTouch is true
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        IMana mana = player.getCapability(EidolonCapabilities.MANA_CAPABILITY);

        if (reputation != null && mana != null) {
            double darkRep = reputation.getReputation(Deities.DARK_DEITY_ID);
            for (RecipeHolder<ChantConversionRecipe> holder : player.level().getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_CONVERSION_TYPE.get())) {
                ChantConversionRecipe r = holder.value();
                if (r.input.test(stack) && (Deities.DUMMY_ID.equals(r.deity) || Deities.DARK_DEITY_ID.equals(r.deity)) && darkRep >= r.minDevotion) {
                    float conversionCost = r.conversionCost >= 0 ? r.conversionCost : getCost();
                    int maxConversionCount = conversionCost != 0 ? (int) Math.min(stack.getCount(), mana.getMagic() / conversionCost) : stack.getCount();
                    if (maxConversionCount <= 0) continue;
                    IMana.expendMana(player, (int) (conversionCost * maxConversionCount));
                    ItemStack result = r.getResultItem(player.level().registryAccess());
                    result.setCount(maxConversionCount);
                    return result;
                }
            }
        }

        // No recipe match, apply necrotic touch if compatible
        if (stack.getMaxStackSize() == 1 && stack.isDamageableItem()) {
            IMana.expendMana(player, getCost());
            stack.set(EidolonDataComponents.NECROTIC, 50);
        }
        return stack;

    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        Vec3 v = getVector(world, player);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (items.size() == 1) {
            if (!world.isClientSide) {
                ItemStack stack = items.getFirst().getItem();
                if (canTouch(stack, world, player)) {
                    ItemStack result = touchResult(stack, player);
                    if (result.getCount() == stack.getCount()) {
                        items.getFirst().setItem(result);
                    } else {
                        // spawn new item entity
                        ItemEntity newItem = new ItemEntity(world, items.getFirst().getX(), items.getFirst().getY(), items.getFirst().getZ(), result);
                        world.addFreshEntity(newItem);
                        // update old item entity
                        stack.shrink(result.getCount());
                        items.getFirst().setItem(stack);
                    }
                    Vec3 p = items.getFirst().position();
                    items.getFirst().setDefaultPickUpDelay();
                    Networking.sendToNearbyClient(world, items.getFirst().blockPosition(), new MagicBurstEffectPacket(p.x, p.y, p.z, Signs.WICKED_SIGN.color(), Signs.BLOOD_SIGN.color()));
                }
            } else {
                world.playSound(player, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1.0F, 0.6F + world.random.nextFloat() * 0.2F);
            }
        }
    }

}
