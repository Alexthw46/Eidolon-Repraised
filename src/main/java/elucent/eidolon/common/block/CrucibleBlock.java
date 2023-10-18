package elucent.eidolon.common.block;

import elucent.eidolon.common.tile.CrucibleTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CrucibleBlock extends BlockBase implements EntityBlock, LiquidBlockContainer {
    public CrucibleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CrucibleTileEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return (level1, pos, state1, tile) -> ((CrucibleTileEntity) tile).tick();
    }

    @Override
    public boolean canPlaceLiquid(@NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull Fluid pFluid) {
        if (pLevel.getBlockEntity(pPos) instanceof CrucibleTileEntity crucibleTileEntity) {
            return !crucibleTileEntity.hasWater && pFluid.isSame(Fluids.WATER);
        }
        return false;
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull FluidState pFluidState) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof CrucibleTileEntity crucibleTileEntity && pFluidState.isSource() && pFluidState.is(Fluids.WATER)) {
            crucibleTileEntity.fill();
            crucibleTileEntity.sync();
            pLevel.playSound(null, pPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }
}
