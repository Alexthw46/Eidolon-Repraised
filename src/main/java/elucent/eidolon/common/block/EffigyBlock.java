package elucent.eidolon.common.block;

import elucent.eidolon.common.tile.EffigyTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EffigyBlock extends HorizontalWaterloggableBlock implements EntityBlock {
    public EffigyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EffigyTileEntity(pos, state);
    }
}
