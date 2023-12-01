package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.common.deity.DeityLocks;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class HealSpell extends StaticSpell {


    public HealSpell(ResourceLocation name, Sign... signs) {
        super(name, 15, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

        if (!world.isClientSide) {

            float heal = 5F;

            var cap = world.getCapability(IReputation.INSTANCE).resolve().isPresent() ? world.getCapability(IReputation.INSTANCE).resolve().get() : null;
            if (cap == null) return;
            double devotion = cap.getReputation(player.getUUID(), Deities.LIGHT_DEITY.getId());

            heal += (float) (devotion / 20F);

            HitResult ray = rayTrace(player, player.getReachDistance(), 0, false);
            LivingEntity toHeal;
            boolean other = false;
            if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity living && living.getMobType() != MobType.UNDEAD) {
                toHeal = living;
                other = living.getHealth() < living.getMaxHealth();
            } else toHeal = player;

            toHeal.heal(heal);
            for (MobEffectInstance effectInstance : toHeal.getActiveEffects()) {
                MobEffect effect = effectInstance.getEffect();
                if (!effect.isBeneficial() && effect.getCurativeItems().contains(Items.MILK_BUCKET.getDefaultInstance())) {
                    toHeal.removeEffect(effect);
                }
            }

            if (other) {
                KnowledgeUtil.grantResearchNoToast(player, DeityLocks.HEAL_VILLAGER);
                world.getCapability(IReputation.INSTANCE).ifPresent(rep -> rep.addReputation(player, Deities.LIGHT_DEITY.getId(), 3));
            }
            ISoul.expendMana(player, getCost());
        }

    }

}
