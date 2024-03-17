package elucent.eidolon.common.tile;

import elucent.eidolon.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ScriptoriumTile extends TileEntityBase {
    public ScriptoriumTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }


    public ScriptoriumTile(BlockPos pos, BlockState state) {
        super(Registry.SCRIPTORIUM_TILE.get(), pos, state);
    }


}
