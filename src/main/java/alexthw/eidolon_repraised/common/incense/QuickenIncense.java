package alexthw.eidolon_repraised.common.incense;

import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class QuickenIncense extends GenericPotionIncense {

    public QuickenIncense(ResourceLocation registryName) {
        super(20 * 20, registryName);
    }

    @Override
    public float getRed() {
        return 0.3f;
    }

    @Override
    public float getGreen() {
        return 0.85f;
    }

    @Override
    public float getBlue() {
        return 0.7f;
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY() + 1;
        double z = blockPos.getZ();
        if (level.random.nextInt(4) == 0)
            for (int i = 0; i < 5; i++) {
                Particles.spawnParticle(level, ParticleTypes.GLOW,
                        x, y - .5, z,
                        3, 0.01, 3,
                        range() * 0.5, 0, range() * 0.5);
            }

        if (level.random.nextInt(5) == 0) Particles.create(EidolonParticles.FEATHER_PARTICLE.get())
                .setAlpha(0.25f, 0).setScale(0.375f, 0.125f).setLifetime(160)
                .randomOffset(range() * 0.75, 0.1).randomVelocity(0.025f, 0.025f)
                .addVelocity(0, 0.0125f, 0)
                .setColor(0.5f, 0.5f, 0.5f, 0.25f, 0.25f, 0.25f)
                .spawn(level, x, y + 0.125, z);

    }

    @Override
    public MobEffectInstance getEffect(Level level, BlockPos blockPos, LivingEntity livingEntity) {
        return new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60 * 10);
    }

}
