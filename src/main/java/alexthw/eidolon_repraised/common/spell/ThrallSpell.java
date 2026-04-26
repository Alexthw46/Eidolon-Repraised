package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.deity.DeityLocks;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.util.EntityUtil;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import static alexthw.eidolon_repraised.Eidolon.prefix;

public class ThrallSpell extends StaticSpell {

    public static final TagKey<EntityType<?>> ENTHRALL_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, prefix("enthrall_blacklist"));
    public static final TagKey<EntityType<?>> ENTHRALL_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, prefix("enthrall_whitelist"));


    public ThrallSpell(ResourceLocation name, Sign... signs) {
        super(name, 50, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 3, 0, false);
        if (ray instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity living) {
            var entityType = living.getType();
            if (entityType.is(ENTHRALL_WHITELIST) || entityType.is(EntityTypeTags.UNDEAD) && !entityType.is(ENTHRALL_BLACKLIST)) {
                return true;
            }
        }
        player.displayClientMessage(Component.translatable("eidolon_repraised.message.no_target"), true);
        return false;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        if (world instanceof ServerLevel && player instanceof ServerPlayer sp) {
            HitResult ray = rayTrace(player, player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 3, 0, false);
            if (ray instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity living) {
                float actualCost = 2 * getCost() * living.getHealth() / living.getMaxHealth();
                var manaCap = player.getCapability(EidolonCapabilities.MANA_CAPABILITY);
                if (manaCap != null && manaCap.getMagic() >= actualCost) {
                    manaCap.takeMagic(actualCost);
                    EntityUtil.enthrall(player, living);
                    KnowledgeUtil.grantResearchNoToast(player, DeityLocks.ENTHRALL_UNDEAD);
                } else
                    sp.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("eidolon_repraised.title.no_mana")));
            }
        }
    }
}
