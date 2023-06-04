package elucent.eidolon.common.tile;

import elucent.eidolon.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CenserTileEntity extends TileEntityBase {
    public CenserTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public CenserTileEntity(BlockPos pos, BlockState state) {
        super(Registry.CENSER_TILE_ENTITY.get(), pos, state);
    }
}
