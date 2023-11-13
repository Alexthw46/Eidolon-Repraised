package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.ISoul;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

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
        HitResult ray = rayTrace(player, player.getBlockReach(), 0, true);
        if (world.dimensionType().ultraWarm() || world.isClientSide())
            return;

        if (ray instanceof BlockHitResult rayTraceResult) {
            BlockPos pos1 = rayTraceResult.getBlockPos();
            BlockState hitState = world.getBlockState(pos1);
            BlockEntity blockEntity = world.getBlockEntity(pos1);

            if (hitState.getBlock() instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.canPlaceLiquid(world, pos1, world.getBlockState(pos1), Fluids.WATER)) {
                liquidBlockContainer.placeLiquid(world, pos1, hitState, Fluids.WATER.getSource(true));
            } else if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, rayTraceResult.getDirection()).isPresent()) {
                blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, rayTraceResult.getDirection()).ifPresent(fluidHandler -> fluidHandler.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE));
            } else if (hitState == Blocks.CAULDRON.defaultBlockState()) {
                world.setBlockAndUpdate(pos1, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
            } else if (world.getBlockState(pos1.relative(rayTraceResult.getDirection())).canBeReplaced(Fluids.WATER)) {
                pos1 = pos1.relative(rayTraceResult.getDirection());
                world.setBlockAndUpdate(pos1, Blocks.WATER.defaultBlockState());
            }
            ISoul.expendMana(player, getCost());
        }
    }
}
