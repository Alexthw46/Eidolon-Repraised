package alexthw.eidolon_repraised.common.tile;

import alexthw.eidolon_repraised.api.ritual.IRitualItemFocus;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class NecroticFocusTileEntity extends ContainerTileBase implements IRitualItemFocus {

    public NecroticFocusTileEntity(BlockPos pos, BlockState state) {
        this(Registry.NECROTIC_FOCUS_TILE_ENTITY.get(), pos, state);
    }

    public NecroticFocusTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public void onDestroyed(BlockState state, BlockPos pos) {
        if (!stack.isEmpty())
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
    }

    @Override
    public ItemInteractionResult onActivated(BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            if (player.getItemInHand(hand).isEmpty() && !stack.isEmpty()) {
                player.addItem(stack);
                stack = ItemStack.EMPTY;
                if (!level.isClientSide) sync();
                return ItemInteractionResult.SUCCESS;
            } else if (!player.getItemInHand(hand).isEmpty() && stack.isEmpty()) {
                stack = player.getItemInHand(hand).copy();
                stack.setCount(1);
                player.getItemInHand(hand).shrink(1);
                if (player.getItemInHand(hand).isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                if (!level.isClientSide) sync();
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
    public void replace(ItemStack stack) {
        this.stack = stack;
        if (!level.isClientSide) sync();
    }
}
