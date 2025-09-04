package alexthw.eidolon_repraised.common.tile;

import alexthw.eidolon_repraised.api.ritual.IRitualItemProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import static alexthw.eidolon_repraised.registries.Registry.HAND_TILE_ENTITY;

public class HandTileEntity extends ContainerTileBase implements IRitualItemProvider {

    public HandTileEntity(BlockPos pos, BlockState state) {
        this(HAND_TILE_ENTITY.get(), pos, state);
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
    public ItemInteractionResult onActivated(BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && level != null && !level.isClientSide) {
            ItemStack itemInHand = player.getItemInHand(hand);
            if (itemInHand.isEmpty() && !stack.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                stack = ItemStack.EMPTY;
                if (!level.isClientSide) sync();
                return ItemInteractionResult.SUCCESS;
            } else if (!itemInHand.isEmpty() && stack.isEmpty()) {
                stack = itemInHand.split(1);
                if (!level.isClientSide) sync();
                return ItemInteractionResult.SUCCESS;
            } else if (!itemInHand.isEmpty()) {
                ItemStack oldstack = stack.copy();
                stack = itemInHand.split(1);
                ItemHandlerHelper.giveItemToPlayer(player, oldstack);
                if (!level.isClientSide) sync();
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.SUCCESS;
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
