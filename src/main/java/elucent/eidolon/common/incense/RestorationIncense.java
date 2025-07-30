package elucent.eidolon.common.incense;

import elucent.eidolon.api.ritual.IncenseRitual;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.registries.EidolonParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class RestorationIncense extends IncenseRitual {

    public RestorationIncense(ResourceLocation registryName) {
        super(800, registryName);
    }

    @Override
    public float getGreen() {
        return .75f;
    }

    @Override
    public void tickEffect(int age) {
        // every 20 ticks, apply a wither effect to the undead around the censer and heal the living entities
        if (age % 20 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(range()))) {
                if (entity.getType().is(EntityTypeTags.UNDEAD)) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * 10, 1));
                } else {
                    entity.heal(1);
                }
            }
        }
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY() + 1;
        double z = blockPos.getZ();

        if (level.random.nextInt(5) == 0) Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                .setAlpha(0.25f, 0).setScale(0.375f, 0.125f).setLifetime(160)
                .randomOffset(range() * 0.5, 0.125).randomVelocity(0.025f, 0.025f)
                .addVelocity(0, 0.0125f, 0)
                .setColor(0.75f, 0.5f, 0.5f, 0.25f, 0.25f, 0.25f)
                .spawn(level, x, y + 0.125, z);

        if (level.random.nextInt(8) == 0)
            for (int i = 0; i < 3; i++) {
                Particles.spawnParticle(level, ParticleTypes.HEART,
                        x, y - .5, z,
                        0, -0.01, 0,
                        range(), 0, range());
            }


    }
}
