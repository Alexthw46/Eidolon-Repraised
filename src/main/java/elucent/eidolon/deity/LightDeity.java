package elucent.eidolon.deity;

import elucent.eidolon.capability.Facts;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class LightDeity extends Deity {
    public LightDeity(ResourceLocation id, int red, int green, int blue) {
        super(id, red, green, blue);
    }

    @Override
    public void onReputationUnlock(Player player, IReputation rep, ResourceLocation lock) {
        if (lock.equals(DeityLocks.BASIC_INCENSE_PRAYER)) {
            KnowledgeUtil.grantSign(player, Signs.SOUL_SIGN);
        } else if (lock.equals(DeityLocks.HEAL_VILLAGER)) {
            KnowledgeUtil.grantSign(player, Signs.MIND_SIGN);
        } else if (lock.equals(DeityLocks.SMITE_UNDEAD)) {
            KnowledgeUtil.grantSign(player, Signs.HARMONY_SIGN);
        }
    }

    @Override
    public void onReputationChange(Player player, IReputation rep, double prev, double current) {
        if (!KnowledgeUtil.knowsSign(player, Signs.FLAME_SIGN) && current >= 3) {
            rep.setReputation(player, id, 3);
            rep.lock(player, id, DeityLocks.BASIC_INCENSE_PRAYER);
            KnowledgeUtil.grantSign(player, Signs.FLAME_SIGN);
        } else if (!KnowledgeUtil.knowsFact(player, Facts.VILLAGER_HEALING) && (current >= 15 || rep.hasLock(player, id, DeityLocks.HEAL_VILLAGER))) {
            rep.setReputation(player, id, 15);
            rep.lock(player, id, DeityLocks.HEAL_VILLAGER);
            KnowledgeUtil.grantFact(player, Facts.VILLAGER_HEALING);
        } else if (!KnowledgeUtil.knowsFact(player, Facts.SMITE) && (current >= 30 || rep.hasLock(player, id, DeityLocks.SMITE_UNDEAD))) {
            rep.setReputation(player, id, 30);
            rep.lock(player, id, DeityLocks.SMITE_UNDEAD);
            KnowledgeUtil.grantFact(player, Facts.SMITE);
            KnowledgeUtil.grantSign(player, Signs.MAGIC_SIGN);
        } else if (current >= 50) {
            rep.setReputation(player, id, 50);
        }
    }
}
