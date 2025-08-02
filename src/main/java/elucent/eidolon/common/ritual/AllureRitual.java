package elucent.eidolon.common.ritual;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.entity.ai.GoToPositionGoal;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AllureRitual extends Ritual {
    public static final ResourceLocation SYMBOL = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "particle/allure_ritual");

    public AllureRitual() {
        super(SYMBOL, ColorUtil.packColor(255, 255, 43, 75));
    }

    @Override
    public Ritual cloneRitual() {
        return new AllureRitual();
    }

    @Override
    public RitualResult tick(Level world, BlockPos pos) {
        if (world.getGameTime() % 200 == 0) {
            List<Animal> animals = world.getEntitiesOfClass(Animal.class, new AABB(pos).inflate(96, 16, 96));
            for (Animal a : animals) {
                List<WrappedGoal> goals = a.goalSelector.getAvailableGoals().stream().filter((goal) -> goal.isRunning() && goal.getGoal() instanceof GoToPositionGoal).toList();
                boolean hasGoal = !goals.isEmpty();
                if (!hasGoal && a.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) >= 12 * 12 && world.random.nextInt(40) == 0) {
                    BlockPos target = pos.below().offset(world.random.nextInt(9) - 4, 0, world.random.nextInt(9) - 4);
                    a.goalSelector.addGoal(1, new GoToPositionGoal(a, target, 1.0));
                } else if (hasGoal && a.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 8 * 8) {
                    for (Goal g : goals) a.goalSelector.removeGoal(g);
                }
            }
        }
        return RitualResult.PASS;
    }
}
