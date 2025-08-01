package elucent.eidolon.common.block;

import elucent.eidolon.common.tile.TileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SingleItemTile extends TileEntityBase implements Container {
    protected ItemStack stack = ItemStack.EMPTY;

    public SingleItemTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int pSlot) {
        return stack;
    }

    @Override
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack copyStack = stack.copy().split(pAmount);
        stack.shrink(pAmount);
        sync(level.registryAccess());
        return copyStack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        sync(level.registryAccess());
        return stack;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        this.stack = pStack;
        sync(level.registryAccess());
    }

    @Override
    public boolean canPlaceItem(int pIndex, @NotNull ItemStack pStack) {
        return stack.isEmpty();
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void clearContent() {
        this.stack = ItemStack.EMPTY;
        sync(level.registryAccess());
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void setStack(ItemStack otherStack) {
        this.stack = otherStack;
        sync(level.registryAccess());
    }

//    @NotNull
//    @Override
//    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, final @Nullable Direction side) {
//        if (cap == Capabilities.ITEM_HANDLER) {
//            return itemHandler.cast();
//        }
//        return super.getCapability(cap, side);
//    }
//
//    @Override
//    public void invalidateCaps() {
//        itemHandler.invalidate();
//        super.invalidateCaps();
//    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        stack = ItemStack.parseOptional(registries,tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (stack != null) {
            tag.put("stack", stack.saveOptional(registries));
        }
    }
}