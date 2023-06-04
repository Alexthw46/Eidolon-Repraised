package elucent.eidolon.common.tile;

import elucent.eidolon.Eidolon;
import elucent.eidolon.Registry;
import elucent.eidolon.common.block.WoodenStandBlock;
import elucent.eidolon.gui.WoodenBrewingStandContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;

public class WoodenStandTileEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private static final int[] SLOTS_FOR_UP = new int[]{3};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
    private static final int[] OUTPUT_SLOTS = new int[]{0, 1, 2};
    private NonNullList<ItemStack> brewingItemStacks = NonNullList.withSize(4, ItemStack.EMPTY);
    private int brewTime, heat;
    private boolean[] filledSlots;
    private Item ingredientID;
    public final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> WoodenStandTileEntity.this.brewTime;
                case 1 -> WoodenStandTileEntity.this.heat;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0 -> WoodenStandTileEntity.this.brewTime = value;
                case 1 -> WoodenStandTileEntity.this.heat = value;
            }
        }

        public int getCount() {
            return 2;
        }
    };

    public WoodenStandTileEntity(BlockPos pos, BlockState state) {
        super(Registry.WOODEN_STAND_TILE_ENTITY.get(), pos, state);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container." + Eidolon.MODID + ".wooden_brewing_stand");
    }

    @Override
    public int getContainerSize() {
        return this.brewingItemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.brewingItemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void tick() {
        boolean flag = this.canBrew();
        boolean flag1 = this.brewTime > 0;
        ItemStack itemstack1 = this.brewingItemStacks.get(3);
        if (level.getGameTime() % 20 == 0) {
            BlockEntity below = level.getBlockEntity(worldPosition.below());
            if (below instanceof CrucibleTileEntity) {
                int prevHeat = heat;
                heat = ((CrucibleTileEntity)below).boiling ? 1 : 0;
                if (prevHeat != heat) setChanged();
            }
        }
        if (flag1) {
            --this.brewTime;
            boolean flag2 = this.brewTime == 0;
            if (flag2 && flag) {
                this.brewPotions();
                this.setChanged();
            } else if (!flag || heat == 0) {
                this.brewTime = 0;
                this.setChanged();
            } else if (this.ingredientID != itemstack1.getItem()) {
                this.brewTime = 0;
                this.setChanged();
            }
        } else if (flag && heat > 0) {
            this.brewTime = 800;
            this.ingredientID = itemstack1.getItem();
            this.setChanged();
        }

        if (!this.level.isClientSide) {
            boolean[] aboolean = this.createFilledSlotsArray();
            if (!Arrays.equals(aboolean, this.filledSlots)) {
                this.filledSlots = aboolean;
                BlockState blockstate = this.level.getBlockState(this.getBlockPos());
                if (!(blockstate.getBlock() instanceof WoodenStandBlock)) {
                    return;
                }

                for(int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
                    blockstate = blockstate.setValue(BrewingStandBlock.HAS_BOTTLE[i], aboolean[i]);
                }

                this.level.setBlock(this.worldPosition, blockstate, 2);
            }
        }
    }

    public boolean[] createFilledSlotsArray() {
        boolean[] aboolean = new boolean[3];

        for(int i = 0; i < 3; ++i) {
            if (!this.brewingItemStacks.get(i).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    private boolean canBrew() {
        ItemStack itemstack = this.brewingItemStacks.get(3);
        if (!itemstack.isEmpty()) return BrewingRecipeRegistry.canBrew(brewingItemStacks, itemstack, OUTPUT_SLOTS); // divert to VanillaBrewingRegistry
        if (itemstack.isEmpty()) {
            return false;
        } else if (!PotionBrewing.isIngredient(itemstack)) {
            return false;
        } else {
            for(int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.brewingItemStacks.get(i);
                if (!itemstack1.isEmpty() && PotionBrewing.hasMix(itemstack1, itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void brewPotions() {
        if (net.minecraftforge.event.ForgeEventFactory.onPotionAttemptBrew(brewingItemStacks)) return;
        ItemStack itemstack = this.brewingItemStacks.get(3);

        BrewingRecipeRegistry.brewPotions(brewingItemStacks, itemstack, OUTPUT_SLOTS);
        net.minecraftforge.event.ForgeEventFactory.onPotionBrewed(brewingItemStacks);
        BlockPos blockpos = this.getBlockPos();
        if (itemstack.hasCraftingRemainingItem()) {
            ItemStack itemstack1 = itemstack.getCraftingRemainingItem();
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
                itemstack = itemstack1;
            } else if (!this.level.isClientSide) {
                Containers.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
            }
        } else itemstack.shrink(1);

        this.brewingItemStacks.set(3, itemstack);
        this.level.levelEvent(1035, blockpos, 0);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.brewingItemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.brewingItemStacks);
        this.brewTime = nbt.getShort("BrewTime");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putShort("BrewTime", (short) this.brewTime);
        ContainerHelper.saveAllItems(compound, this.brewingItemStacks);
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return index >= 0 && index < this.brewingItemStacks.size() ? this.brewingItemStacks.get(index) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.brewingItemStacks, index, count);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.brewingItemStacks, index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        if (index >= 0 && index < this.brewingItemStacks.size()) {
            this.brewingItemStacks.set(index, stack);
        }

    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        if (index == 3) {
            return BrewingRecipeRegistry.isValidIngredient(stack)
                   && !stack.is(Tags.Items.DUSTS_REDSTONE)
                   && !stack.is(Tags.Items.DUSTS_GLOWSTONE);
        } else {
            return BrewingRecipeRegistry.isValidInput(stack) && this.getItem(index).isEmpty();
        }
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.UP) {
            return SLOTS_FOR_UP;
        } else {
            return side == Direction.DOWN ? SLOTS_FOR_DOWN : OUTPUT_SLOTS;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        if (index == 3) {
            return stack.getItem() == Items.GLASS_BOTTLE;
        } else {
            return true;
        }
    }

    @Override
    public void clearContent() {
        this.brewingItemStacks.clear();
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new WoodenBrewingStandContainer(id, player, this, this.dataAccess);
    }

    final net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    @Override
    public <T> net.minecraftforge.common.util.@NotNull LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.UP)
                return handlers[0].cast();
            else if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[2].cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler> handler : handlers)
            handler.invalidate();
    }
}
