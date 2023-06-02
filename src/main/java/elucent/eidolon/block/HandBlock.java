package elucent.eidolon.block;

import elucent.eidolon.tile.HandTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class HandBlock extends HorizontalWaterloggableBlock implements EntityBlock {
    public HandBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new HandTileEntity(pos, state);
    }
}
