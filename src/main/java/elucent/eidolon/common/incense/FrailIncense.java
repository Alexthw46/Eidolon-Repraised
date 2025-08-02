package elucent.eidolon.common.incense;

import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class FrailIncense extends GenericPotionIncense {

    public FrailIncense(ResourceLocation registryName) {
        super(20 * 40, registryName);
    }

    @Override
    public float getRed() {
        return 0.8f;
    }

    @Override
    public float getGreen() {
        return 0.2f;
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY() + 1;
        double z = blockPos.getZ();
        if (level.random.nextInt(4) == 0)
            for (int i = 0; i < 5; i++) {
                Particles.spawnParticle(level, ParticleTypes.MYCELIUM,
                        x, y + .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
                Particles.spawnParticle(level, ParticleTypes.WITCH,
                        x, y - .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
            }
    }

    @Override
    public MobEffectInstance getEffect(Level level, BlockPos blockPos, LivingEntity livingEntity) {
        return new MobEffectInstance(EidolonPotions.VULNERABLE_EFFECT, 20 * 10);
    }

}
