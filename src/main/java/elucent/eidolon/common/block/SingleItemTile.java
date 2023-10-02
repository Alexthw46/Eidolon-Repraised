package elucent.eidolon.common.block;

import elucent.eidolon.common.tile.TileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SingleItemTile extends TileEntityBase implements Container {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    protected ItemStack stack = ItemStack.EMPTY;
    public ItemEntity renderEntity;

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
        sync();
        return copyStack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        sync();
        return stack;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        this.stack = pStack;
        sync();
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
        sync();
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void setStack(ItemStack otherStack) {
        this.stack = otherStack;
        sync();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, final @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        itemHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        stack = ItemStack.of((CompoundTag) compound.get("itemStack"));
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (stack != null) {
            CompoundTag stackTag = new CompoundTag();
            stack.save(stackTag);
            tag.put("itemStack", stackTag);
        }
    }
}