package alexthw.eidolon_repraised.gui;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.registries.EidolonDataComponents;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScriptoriumContainer extends AbstractContainerMenu {

    private final Container inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            ScriptoriumContainer.this.slotsChanged(this);
        }
    };

    private final ContainerLevelAccess access;

    public List<Sign> signs;

    public ScriptoriumContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, ContainerLevelAccess.NULL);
    }

    public ScriptoriumContainer(int id, Inventory playerInventory, ContainerLevelAccess access) {
        super(Registry.SCRIPTORIUM_CONTAINER.get(), id);
        this.access = access;
        this.addSlot(new NotesSlot(inventory, 0, -30, 33));
        this.addSlot(new NotesSlot(inventory, 1, -30, 56));

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 12 + i1 * 18, 130 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 12 + l * 18, 188));
        }

    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // we are in the player inventory
            if ((index < 0 || (index > 1 && index < 38))) {
                if (this.slots.getFirst().mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else
                    // main inventory
                    if (index >= 2 && index < 29) {
                        if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else
                        // hotbar
                        if (index >= 29) {
                            if (!this.moveItemStackTo(itemstack1, 2, 29, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                            return ItemStack.EMPTY;
                        }
            } else { // we are in the table slots
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
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

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(this.access, pPlayer, Registry.SCRIPTORIUM.get());
    }

    @Override
    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);
        // drop the items in the slots
        for (Slot slot : this.slots) {
            if (slot instanceof NotesSlot) {
                ItemStack stack = slot.getItem();
                if (!stack.isEmpty()) {
                    pPlayer.drop(stack, false);
                    slot.set(ItemStack.EMPTY);
                }
            }
        }
    }

    public void setChant(List<Sign> currentChant) {
        // takes the itemstack from the first slot and sets it to the chant,
        // then move the itemstack to the second slot
        if (currentChant.isEmpty()) {
            return;
        }
        this.access.execute((p_217003_6_, p_217003_7_) -> {
            ItemStack stack2 = this.slots.get(1).getItem().copy();
            // check if the itemstack is empty or if it is the same as the current chant
            if (stack2.isEmpty() || (stack2.getCount() < stack2.getMaxStackSize() && stack2.getOrDefault(EidolonDataComponents.SPELL, List.of()).equals(currentChant))) {
                this.slots.getFirst().remove(1);
                ItemStack stack = Registry.CHANT_SCROLL.get().getDefaultInstance();
                if (stack2.isEmpty()) {
                    stack.set(EidolonDataComponents.SPELL, currentChant);
                    stack2 = stack;
                } else {
                    stack2.grow(1);
                }
                this.slots.get(1).set(stack2);

                inventory.setChanged();
                slotsChanged(inventory);
            }
        });
    }

    static class NotesSlot extends Slot {
        public NotesSlot(Container iInventoryIn, int index, int xPosition, int yPosition) {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Registry.CHANT_SCROLL.get());
        }

    }

}