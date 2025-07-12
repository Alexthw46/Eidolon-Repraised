package elucent.eidolon.common.spell;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.api.capability.ISoul;
import elucent.eidolon.common.deity.DeityLocks;
import elucent.eidolon.util.EntityUtil;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import var;

import static elucent.eidolon.Eidolon.prefix;

public class ThrallSpell extends StaticSpell {

    public static final TagKey<EntityType<?>> ENTHRALL_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, prefix("enthrall_blacklist"));
    public static final TagKey<EntityType<?>> ENTHRALL_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, prefix("enthrall_whitelist"));


    public ThrallSpell(ResourceLocation name, Sign... signs) {
        super(name, 50, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getBlockReach() + 3, 0, false);
        if (ray instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity living) {
            var type = Eidolon.isValidUndead(living);
            return (!living.getType().is(ENTHRALL_BLACKLIST) && type == MobType.UNDEAD) || living.getType().is(ENTHRALL_WHITELIST);
        }
        return false;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        if (world instanceof ServerLevel && player instanceof ServerPlayer sp) {
            HitResult ray = rayTrace(player, player.getBlockReach() + 3, 0, false);
            if (ray instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity living) {
                float actualCost = 2 * getCost() * living.getHealth() / living.getMaxHealth();
                player.getCapability(ISoul.INSTANCE).ifPresent(soul -> {
                    if (soul.getMagic() >= actualCost) {
                        soul.takeMagic(actualCost);
                        EntityUtil.enthrall(player, living);
                        KnowledgeUtil.grantResearchNoToast(player, DeityLocks.ENTHRALL_UNDEAD);
                    } else
                        sp.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("eidolon.title.no_mana")));
                });
            }
        }
    }
}
