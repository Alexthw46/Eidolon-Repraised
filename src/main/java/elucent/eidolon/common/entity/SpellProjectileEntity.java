package elucent.eidolon.common.entity;

import elucent.eidolon.util.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Predicate;

public abstract class SpellProjectileEntity extends Entity {
    UUID casterId = null;
    private final Predicate<Entity> impactPredicate = this::shouldImpact;
    public Predicate<Entity> trackingPredicate = this::shouldTrack;

    public boolean isTracking;

    public SpellProjectileEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public Entity shoot(double x, double y, double z, double vx, double vy, double vz, final UUID caster, final ItemStack stack) {
        setPos(x, y, z);
        setDeltaMovement(vx, vy, vz);
        casterId = caster;
        hurtMarked = true;
        return this;
    }

    private boolean shouldImpact(final Entity target) {
        if (!target.isSpectator() && target.isPickable() && !target.getUUID().equals(casterId)) {
            return true;
        }

        return shouldTrack(target);
    }

    private boolean shouldTrack(final Entity target) {
        // TODO :: Create tag
        return !target.isSpectator() && !target.getUUID().equals(casterId) && target instanceof Enemy;
    }

    @Override
    public void tick() {
        if (isTracking) {
            EntityUtil.moveTowardsTarget(this);
        }

        Vec3 motion = getDeltaMovement();
        setDeltaMovement(motion.x * 0.96, (motion.y > 0 ? motion.y * 0.96 : motion.y) - 0.03f, motion.z * 0.96);

        super.tick();

        if (!level.isClientSide) {
            HitResult ray = ProjectileUtil.getHitResult(this, impactPredicate);
            if (ray.getType() == HitResult.Type.ENTITY) {
                onImpact(ray, ((EntityHitResult)ray).getEntity());
            }
            else if (ray.getType() == HitResult.Type.BLOCK) {
                onImpact(ray);
            }
        }

        Vec3 pos = position();
        xo = pos.x;
        yo = pos.y;
        zo = pos.z;
        setPos(pos.x + motion.x, pos.y + motion.y, pos.z + motion.z);
    }

    public UUID getCasterId() {
        return casterId;
    }

    protected abstract void onImpact(HitResult ray, Entity target);
    protected abstract void onImpact(HitResult ray);

    @Override
    protected void defineSynchedData() {
        //
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        casterId = compound.contains("caster") ? compound.getUUID("caster") : null;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        if (casterId != null) compound.putUUID("caster", casterId);
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
