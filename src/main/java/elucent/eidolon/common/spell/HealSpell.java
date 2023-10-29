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

import java.util.concurrent.atomic.AtomicReference;

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

            AtomicReference<Float> heal = new AtomicReference<>(5F);

            world.getCapability(IReputation.INSTANCE).ifPresent(
                    iReputation -> heal.updateAndGet(v -> (float) (v + iReputation.getReputation(player.getUUID(), Deities.LIGHT_DEITY.getId()) / 20F))
            );

            HitResult ray = rayTrace(player, player.getReachDistance(), 0, true);
            LivingEntity toHeal;
            boolean other = false;
            if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity living && living.getMobType() != MobType.UNDEAD) {
                toHeal = living;
                other = living.getHealth() < living.getMaxHealth();
            } else toHeal = player;

            toHeal.heal(heal.get());
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
