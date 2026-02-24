package alexthw.eidolon_repraised.common.incense;

import alexthw.eidolon_repraised.api.ritual.IncenseRitual;
import alexthw.eidolon_repraised.client.particle.Particles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class PurityIncense extends IncenseRitual {

    public PurityIncense(ResourceLocation registryName) {
        super(40 * 20, registryName);
    }

    @Override
    public float getRed() {
        return 0.8f;
    }

    @Override
    public float getGreen() {
        return 0.9f;
    }

    @Override
    public float getBlue() {
        return 0.9f;
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY() + 1;
        double z = blockPos.getZ();
        if (level.random.nextInt(4) == 0)
            for (int i = 0; i < 5; i++) {
                Particles.spawnParticle(level, ParticleTypes.CHERRY_LEAVES,
                        x, y + .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
            }
    }

    @Override
    public void tickEffect(int age) {
        if (age % 40 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(range()))) {
                entity.getActiveEffects().stream()
                        .filter(effect -> !effect.getEffect().value().isBeneficial() && !effect.getCures().isEmpty())
                        .map(MobEffectInstance::getEffect)
                        .toList()
                        .forEach(entity::removeEffect);
            }
        }
    }
}
