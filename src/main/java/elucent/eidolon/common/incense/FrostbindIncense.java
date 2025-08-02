package elucent.eidolon.common.incense;

import elucent.eidolon.api.ritual.IncenseRitual;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.registries.EidolonParticles;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class FrostbindIncense extends IncenseRitual {

    public FrostbindIncense(ResourceLocation registryName) {
        super(20 * 60 * 2, registryName);
    }

    @Override
    public float getRed() {
        return 0.3f;
    }

    @Override
    public float getGreen() {
        return 0.75f;
    }

    @Override
    public float getBlue() {
        return 1.0f;
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY() + 1;
        double z = blockPos.getZ();
        if (level.random.nextInt(4) == 0) {

            for (int i = 0; i < 5; i++) {
                Particles.spawnParticle(level, ParticleTypes.SNOWFLAKE,
                        x, y + .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
            }

            Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                    .setAlpha(0.35f, 0).setScale(0.375f, 0.125f).setLifetime(80)
                    .randomOffset(range() * 0.75, 0.1).randomVelocity(0.025f, 0.025f)
                    .addVelocity(0, -0.0125f, 0)
                    .setColor(0.75F, 0.95F, 1, 0.005f, 0.005f, 0.005f)
                    .repeat(level, x, y + 0.125, z, 5);
        }
    }

    @Override
    public void tickEffect(int age) {
        if (age % 10 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(range()))) {
                if (entity != player && entity.isAlive()) {
                    entity.addEffect(new MobEffectInstance(EidolonPotions.CHILLED_EFFECT, 30));
                    if (entity.canFreeze()) entity.setTicksFrozen(entity.getTicksFrozen() + 80);
                }
            }
        }
    }
}
