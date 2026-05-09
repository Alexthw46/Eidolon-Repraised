package alexthw.eidolon_repraised.common.entity.ai;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenericBarterGoal<E extends PathfinderMob> extends Goal {
    static final Random rand = new Random();
    static final int WAIT_TIME = 50; // Roughly 2,5 seconds

    final Predicate<ItemStack> valid;
    final Function<ItemStack, ItemStack> result;
    final float speed;

    final E entity;
    ItemEntity targetItem;
    ItemStack heldItem;
    ItemStack previousHeld;

    int pickupCooldown = 0;

    public GenericBarterGoal(E entity, Predicate<ItemStack> valid, Function<ItemStack, ItemStack> result, float speed) {
        this.entity = entity;
        this.valid = valid;
        this.result = result;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        if (entity.level().isClientSide) return false;

        List<ItemEntity> items = entity.level().getEntitiesOfClass(
            ItemEntity.class,
            entity.getBoundingBox().inflate(8.0),
            (item) -> valid.test(item.getItem())
        );
        if (items.isEmpty()) return false;
        targetItem = items.getFirst();
        return targetItem != null && targetItem.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        if (entity.level().isClientSide) return false;
        if (heldItem != null) return true;
        if (targetItem == null || !targetItem.isAlive()) return false;
        // Give up if item is too far away
        return entity.distanceToSqr(targetItem) <= 256.0;
    }

    @Override
    public void start() {
        entity.getNavigation().stop();
        pickupCooldown = 0;
    }

    @Override
    public void stop() {
        targetItem = null;
        pickupCooldown = 0;
        entity.getNavigation().stop();

        if (heldItem != null) {
            throwItem(heldItem.copy());
            heldItem = null;
        }
        restorePreviousItem();
    }

    @Override
    public void tick() {
        if (entity.level().isClientSide) return;

        if (heldItem != null) {
            // TODO: this stops it from wandering, but doesn't stop it from trying
            // Rely on the other goals to have it look at the player
            entity.getNavigation().stop();
            if (pickupCooldown > 0) {
                pickupCooldown--;
            } else {
                throwItem(result.apply(heldItem));
                heldItem = null;
                restorePreviousItem();
            }
            return;
        }

        if (targetItem != null && targetItem.isAlive()) {
            // Navigate to the target item
            entity.getNavigation().moveTo(targetItem, speed);
            entity.getLookControl().setLookAt(targetItem, 30.0f, 30.0f);

            if (entity.distanceToSqr(targetItem) < 2.25) {
                heldItem = targetItem.getItem().copy();
                pickupCooldown = WAIT_TIME;

                previousHeld = entity.getMainHandItem().copy();
                entity.setItemInHand(InteractionHand.MAIN_HAND, heldItem.copy());

                targetItem.remove(RemovalReason.DISCARDED);
                targetItem = null;

                entity.level().playSound(null, entity.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.HOSTILE, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
        }
    }

    private void throwItem(ItemStack stack) {
        Vec3 pos = entity.position().add(0, entity.getEyeHeight(), 0);
        ItemEntity thrown = new ItemEntity(entity.level(), pos.x, pos.y, pos.z, stack);
        Vec3 velocity = entity.getLookAngle().normalize().scale(0.2);
        thrown.setDeltaMovement(velocity);
        thrown.setPickUpDelay(20);
        entity.level().addFreshEntity(thrown);
    }

    private void restorePreviousItem() {
        if (previousHeld != null) {
            entity.setItemInHand(InteractionHand.MAIN_HAND, previousHeld.isEmpty() ? ItemStack.EMPTY : previousHeld.copy());
            previousHeld = null;
        }
    }
}
