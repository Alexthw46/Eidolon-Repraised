package alexthw.eidolon_repraised.common.incense;

import alexthw.eidolon_repraised.api.ritual.IncenseRitual;
import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class UndeathIncense extends IncenseRitual {

    public UndeathIncense(ResourceLocation registryName) {
        super(20 * 20, registryName);
    }

    @Override
    public float getRed() {
        return 0.3f;
    }

    @Override
    public float getBlue() {
        return 0.7f;
    }

    @Override
    public void tickEffect(int age) {
        if (age % 40 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(range()))) {
                entity.addEffect(new MobEffectInstance(EidolonPotions.UNDEATH_EFFECT, 20 * 60 * 2, 0, true, false));
            }
        }
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY();
        double z = blockPos.getZ();

        if (level.random.nextInt(4) == 0) {
            Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                    .setAlpha(0.35f, 0).setScale(0.375f, 0.125f).setLifetime(280)
                    .randomOffset(range() * 0.75, 0.5).randomVelocity(0.025f, 0.025f)
                    .addVelocity(0, -0.0125f, 0)
                    .setColor(0.125f, 0.125f, 0.125f, 0.005f, 0.005f, 0.005f)
                    .repeat(level, x, y + 0.125, z, 5);
            for (int i = 0; i < 5; i++) {
                Particles.spawnParticle(level, ParticleTypes.SOUL,
                        x, y + .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
            }
        }
    }
}
