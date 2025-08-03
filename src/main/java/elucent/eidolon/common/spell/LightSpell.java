package elucent.eidolon.common.spell;

import elucent.eidolon.api.capability.IMana;
import elucent.eidolon.api.capability.IReputation;
import elucent.eidolon.api.deity.Deity;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.common.block.GhostLight;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.registries.EidolonCapabilities;
import elucent.eidolon.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

public class LightSpell extends StaticSpell {

    final Deity deity;

    public LightSpell(ResourceLocation name, Deity deity, Sign... signs) {
        super(name, 3, signs);
        this.deity = deity;
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        return reputation != null && reputation.getReputation(deity.getId()) >= 3;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, true);
        if (ray instanceof BlockHitResult blockHitResult) {
            BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
            if (world.getBlockState(blockPos).canBeReplaced() && world.isUnobstructed(Registry.GHOST_LIGHT.get().defaultBlockState(), blockPos, CollisionContext.of(player))) {
                BlockState lightBlockState = Registry.GHOST_LIGHT.get().defaultBlockState();
                if (deity.getId().equals(Deities.DARK_DEITY_ID)) {
                    lightBlockState = lightBlockState.setValue(GhostLight.DEITY, false);
                }
                world.setBlockAndUpdate(blockPos, lightBlockState);
                world.sendBlockUpdated(blockPos, world.getBlockState(blockPos), world.getBlockState(blockPos), 2);
                IMana.expendMana(player, getCost());
            }
        } else if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
            IMana.expendMana(player, getCost());
        }
    }

}
