package elucent.eidolon.common.spell;

import elucent.eidolon.api.capability.IMana;
import elucent.eidolon.api.spells.Sign;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class WaterSpell extends StaticSpell {
    public WaterSpell(ResourceLocation name, Sign... signs) {
        super(name, 10, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue(), 0, true);
        if (world.dimensionType().ultraWarm() || world.isClientSide())
            return;

        if (ray instanceof BlockHitResult rayTraceResult) {
            BlockPos pos1 = rayTraceResult.getBlockPos();
            BlockState hitState = world.getBlockState(pos1);
            BlockEntity blockEntity = world.getBlockEntity(pos1);

            if (hitState.getBlock() instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.canPlaceLiquid(player, world, pos1, world.getBlockState(pos1), Fluids.WATER)) {
                liquidBlockContainer.placeLiquid(world, pos1, hitState, Fluids.WATER.getSource(true));
            } else {
                var fluidHandler = world.getCapability(Capabilities.FluidHandler.BLOCK, rayTraceResult.getBlockPos(), rayTraceResult.getDirection());
                if (fluidHandler != null) {
                    fluidHandler.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
                } else if (world.getBlockState(pos1.relative(rayTraceResult.getDirection())).canBeReplaced(Fluids.WATER)) {
                    pos1 = pos1.relative(rayTraceResult.getDirection());
                    world.setBlockAndUpdate(pos1, Blocks.WATER.defaultBlockState());
                }
                IMana.expendMana(player, getCost());
            }
        }
    }
}
