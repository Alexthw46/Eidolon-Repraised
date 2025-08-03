package alexthw.eidolon_repraised.common.ritual;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;

import java.util.List;

public class PurifyRitual extends Ritual {
    public static final ResourceLocation SYMBOL = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "particle/purify_ritual");

    public PurifyRitual() {
        super(SYMBOL, ColorUtil.packColor(255, 163, 252, 255));
    }

    @Override
    public Ritual cloneRitual() {
        return new PurifyRitual();
    }

    @Override
    public RitualResult start(Level level, BlockPos pos) {
        List<PathfinderMob> purifiable = level.getEntitiesOfClass(PathfinderMob.class, Ritual.getDefaultBounds(pos), (entity) -> entity instanceof ZombieVillager || entity instanceof ZombifiedPiglin || entity instanceof Zoglin);

        if (!purifiable.isEmpty() && !level.isClientSide)
            level.playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1.0f, 1.0f);
        if (level instanceof ServerLevel world) for (PathfinderMob entity : purifiable) {
            if (entity instanceof ZombieVillager villager) {
                villager.finishConversion((ServerLevel) world);
            }
            if (entity instanceof ZombifiedPiglin) {
                entity.remove(RemovalReason.KILLED);
                Piglin piglin = new Piglin(EntityType.PIGLIN, world);
                piglin.copyPosition(entity);
                piglin.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
                world.addFreshEntity(piglin);
            }
            if (entity instanceof Zoglin) {
                entity.remove(RemovalReason.KILLED);
                Hoglin hoglin = new Hoglin(EntityType.HOGLIN, world);
                hoglin.copyPosition(entity);
                hoglin.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
                world.addFreshEntity(hoglin);
            }
        }
        return RitualResult.TERMINATE;
    }
}
