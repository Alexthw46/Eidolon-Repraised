package alexthw.eidolon_repraised.common.deity;

import alexthw.eidolon_repraised.api.deity.Deity;
import alexthw.eidolon_repraised.capability.Facts;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class DarkDeity extends Deity {
    public DarkDeity(ResourceLocation id, int red, int green, int blue) {
        super(id, red, green, blue);
        progression.add(new Stage(DeityLocks.SACRIFICE_MOB, 3, true)
                .requirement(new ResearchRequirement(DeityLocks.SACRIFICE_MOB))
        ).add(new Stage(DeityLocks.DARK_TOUCH, 10, false)
        ).add(new Stage(DeityLocks.SACRIFICE_VILLAGER, 15, true)
                .requirement(new ResearchRequirement(DeityLocks.SACRIFICE_VILLAGER))
        ).add(new Stage(DeityLocks.ZOMBIFY_VILLAGER, 35, true)
                .requirement(new ResearchRequirement(DeityLocks.ZOMBIFY_VILLAGER))
        ).add(new Stage(DeityLocks.ENTHRALL_UNDEAD, 50, true)
                .requirement(new ResearchRequirement(DeityLocks.ENTHRALL_UNDEAD))
        ).setMax(100);
    }

    @Override
    public void onReputationUnlock(Player player, ResourceLocation lock) {
        if (lock.equals(DeityLocks.SACRIFICE_MOB)) {
            KnowledgeUtil.grantSign(player, Signs.SOUL_SIGN);
        } else if (lock.equals(DeityLocks.DARK_TOUCH) && player instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("eidolon.title.new_fact")));
        } else if (lock.equals(DeityLocks.SACRIFICE_VILLAGER)) {
            KnowledgeUtil.grantSign(player, Signs.MIND_SIGN);
        } else if (lock.equals(DeityLocks.ZOMBIFY_VILLAGER)) {
            KnowledgeUtil.grantSign(player, Signs.HARMONY_SIGN);
        } else if (lock.equals(DeityLocks.ENTHRALL_UNDEAD)) {
            KnowledgeUtil.grantSign(player, Signs.WARDING_SIGN);
        }
    }

    @Override
    public void onReputationLock(Player player, ResourceLocation lock) {
        if (lock.equals(DeityLocks.SACRIFICE_MOB)) {
            KnowledgeUtil.grantSign(player, Signs.BLOOD_SIGN);
        } else if (lock.equals(DeityLocks.SACRIFICE_VILLAGER)) {
            KnowledgeUtil.grantFact(player, Facts.VILLAGER_SACRIFICE);
        } else if (lock.equals(DeityLocks.ZOMBIFY_VILLAGER)) {
            KnowledgeUtil.grantFact(player, Facts.ZOMBIFY);
            KnowledgeUtil.grantSign(player, Signs.DEATH_SIGN);
        } else if (lock.equals(DeityLocks.ENTHRALL_UNDEAD)) {
            KnowledgeUtil.grantFact(player, Facts.ENTHRALL);
            KnowledgeUtil.grantSign(player, Signs.MAGIC_SIGN);
        }
    }

}
