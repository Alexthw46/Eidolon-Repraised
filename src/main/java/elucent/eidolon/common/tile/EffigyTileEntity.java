package elucent.eidolon.common.tile;

import elucent.eidolon.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EffigyTileEntity extends TileEntityBase {
    long previous = -1;

    public EffigyTileEntity(BlockPos pos, BlockState state) {
        super(Registry.EFFIGY_TILE_ENTITY.get(), pos, state);
    }

    public boolean ready() {
        return true; // world.getGameTime() - previous >= 24000;
    }

    public void pray() {
        if (!level.isClientSide) {
            previous = level.getGameTime();
            sync();
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        previous = tag.getLong("previous");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putLong("previous", previous);
    }
}
