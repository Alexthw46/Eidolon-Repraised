package elucent.eidolon.util;

import elucent.eidolon.Eidolon;
import elucent.eidolon.common.entity.AngelArrowEntity;
import elucent.eidolon.common.entity.SpellProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class EntityUtil {
    public static final String THRALL_KEY = Eidolon.MODID + ":thrall";

    public static void enthrall(LivingEntity caster, LivingEntity thrall) {
        thrall.getPersistentData().putUUID(THRALL_KEY, caster.getUUID());
        if (thrall instanceof Mob mob) {
            mob.setPersistenceRequired();
        }
    }

    public static boolean isEnthralled(LivingEntity entity) {
        return entity.getPersistentData().contains(THRALL_KEY);
    }

    public static boolean isEnthralledBy(LivingEntity entity, LivingEntity owner) {
        return entity != null && owner != null && isEnthralled(entity) && entity.getPersistentData().getUUID(THRALL_KEY).equals(owner.getUUID());
    }

    public static boolean sameMaster(@NotNull LivingEntity entity, @NotNull LivingEntity source) {
        if (!isEnthralled(entity) || !isEnthralled(source)) return false;
        return entity.getPersistentData().getUUID(THRALL_KEY).equals(source.getPersistentData().getUUID(THRALL_KEY));
    }

    public static void moveTowardsTarget(final Entity entity) {
        if (entity == null) {
            return;
        }

        Entity owner;
        Predicate<Entity> targetPredicate;

        if (entity instanceof AngelArrowEntity angelArrow) {
            owner = angelArrow.getOwner();
            targetPredicate = angelArrow.mode;
        } else if (entity instanceof AbstractArrow arrow) {
            owner = arrow.getOwner();
            targetPredicate = target -> true;
        } else if (entity instanceof SpellProjectileEntity spellProjectile) {
            if (spellProjectile.getCasterId() != null) {
                owner = spellProjectile.getCaster();
            } else {
                owner = null;
            }

            targetPredicate = spellProjectile.trackingPredicate;
        } else {
            owner = null;
            targetPredicate = target -> true;
        }

        List<LivingEntity> entities = entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(12), target -> targetPredicate.test(target) && target != owner && target.isAlive() && !(owner != null && target.isAlliedTo(owner)) && (!entity.level.isClientSide() || target != Minecraft.getInstance().player));

        if (!entities.isEmpty()) {
            //for (Entity e : entities) System.out.println(e);
            LivingEntity nearest = entities.stream().min(Comparator.comparingDouble((e) -> e.distanceToSqr(entity))).get();
            Vec3 diff = nearest.position().add(0, nearest.getBbHeight() / 2, 0).subtract(entity.position());
            double speed = entity.getDeltaMovement().length();
            Vec3 newmotion = entity.getDeltaMovement().add(diff.normalize().scale(speed)).scale(0.5);
            if (newmotion.length() == 0) newmotion = newmotion.add(0.01, 0, 0); // avoid divide by zero
            entity.setDeltaMovement(newmotion.scale(speed / newmotion.length()));
        }
    }
}
