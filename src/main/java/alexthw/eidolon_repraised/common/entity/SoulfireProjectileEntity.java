package alexthw.eidolon_repraised.common.entity;

import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.network.MagicBurstEffectPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.registries.EidolonSounds;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SoulfireProjectileEntity extends SpellProjectileEntity {
    public SoulfireProjectileEntity(EntityType<? extends SpellProjectileEntity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            Vec3 motion = getDeltaMovement();
            Vec3 pos = position();
            Vec3 norm = motion.normalize().scale(0.025f);
            for (int i = 0; i < 8; i++) {
                double lerpX = Mth.lerp(i / 8.0f, xo, pos.x);
                double lerpY = Mth.lerp(i / 8.0f, yo, pos.y);
                double lerpZ = Mth.lerp(i / 8.0f, zo, pos.z);
                Particles.create(EidolonParticles.SPARKLE_PARTICLE.get())
                        .addVelocity(-norm.x, -norm.y, -norm.z)
                        .setAlpha(0.375f, 0).setScale(0.375f, 0)
                        .setColor(1, 0.875f, 0.5f, 0.5f, 0.25f, 1)
                        .setLifetime(5)
                        .spawn(level(), lerpX, lerpY, lerpZ);
                Particles.create(EidolonParticles.WISP_PARTICLE.get())
                        .addVelocity(-norm.x, -norm.y, -norm.z)
                        .setAlpha(0.125f, 0).setScale(0.25f, 0.125f)
                        .setColor(1, 0.5f, 0.625f, 0.5f, 0.25f, 1)
                        .setLifetime(20)
                        .spawn(level(), lerpX, lerpY, lerpZ);
            }
        }
    }

    @Override
    protected void onImpact(HitResult ray, Entity target) {
        Entity caster = getOwner();
        handleSpellDamage(caster, target, target.damageSources().indirectMagic(this, caster), 7.0f);
        onImpact(ray);
    }

    @Override
    protected void onImpact(HitResult ray) {
        removeAfterChangingDimensions();
        if (!level().isClientSide) {
            Vec3 pos = ray.getLocation();
            level().playSound(null, pos.x, pos.y, pos.z, EidolonSounds.SPLASH_SOULFIRE_EVENT.get(), SoundSource.NEUTRAL, 0.6f, random.nextFloat() * 0.2f + 0.9f);
            Networking.sendToNearbyClient(level(), blockPosition(), new MagicBurstEffectPacket(pos.x, pos.y, pos.z, ColorUtil.packColor(255, 255, 229, 125), ColorUtil.packColor(255, 124, 57, 247)));
        }
    }
}
