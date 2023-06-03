package elucent.eidolon.deity;

import elucent.eidolon.capability.Facts;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class DarkDeity extends Deity {
    public DarkDeity(ResourceLocation id, int red, int green, int blue) {
        super(id, red, green, blue);
    }

    @Override
    public void onReputationUnlock(Player player, IReputation rep, ResourceLocation lock) {
        if (lock.equals(DeityLocks.SACRIFICE_MOB)) {
            KnowledgeUtil.grantSign(player, Signs.SOUL_SIGN);
        } else if (lock.equals(DeityLocks.SACRIFICE_VILLAGER)) {
            KnowledgeUtil.grantSign(player, Signs.MIND_SIGN);
        } else if (lock.equals(DeityLocks.ENTHRALL_UNDEAD)) {
            KnowledgeUtil.grantSign(player, Signs.DEATH_SIGN);
        }
    }

    @Override
    public void onReputationChange(Player player, IReputation rep, double prev, double current) {
        if (!KnowledgeUtil.knowsSign(player, Signs.BLOOD_SIGN) && (current >= 3 || rep.hasLock(player, id, DeityLocks.SACRIFICE_MOB))) {
            rep.setReputation(player, id, 3);
            rep.lock(player, id, DeityLocks.SACRIFICE_MOB);
            KnowledgeUtil.grantSign(player, Signs.BLOOD_SIGN);
        } else if (!KnowledgeUtil.knowsFact(player, Facts.VILLAGER_SACRIFICE) && (current >= 15 || rep.hasLock(player, id, DeityLocks.SACRIFICE_VILLAGER))) {
            rep.setReputation(player, id, 15);
            rep.lock(player, id, DeityLocks.SACRIFICE_VILLAGER);
            KnowledgeUtil.grantFact(player, Facts.VILLAGER_SACRIFICE);
        } else if (!KnowledgeUtil.knowsFact(player, Facts.ENTHRALL) && (current >= 30 || rep.hasLock(player, id, DeityLocks.ENTHRALL_UNDEAD))) {
            rep.setReputation(player, id, 30);
            rep.lock(player, id, DeityLocks.ENTHRALL_UNDEAD);
            KnowledgeUtil.grantFact(player, Facts.ENTHRALL);
            KnowledgeUtil.grantSign(player, Signs.MAGIC_SIGN);
        } else if (current >= 50) {
            rep.setReputation(player, id, 50);
        }
    }
}
