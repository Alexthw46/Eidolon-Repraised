package alexthw.eidolon_repraised.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ContainerTileBase extends TileEntityBase implements Container {
    ItemStack stack = ItemStack.EMPTY;

    public ContainerTileBase(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
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

    @Override
    protected void loadAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        this.stack = ItemStack.parseOptional(pRegistries, compound.getCompound("itemStack"));
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (!stack.isEmpty()) {
            try {
                Tag stackTag = stack.save(pRegistries);
                tag.put("itemStack", stackTag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
