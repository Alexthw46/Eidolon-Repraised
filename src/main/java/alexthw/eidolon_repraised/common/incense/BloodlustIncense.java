package alexthw.eidolon_repraised.common.incense;

import alexthw.eidolon_repraised.client.particle.Particles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BloodlustIncense extends GenericPotionIncense {

    public BloodlustIncense(ResourceLocation registryName) {
        super(20 * 20, registryName);
    }

    @Override
    public float getRed() {
        return 0.75f;
    }

    @Override
    public float getGreen() {
        return 0.1f;
    }

    @Override
    public float getBlue() {
        return 0.1f;
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY() - 1;
        double z = blockPos.getZ();
        if (level.random.nextInt(4) == 0) {
            for (int i = 0; i < 5; i++) {
                Particles.spawnParticle(level, ParticleTypes.CRIMSON_SPORE,
                        x, y + .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
            }
            Particles.createRune(ResourceLocation.tryParse("eidolon_repraised:crimson_rose"))
                    .setAlpha(0.75f, 0).setScale(0.475f, 0.25f).setLifetime(180)
                    .randomOffset(range() * 0.75, 0.1).randomVelocity(0.025f, 0.025f)
                    .addVelocity(0, -0.0125f, 0)
                    .setColor(0.95F, 0.25F, 0.1F, 0.005f, 0.005f, 0.005f)
                    .repeat(level, x, y + .75, z, 2);
        }
    }

    @Override
    public MobEffectInstance getEffect(Level level, BlockPos blockPos, LivingEntity livingEntity) {
        return new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60 * 20 * 10);
    }

}
