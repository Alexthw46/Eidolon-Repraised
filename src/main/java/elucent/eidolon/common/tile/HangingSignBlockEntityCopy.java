package elucent.eidolon.common.tile;

import elucent.eidolon.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class HangingSignBlockEntityCopy extends SignBlockEntity {

    public HangingSignBlockEntityCopy(BlockPos p_250603_, BlockState p_251674_) {
        super(p_250603_, p_251674_);
    }

    public int getTextLineHeight() {
        return 9;
    }

    public int getMaxTextLineWidth() {
        return 60;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return Registry.H_SIGN_BLOCKENTITY.get();
    }
}
