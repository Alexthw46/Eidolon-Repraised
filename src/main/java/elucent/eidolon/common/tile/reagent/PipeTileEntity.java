package elucent.eidolon.common.tile.reagent;

import elucent.eidolon.Registry;
import elucent.eidolon.common.block.PipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PipeTileEntity extends ReagentTankTileEntity {

    public PipeTileEntity(BlockPos pos, BlockState state) {
        this(Registry.PIPE_TILE_ENTITY.get(), pos, state);
    }

    public PipeTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state, 128);
    }

    @Override
    public boolean isOutput(Direction direction) {
        return getBlockState().getValue(PipeBlock.OUT) == direction;
    }

    @Override
    public boolean isInput(Direction direction) {
        return getBlockState().getValue(PipeBlock.IN) == direction;
    }
}
