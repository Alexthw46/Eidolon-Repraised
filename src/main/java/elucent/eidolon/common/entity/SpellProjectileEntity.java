package elucent.eidolon.common.entity;

import elucent.eidolon.Eidolon;
import elucent.eidolon.util.EntityUtil;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

public abstract class SpellProjectileEntity extends Entity {
    public static final TagKey<EntityType<?>> TRACKABLE = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(Eidolon.MODID, "trackable"));
    public static final TagKey<EntityType<?>> TRACKABLE_BLACKLIST = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(Eidolon.MODID, "trackable_blacklist"));

    public Predicate<Entity> trackingPredicate = this::shouldTrack;
    public boolean isTracking;
    public boolean noImmunityFrame;

    protected UUID casterId = null;

    private final Predicate<Entity> impactPredicate = this::shouldImpact;

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
        return !target.isSpectator() && !target.getUUID().equals(casterId) && !target.getType().is(TRACKABLE_BLACKLIST) && (target instanceof Enemy || target.getType().is(TRACKABLE));
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
            HitResult ray = ProjectileUtil.getHitResultOnMoveVector(this, (e) -> !e.isSpectator() && e.isPickable() && !e.getUUID().equals(casterId));
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

    protected void handleSpellDamage(final Entity caster, final Entity target, final DamageSource damageSource, float rawDamage) {
        int prevHurtResist = target.invulnerableTime;

        if (noImmunityFrame) {
            target.invulnerableTime = 0;
        }

        target.hurt(damageSource.setMagic(), rawDamage);

        if (noImmunityFrame) {
            target.invulnerableTime = prevHurtResist;
        }
    }

    public @Nullable Player getCaster() {
        return level.getPlayerByUUID(casterId);
    }

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
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
