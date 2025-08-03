package alexthw.eidolon_repraised.common.incense;

import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ToughIncense extends GenericPotionIncense {
    public ToughIncense(ResourceLocation registryName) {
        super(20 * 20, registryName);
    }

    @Override
    public MobEffectInstance getEffect(Level level, BlockPos blockPos, LivingEntity livingEntity) {
        return new MobEffectInstance(EidolonPotions.REINFORCED_EFFECT, 20 * 60 * 2, 1);
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY() + 1;
        double z = blockPos.getZ();
        if (level.random.nextInt(4) == 0)
            for (int i = 0; i < 5; i++) {
                Particles.spawnParticle(level, ParticleTypes.ENCHANTED_HIT,
                        x, y - .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
                Particles.spawnParticle(level, ParticleTypes.ASH,
                        x, y + .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
            }
    }
}