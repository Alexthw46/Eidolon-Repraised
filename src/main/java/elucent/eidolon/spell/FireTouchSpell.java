package elucent.eidolon.spell;

import elucent.eidolon.capability.ISoul;
import elucent.eidolon.common.tile.IBurner;
import elucent.eidolon.network.IgniteEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.Researches;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import static net.minecraft.world.level.block.AbstractCandleBlock.LIT;


public class FireTouchSpell extends StaticSpell {

    public FireTouchSpell(ResourceLocation name, Sign... signs) {
        super(name, 10, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos blockPos, Player player) {
        //Vec3 v = getVector(world, player);
        //List<BrazierTileEntity> braziers = getTilesWithinAABB(BrazierTileEntity.class, world, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        //List<CampfireBlockEntity> campfires = getTilesWithinAABB(CampfireBlockEntity.class, world, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
        if (!KnowledgeUtil.knowsResearch(player, Researches.FIRE_SPELL.getRegistryName())) return false;
        HitResult ray = rayTrace(player, player.getReachDistance(), 0, true);
        if (ray instanceof BlockHitResult rayTraceResult) {
            BlockState hitState = world.getBlockState(rayTraceResult.getBlockPos());
            if (hitState.getBlock() instanceof CandleBlock && CandleBlock.canLight(hitState) || hitState.getBlock() instanceof CampfireBlock && CampfireBlock.canLight(hitState)) {
                return true;
            } else if (world.getBlockEntity(rayTraceResult.getBlockPos()) instanceof IBurner brazier) {
                return brazier.canStartBurning();
            }
        }
        return ray instanceof EntityHitResult;
    }

    @Override
    public void cast(Level world, BlockPos blockPos, Player player) {

        if (!world.isClientSide) {
            //Vec3 v = getVector(world, player);
            //List<BrazierTileEntity> braziers = getTilesWithinAABB(BrazierTileEntity.class, world, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));
            //List<CampfireBlockEntity> campfires = getTilesWithinAABB(CampfireBlockEntity.class, world, new AABB(v.x - 1.5, v.y - 1.5, v.z - 1.5, v.x + 1.5, v.y + 1.5, v.z + 1.5));

            HitResult ray = rayTrace(player, player.getReachDistance(), 0, true);
            if (ray instanceof BlockHitResult blockHitResult) {
                BlockState hitState = world.getBlockState(blockHitResult.getBlockPos());
                if (hitState.getBlock() instanceof CandleBlock && CandleBlock.canLight(hitState) || hitState.getBlock() instanceof CampfireBlock && CampfireBlock.canLight(hitState)) {
                    world.setBlock(blockHitResult.getBlockPos(), hitState.setValue(LIT, Boolean.TRUE), 11);
                    Networking.sendToTracking(world, blockHitResult.getBlockPos(), new IgniteEffectPacket(blockHitResult.getBlockPos(), 1.0F, 0.5F, 0.25F));
                } else if (world.getBlockEntity(blockHitResult.getBlockPos()) instanceof IBurner brazier) {
                    brazier.startBurning(player, world, blockHitResult.getBlockPos());
                }
                world.playSound(player, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            } else if (ray instanceof EntityHitResult entityHitResult) {
                entityHitResult.getEntity().setSecondsOnFire(10);
            } else return;
            ISoul.expendMana(player, getCost());
        }

    }

}