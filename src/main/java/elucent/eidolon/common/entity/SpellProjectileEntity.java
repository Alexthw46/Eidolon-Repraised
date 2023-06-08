package elucent.eidolon.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class SpellProjectileEntity extends Entity {
    UUID casterId = null;

    public SpellProjectileEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public Entity shoot(double x, double y, double z, double vx, double vy, double vz, UUID caster) {
        setPos(x, y, z);
        setDeltaMovement(vx, vy, vz);
        casterId = caster;
        hurtMarked = true;
        return this;
    }

    @Override
    public void tick() {
        Vec3 motion = getDeltaMovement();
        setDeltaMovement(motion.x * 0.96, (motion.y > 0 ? motion.y * 0.96 : motion.y) - 0.03f, motion.z * 0.96);

        super.tick();

        if (!level.isClientSide) {
            HitResult ray = ProjectileUtil.getHitResult(this, (e) -> !e.isSpectator() && e.isPickable() && !e.getUUID().equals(casterId));
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
