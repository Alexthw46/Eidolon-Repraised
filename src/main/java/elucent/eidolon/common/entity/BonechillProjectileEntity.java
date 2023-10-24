package elucent.eidolon.common.entity;

import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.network.MagicBurstEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.*;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BonechillProjectileEntity extends SpellProjectileEntity {
    public BonechillProjectileEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 motion = getDeltaMovement();
        Vec3 pos = position();
        Vec3 norm = motion.normalize().scale(0.025f);
        for (int i = 0; i < 8; i ++) {
            double lerpX = Mth.lerp(i / 8.0f, xOld, pos.x);
            double lerpY = Mth.lerp(i / 8.0f, yOld, pos.y);
            double lerpZ = Mth.lerp(i / 8.0f, zOld, pos.z);
            Particles.create(EidolonParticles.WISP_PARTICLE)
                .addVelocity(-norm.x, -norm.y, -norm.z)
                .setAlpha(0.0625f, 0).setScale(0.625f, 0)
                .setColor(0.875f, 1, 1, 0.375f, 0.5f, 0.75f)
                .setLifetime(5)
                .spawn(level, lerpX, lerpY, lerpZ);
            Particles.create(EidolonParticles.WISP_PARTICLE)
                .addVelocity(-norm.x, -norm.y, -norm.z)
                .setAlpha(0.125f, 0).setScale(0.25f, 0.125f)
                .setColor(1, 0.75f, 0.875f, 0.375f, 0.5f, 0.75f)
                .setLifetime(20)
                .spawn(level, lerpX, lerpY, lerpZ);
        }
    }

    @Override
    protected void onImpact(HitResult ray, Entity target) {
        handleDamage(target, new IndirectEntityDamageSource(Registry.FROST_DAMAGE.getMsgId(), this, getCaster()), 4);

        if (target instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(EidolonPotions.CHILLED_EFFECT.get(), 300, 0));
        }

        onImpact(ray);
    }

    @Override
    protected void onImpact(HitResult ray) {
        kill();
        if (!level.isClientSide) {
            Vec3 pos = ray.getLocation();
            level.playSound(null, pos.x, pos.y, pos.z, EidolonSounds.SPLASH_BONECHILL_EVENT.get(), SoundSource.NEUTRAL, 0.5f, random.nextFloat() * 0.2f + 0.9f);
            Networking.sendToTracking(level, blockPosition(), new MagicBurstEffectPacket(pos.x, pos.y, pos.z, ColorUtil.packColor(255, 192, 224, 255), ColorUtil.packColor(255, 96, 128, 192)));
        }
    }
}
