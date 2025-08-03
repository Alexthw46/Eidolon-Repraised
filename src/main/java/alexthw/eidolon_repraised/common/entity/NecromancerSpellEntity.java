package alexthw.eidolon_repraised.common.entity;

import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.network.MagicBurstEffectPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.registries.EidolonEntities;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.registries.EidolonPotions;
import alexthw.eidolon_repraised.util.ColorUtil;
import alexthw.eidolon_repraised.util.DamageTypeData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class NecromancerSpellEntity extends SpellProjectileEntity {
    public static final EntityDataAccessor<Integer> DELAY = SynchedEntityData.defineId(NecromancerSpellEntity.class, EntityDataSerializers.INT);

    public NecromancerSpellEntity(EntityType<? extends SpellProjectileEntity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public NecromancerSpellEntity(Level worldIn, double x, double y, double z, double vx, double vy, double vz, int delay) {
        super(EidolonEntities.NECROMANCER_SPELL.get(), worldIn);
        setPos(x, y, z);
        setDeltaMovement(vx, vy, vz);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DELAY, 0);
    }

    @Override
    public void tick() {
        if (getEntityData().get(DELAY) > 0) {
            getEntityData().set(DELAY, getEntityData().get(DELAY) - 1);
            return;
        }
        super.tick();

        Vec3 motion = getDeltaMovement();
        Vec3 pos = position();
        Vec3 norm = motion.normalize().scale(0.025f);
        for (int i = 0; i < 8; i++) {
            double lerpX = Mth.lerp(i / 8.0f, xo, pos.x);
            double lerpY = Mth.lerp(i / 8.0f, yo, pos.y);
            double lerpZ = Mth.lerp(i / 8.0f, zo, pos.z);
            Particles.create(EidolonParticles.WISP_PARTICLE.get())
                    .addVelocity(-norm.x, -norm.y, -norm.z)
                    .setAlpha(0.375f, 0).setScale(0.25f, 0)
                    .setColor(1, 0.3125f, 0.375f, 0.75f, 0.375f, 1)
                    .setLifetime(5)
                    .spawn(level(), lerpX, lerpY, lerpZ);
            Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                    .addVelocity(-norm.x, -norm.y, -norm.z)
                    .setAlpha(0.0625f, 0).setScale(0.3125f, 0.125f)
                    .setColor(0.625f, 0.375f, 1, 0.25f, 0.25f, 0.75f)
                    .randomVelocity(0.025f, 0.025f)
                    .setLifetime(20)
                    .spawn(level(), lerpX, lerpY, lerpZ);
        }
    }

    @Override
    protected void onImpact(HitResult ray, Entity target) {
        if (target instanceof LivingEntity living)
            living.addEffect(new MobEffectInstance(EidolonPotions.VULNERABLE_EFFECT, 100));
        Entity caster = getOwner();
        handleSpellDamage(caster, target, DamageTypeData.source(target.level(),DamageTypes.WITHER, this, caster), 3 + level().getDifficulty().getId());
        onImpact(ray);
    }

    @Override
    protected void onImpact(HitResult ray) {
        removeAfterChangingDimensions();
        if (!level().isClientSide) {
            Vec3 pos = ray.getLocation();
            level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 0.5f, random.nextFloat() * 0.2f + 0.9f);
            Networking.sendToNearbyClient(level(), blockPosition(), new MagicBurstEffectPacket(pos.x, pos.y, pos.z, ColorUtil.packColor(255, 158, 92, 255), ColorUtil.packColor(255, 60, 62, 186)));
        }
    }


}
