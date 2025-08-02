package elucent.eidolon.common.incense;

import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.registries.EidolonParticles;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SoulHarvestIncense extends GenericPotionIncense {

    public SoulHarvestIncense(ResourceLocation registryName) {
        super(400, registryName);
    }

    @Override
    public float getRed() {
        return 0.6f;
    }

    @Override
    public float getGreen() {
        return 0.4f;
    }

    @Override
    public float getBlue() {
        return 0.8f;
    }


    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY() + 1;
        double z = blockPos.getZ();
        if (level.random.nextInt(4) == 0) {
            for (int i = 0; i < 5; i++)
                Particles.spawnParticle(level, ParticleTypes.ENCHANT,
                        x, y + .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
            Particles.create(EidolonParticles.WISP_PARTICLE.get())
                    .setAlpha(0.75f, 0.2f).setScale(0.175f, 0.125f).setLifetime(80)
                    .randomOffset(range() * 0.75, 0.1).randomVelocity(0.15f, 0.15f)
                    .addVelocity(0, 0.0125f, 0)
                    .setColor(0.5F, 0.15F, 0.85F, 0.005f, 0.005f, 0.005f)
                    .repeat(level, x, y - .5, z, 2);
        }

    }

    @Override
    public MobEffectInstance getEffect(Level level, BlockPos blockPos, LivingEntity livingEntity) {
        return new MobEffectInstance(EidolonPotions.SOUL_HARVEST, 100, 0, true, false);
    }

}
