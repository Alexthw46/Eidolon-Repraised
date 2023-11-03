package elucent.eidolon.common.tile;

import elucent.eidolon.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignBlockEntityCopy extends SignBlockEntity {
    public SignBlockEntityCopy(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return Registry.SIGN_BLOCKENTITY.get();
    }
}
