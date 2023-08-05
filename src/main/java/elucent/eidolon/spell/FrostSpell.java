package elucent.eidolon.spell;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.registries.EidolonPotions;
import elucent.eidolon.registries.Researches;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class FrostSpell extends StaticSpell {

    public FrostSpell(ResourceLocation name, Sign... signs) {
        super(name, 20, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        if (!KnowledgeUtil.knowsResearch(player, Researches.FROST_SPELL.getRegistryName())) return false;
        HitResult ray = rayTrace(player, player.getBlockReach(), 0, true);
        if (ray instanceof BlockHitResult rayTraceResult) {
            var fluidState = world.getFluidState(rayTraceResult.getBlockPos());
            if (fluidState.is(Fluids.WATER) && fluidState.isSource()) {
                return true;
            }
        }
        return ray instanceof EntityHitResult;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        if (!world.isClientSide) {
            HitResult ray = rayTrace(player, player.getBlockReach(), 0, true);
            if (ray instanceof BlockHitResult blockHitResult) {
                var fluidState = world.getFluidState(blockHitResult.getBlockPos());
                if (fluidState.is(Fluids.WATER) && fluidState.isSource()) {
                    world.setBlockAndUpdate(blockHitResult.getBlockPos(), Blocks.ICE.defaultBlockState());
                    world.playSound(player, pos, SoundEvents.PLAYER_HURT_FREEZE, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                } else return;
            } else if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(EidolonPotions.CHILLED_EFFECT.get(), 200));
            } else return;
            ISoul.expendMana(player, getCost());
        }
    }
}
