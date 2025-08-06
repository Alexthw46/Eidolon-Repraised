package alexthw.eidolon_repraised.common.tile;

import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        previous = tag.getLong("previous");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        tag.putLong("previous", previous);
    }
}
