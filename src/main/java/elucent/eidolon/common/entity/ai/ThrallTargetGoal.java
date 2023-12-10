package elucent.eidolon.common.entity.ai;

import elucent.eidolon.event.Events;
import elucent.eidolon.util.EntityUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.UUID;

import static elucent.eidolon.util.EntityUtil.THRALL_KEY;

public class ThrallTargetGoal extends TargetGoal {

    protected Player owner;

    public ThrallTargetGoal(PathfinderMob pMob) {
        super(pMob, true);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }
    @Override
    public boolean canUse() {
        return EntityUtil.isEnthralled(mob);
    }

    @Override
    public void start() {
        super.start();
        UUID master = mob.getPersistentData().getUUID(THRALL_KEY);
        owner = mob.level.getPlayerByUUID(master);
        if (owner == null) return;
        targetMob = Events.handleEnthralledTargeting(owner.getLastHurtByMob(), owner.getLastHurtMob(), mob);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && owner != null && (targetMob != null && targetMob.isAlive() && mob.canAttack(targetMob));
    }

}
