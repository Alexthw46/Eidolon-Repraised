package elucent.eidolon.common.tile;

import elucent.eidolon.Registry;
import elucent.eidolon.api.ritual.IRitualItemProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class HandTileEntity extends TileEntityBase implements IRitualItemProvider {
    ItemStack stack = ItemStack.EMPTY;

    public HandTileEntity(BlockPos pos, BlockState state) {
        this(Registry.HAND_TILE_ENTITY.get(), pos, state);
    }

    public HandTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public void onDestroyed(BlockState state, BlockPos pos) {
        if (!stack.isEmpty())
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
    }

    @Override
    public InteractionResult onActivated(BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && level != null) {
            ItemStack itemInHand = player.getItemInHand(hand);
            if (itemInHand.isEmpty() && !stack.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                stack = ItemStack.EMPTY;
                if (!level.isClientSide) sync();
                return InteractionResult.SUCCESS;
            } else if (!itemInHand.isEmpty() && stack.isEmpty()) {
                stack = itemInHand.split(1);
                if (!level.isClientSide) sync();
                return InteractionResult.SUCCESS;
            } else if (!itemInHand.isEmpty() && !stack.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                stack = itemInHand.split(1);
                if (!level.isClientSide) sync();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        stack = ItemStack.of(tag.getCompound("stack"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("stack", stack.save(new CompoundTag()));
    }

    @Override
    public ItemStack provide() {
        return stack.copy();
    }

    @Override
    public void take() {
        stack = ItemStack.EMPTY;
        if (!level.isClientSide) sync();
    }
}
