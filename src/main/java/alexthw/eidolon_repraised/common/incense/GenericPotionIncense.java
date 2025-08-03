package alexthw.eidolon_repraised.common.incense;

import alexthw.eidolon_repraised.api.ritual.IncenseRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class GenericPotionIncense extends IncenseRitual {

    public GenericPotionIncense(int maxDuration, ResourceLocation registryName) {
        super(maxDuration, registryName);
    }

    public abstract MobEffectInstance getEffect(Level level, BlockPos blockPos, LivingEntity livingEntity);

    @Override
    public void tickEffect(int age) {
        if (age % 20 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(range()))) {
                entity.addEffect(getEffect(level, pos, entity));
            }
        }
    }
}
