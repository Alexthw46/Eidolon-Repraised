package alexthw.eidolon_repraised.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class GoToPositionGoal extends Goal {
    final BlockPos dest;
    final PathfinderMob creature;
    final double speed;
    boolean running;

    public GoToPositionGoal(PathfinderMob creature, BlockPos pos, double speedIn) {
        this.creature = creature;
        this.dest = pos;
        this.speed = speedIn;
        this.running = true;
    }

    @Override
    public void start() {
        super.start();
        creature.getNavigation().moveTo(dest.getX(), dest.getY(), dest.getZ(), speed);
    }

    @Override
    public void tick() {
        if (running && creature.level().getGameTime() % 20 == 0) {
            if (creature.getTarget() != null) {
                creature.setTarget(null);
            }
            if (creature.distanceToSqr(dest.getX(), dest.getY(), dest.getZ()) < 8 * 8) {
                running = false;
            } else if (creature.getNavigation().isDone()) {
                creature.getNavigation().moveTo(dest.getX(), dest.getY(), dest.getZ(), speed);
            }
        }
    }

    @Override
    public boolean canUse() {
        return running;
    }
}
