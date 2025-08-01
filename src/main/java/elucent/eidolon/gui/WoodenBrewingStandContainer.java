package elucent.eidolon.gui;

import elucent.eidolon.registries.Registry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WoodenBrewingStandContainer extends AbstractContainerMenu {
    private final Container tileBrewingStand;
    private final ContainerData intArray;
    private final Slot slot;

    public WoodenBrewingStandContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(4), new SimpleContainerData(2));
    }

    public WoodenBrewingStandContainer(int id, Inventory playerInventory, Container inventory, ContainerData p_i50096_4_) {
        super(Registry.WOODEN_STAND_CONTAINER.get(), id);
        checkContainerSize(inventory, 4);
        checkContainerDataCount(p_i50096_4_, 2);
        this.tileBrewingStand = inventory;
        this.intArray = p_i50096_4_;
        this.addSlot(new PotionSlot(inventory, 0, 56, 51));
        this.addSlot(new PotionSlot(inventory, 1, 79, 58));
        this.addSlot(new PotionSlot(inventory, 2, 102, 51));
        this.slot = this.addSlot(new IngredientSlot(inventory, 3, 79, 17));
        this.addDataSlots(p_i50096_4_);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    public boolean stillValid(@NotNull Player playerIn) {
        return this.tileBrewingStand.stillValid(playerIn);
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if ((index < 0 || index > 2) && index != 3) {
                if (this.slot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (PotionSlot.mayPlaceItem(playerIn.level().potionBrewing(), itemstack) && itemstack.getCount() == 1) {
                    if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 4 && index < 31) {
                    if (!this.moveItemStackTo(itemstack1, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 31 && index < 40) {
                    if (!this.moveItemStackTo(itemstack1, 4, 31, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @OnlyIn(Dist.CLIENT)
    public int getHeat() {
        return this.intArray.get(1);
    }

    @OnlyIn(Dist.CLIENT)
    public int getTime() {
        return this.intArray.get(0);
    }

    static class IngredientSlot extends PotionSlot {
        public IngredientSlot(Container iInventoryIn, int index, int xPosition, int yPosition) {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return super.mayPlace(stack) && !stack.is(Tags.Items.DUSTS_REDSTONE)
                    && !stack.is(Tags.Items.DUSTS_GLOWSTONE);
        }

        public int getMaxStackSize() {
            return 64;
        }
    }

    static class PotionSlot extends Slot {
        private final PotionBrewing potionBrewing;

        public PotionSlot(Container container, int slot, int x, int y) {
            this(PotionBrewing.EMPTY, container, slot, x, y);
        }

        public PotionSlot(PotionBrewing potionBrewing, Container p_39123_, int p_39124_, int p_39125_, int p_39126_) {
            super(p_39123_, p_39124_, p_39125_, p_39126_);
            this.potionBrewing = potionBrewing;
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return mayPlaceItem(this.potionBrewing, stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void onTake(@NotNull Player player, ItemStack stack) {
            Optional<Holder<Potion>> optional = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
            if (optional.isPresent() && player instanceof ServerPlayer serverplayer) {
                net.neoforged.neoforge.event.EventHooks.onPlayerBrewedPotion(player, stack);
                CriteriaTriggers.BREWED_POTION.trigger(serverplayer, optional.get());
            }

            super.onTake(player, stack);
        }

        /**
         * Returns {@code true} if this {@link net.minecraft.world.item.ItemStack} can be filled with a potion.
         */
        @Deprecated // Neo: use the overload that takes PotionBrewing instead
        public static boolean mayPlaceItem(ItemStack stack) {
            return stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION) || stack.is(Items.GLASS_BOTTLE);
        }

        public static boolean mayPlaceItem(PotionBrewing potionBrewing, ItemStack p_39134_) {
            return potionBrewing.isInput(p_39134_) || p_39134_.is(Items.GLASS_BOTTLE);
        }
    }

}
