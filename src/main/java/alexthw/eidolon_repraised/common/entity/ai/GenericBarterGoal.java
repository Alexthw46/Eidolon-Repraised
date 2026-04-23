package alexthw.eidolon_repraised.common.entity.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenericBarterGoal<E extends PathfinderMob> extends Goal {

    static final Random rand = new Random();
    final Predicate<ItemStack> valid;
    final Function<ItemStack, ItemStack> result;
    int progress = 0, cooldown = 0, lastTick = 0;
    final E entity;
    ItemStack backupHack = ItemStack.EMPTY;

    public GenericBarterGoal(E entity, Predicate<ItemStack> valid, Function<ItemStack, ItemStack> result) {
        this.entity = entity;
        this.valid = valid;
        this.result = result;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    @Override
    public void stop() {
        // If the goal is stopped while in the middle of a barter, make sure the held item isn't lost.
        if (progress > 0) {
            ItemStack held = entity.getMainHandItem();
            if (!held.isEmpty() && !entity.level().isClientSide) {
                // drop the held item back into the world so it isn't lost
                entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY() + 0.1, entity.getZ(), held.copy()));
            }
            // restore previous held stack (if any)
            entity.setItemInHand(InteractionHand.MAIN_HAND, !backupHack.isEmpty() ? backupHack.copy() : ItemStack.EMPTY);
            backupHack = ItemStack.EMPTY;
            progress = 0;
            cooldown = 600;
        }
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        if (cooldown > 0) return;

        entity.setTarget(null);
        if (progress > 0) {
            // performing barter countdown
            progress--;
            entity.getNavigation().stop();
            if (progress == 0) {
                if (!entity.level().isClientSide) {
                    // spawn the result based on the item we were holding during barter
                    ItemStack held = entity.getMainHandItem().copy();
                    entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY() + 0.1, entity.getZ(), result.apply(held)));
                }
                // restore previous held item
                entity.setItemInHand(InteractionHand.MAIN_HAND, backupHack.copy());
                backupHack = ItemStack.EMPTY;
                cooldown = 600;
            }
        } else {
            List<ItemEntity> items = entity.level().getEntitiesOfClass(ItemEntity.class, new AABB(entity.blockPosition().offset(-8, -8, -8).getBottomCenter(), entity.blockPosition().offset(8, 8, 8).getCenter()), (item) -> valid.test(item.getItem()));
            if (items.isEmpty()) return;
            ItemEntity nearest = items.stream().min(Comparator.comparingDouble(a -> a.distanceToSqr(entity))).orElse(null);
            if (nearest.distanceToSqr(entity) < 2.25) {
                // start barter: back up current held stack and take the item
                progress = 100;
                backupHack = entity.getMainHandItem().copy();
                entity.setItemInHand(InteractionHand.MAIN_HAND, nearest.getItem().copy());
                nearest.remove(RemovalReason.DISCARDED);
                entity.level().playSound(null, entity.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.HOSTILE, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            } else {
                entity.getNavigation().moveTo(nearest.getX(), nearest.getY(), nearest.getZ(), 1.0f);
            }
        }
    }

    @Override
    public boolean canUse() {
        if (-- cooldown > 0) return false;
        if (progress > 0 || entity.tickCount < lastTick + 20) return false;
        lastTick = entity.tickCount;
        List<ItemEntity> items = entity.level().getEntitiesOfClass(ItemEntity.class, new AABB(entity.blockPosition().offset(-8, -8, -8).getBottomCenter(), entity.blockPosition().offset(8, 8, 8).getCenter()), (item) -> valid.test(item.getItem()));
        return !items.isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        if (progress > 0) return true;
        else { // walking towards item
            List<ItemEntity> items = entity.level().getEntitiesOfClass(ItemEntity.class, new AABB(entity.blockPosition().offset(-8, -8, -8).getBottomCenter(), entity.blockPosition().offset(8, 8, 8).getCenter()), (item) -> valid.test(item.getItem()));
            return !items.isEmpty();
        }
    }
}
