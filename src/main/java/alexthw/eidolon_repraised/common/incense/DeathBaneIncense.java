package alexthw.eidolon_repraised.common.incense;

import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DeathBaneIncense extends GenericPotionIncense {

    public DeathBaneIncense(ResourceLocation registryName) {
        super(800, registryName);
    }


    @Override
    public MobEffectInstance getEffect(Level level, BlockPos blockPos, LivingEntity livingEntity) {
        return !livingEntity.getType().is(EntityTypeTags.UNDEAD) ?
                new MobEffectInstance(EidolonPotions.LIGHT_BLESSED, 20 * 60 * 10) :
                new MobEffectInstance(MobEffects.CONFUSION, 20 * 60 * 2);
    }

    @Override
    public void animateParticles(int burnCounter, BlockPos blockPos, Level level) {
        super.animateParticles(burnCounter, blockPos, level);
        double x = blockPos.getX();
        double y = blockPos.getY() + 1;
        double z = blockPos.getZ();
        if (level.random.nextInt(4) == 0) {
            Particles.create(EidolonParticles.FLAME_PARTICLE.get())
                    .setAlpha(0.5f, 0).setScale(0.175f, 0.125f).setLifetime(80)
                    .randomOffset(range() * 0.75, 0.1).randomVelocity(0.025f, 0.025f)
                    .addVelocity(0, -0.0125f, 0)
                    .setColor(0.75F, 0.95F, 0.95F, 0.005f, 0.005f, 0.005f)
                    .repeat(level, x, y - .75, z, 2);
            Particles.createRune(ResourceLocation.tryParse("eidolon_repraised:purity"))
                    .setAlpha(0.75f, 0).setScale(0.475f, 0.25f).setLifetime(160)
                    .randomOffset(range() * 0.75, 0.25).randomVelocity(0.025f, 0.025f)
                    .addVelocity(0, -0.0125f, 0)
                    .setColor(0.95F, 0.95F, 0.95F, 0.005f, 0.005f, 0.005f)
                    .repeat(level, x, y + .75, z, 2);
        }

    }
}