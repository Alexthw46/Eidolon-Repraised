package elucent.eidolon.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityBase extends BlockEntity {
    public TileEntityBase(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public void onDestroyed(BlockState state, BlockPos pos) {
        // invalidateCaps();
    }

    public ItemInteractionResult onActivated(BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public InteractionResult onActivated(BlockState state, BlockPos pos, Player player) {
        return InteractionResult.PASS;
    }

    public void sync(HolderLookup.Provider registries) {
        setChanged();
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        if (level == null) return;
        if (!level.isClientSide()) {
            //TODO Look into this
            //Networking.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new TESyncPacket(worldPosition, tag));

        }
    }

//    @Override
//    public @NotNull CompoundTag getUpdateTag() {
//        CompoundTag tag = new CompoundTag();
//        this.saveAdditional(tag);
//        return tag;
//    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag); // (this.worldPosition, 3, this.getUpdateTag());
    }

//    @Override
//    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
//        super.onDataPacket(net, pkt);
//        if (pkt.getTag() != null) handleUpdateTag(pkt.getTag());
//    }
}
