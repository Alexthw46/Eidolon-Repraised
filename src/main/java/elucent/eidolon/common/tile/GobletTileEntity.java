package elucent.eidolon.common.tile;

import elucent.eidolon.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static elucent.eidolon.util.RegistryUtil.getRegistryName;

public class GobletTileEntity extends TileEntityBase {
    EntityType<?> type = null;

    public GobletTileEntity(BlockPos pos, BlockState state) {
        super(Registry.GOBLET_TILE_ENTITY.get(), pos, state);
    }

    public EntityType<?> getEntityType() {
        return type;
    }

    public void setEntityType(EntityType<?> type) {
        this.type = type;
        sync();
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("type"))
            type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(tag.getString("type")));
        else type = null;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        if (type != null) tag.putString("type", getRegistryName(type).toString());
    }
}
