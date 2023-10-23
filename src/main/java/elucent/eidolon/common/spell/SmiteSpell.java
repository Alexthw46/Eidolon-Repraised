package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.common.deity.DeityLocks;
import elucent.eidolon.registries.EidolonAttributes;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class SmiteSpell extends StaticSpell {
    public SmiteSpell(ResourceLocation name, Sign... signs) {
        super(name, 40, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        var ray = rayTrace(player, player.getReachDistance(), 0, true);

        if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            return livingEntity.getMobType() == MobType.UNDEAD;
        }

        return false;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

        var ray = rayTrace(player, player.getReachDistance(), 0, true);

        if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity.getMobType() == MobType.UNDEAD) {
                if (world instanceof ServerLevel) {
                    if (livingEntity.hurt(DamageSource.MAGIC, EidolonAttributes.getSpellDamage(player, 10))) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EidolonAttributes.getSpellEffectDuration(player, 200), EidolonAttributes.getSpellEffectAmplifier(player, 2)));
                        KnowledgeUtil.grantResearchNoToast(player, DeityLocks.SMITE_UNDEAD);
                    }
                } else {
                    //TODO add cool client effects
                }
            }
        }

    }
}
