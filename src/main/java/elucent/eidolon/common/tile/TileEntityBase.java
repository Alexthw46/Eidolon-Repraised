package elucent.eidolon.common.tile;

import elucent.eidolon.network.Networking;
import elucent.eidolon.network.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

public class TileEntityBase extends BlockEntity {
    public TileEntityBase(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public void onDestroyed(BlockState state, BlockPos pos) {
        invalidateCaps();
    }

    public InteractionResult onActivated(BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public void sync() {
        setChanged();
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        if (level.isClientSide)
            Networking.INSTANCE.sendToServer(new TESyncPacket(worldPosition, tag));
        else {
            Networking.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new TESyncPacket(worldPosition, tag));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag); // (this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (pkt.getTag() != null) handleUpdateTag(pkt.getTag());
    }
}
