package elucent.eidolon.common.spell;

import elucent.eidolon.api.capability.IMana;
import elucent.eidolon.api.capability.IReputation;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.common.deity.DeityLocks;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.ModConfigSpec;

public class HealSpell extends StaticSpell {


    public HealSpell(ResourceLocation name, Sign... signs) {
        super(name, 15, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        return true;
    }

    ModConfigSpec.IntValue BASE_HEAL;
    ModConfigSpec.IntValue REP_FROM_HEAL;
    ModConfigSpec.DoubleValue DEVOTION_TO_HEAL;

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

        if (!world.isClientSide) {

            float heal = getBaseHealing();

            IReputation cap = player.getCapability(elucent.eidolon.registries.EidolonCapabilities.REPUTATION_CAPABILITY);
            if (cap == null) return;
            double devotion = cap.getReputation(Deities.LIGHT_DEITY.getId());

            heal += (float) (devotion * getDevotionToHeal());

            HitResult ray = rayTrace(player, player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE), 0, false);
            LivingEntity toHeal;
            boolean other = false;
            if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity living && living.getType().is(EntityTypeTags.UNDEAD)) {
                toHeal = living;
                other = living.getHealth() < living.getMaxHealth();
            } else toHeal = player;

            toHeal.heal(heal);
            for (MobEffectInstance effectInstance : toHeal.getActiveEffects()) {
                Holder<MobEffect> effect = effectInstance.getEffect();
                if (!effect.value().isBeneficial() && effectInstance.getCures().contains(EffectCures.MILK)) {
                    toHeal.removeEffect(effect);
                }
            }

            if (other) {
                KnowledgeUtil.grantResearchNoToast(player, DeityLocks.HEAL_VILLAGER);
                cap.addReputation(Deities.LIGHT_DEITY.getId(), getRepFromHealOther());
            }
            IMana.expendMana(player, getCost());
        }

    }

    public int getBaseHealing() {
        return BASE_HEAL == null ? 5 : BASE_HEAL.get();
    }

    public double getDevotionToHeal() {
        return DEVOTION_TO_HEAL == null ? 0.05 : DEVOTION_TO_HEAL.get();
    }

    public int getRepFromHealOther() {
        return REP_FROM_HEAL == null ? 3 : REP_FROM_HEAL.get();
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder spellBuilder) {
        super.buildConfig(spellBuilder);
        BASE_HEAL = spellBuilder.comment("Base healing amount").defineInRange("base_heal", 5, 1, 100);
        REP_FROM_HEAL = spellBuilder.comment("Reputation gained from healing other entities").defineInRange("rep_from_heal", 3, 1, 100);
        DEVOTION_TO_HEAL = spellBuilder.comment("Devotion to extra healing ratio").defineInRange("devotion_to_heal", 0.05, 0, 1);
    }
}
