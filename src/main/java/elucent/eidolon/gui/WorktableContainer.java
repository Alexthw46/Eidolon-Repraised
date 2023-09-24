package elucent.eidolon.gui;

import elucent.eidolon.recipe.WorktableRecipe;
import elucent.eidolon.registries.Registry;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WorktableContainer extends AbstractContainerMenu {
    final CraftingContainer core = new TransientCraftingContainer(this, 3, 3);
    final CraftingContainer extras = new TransientCraftingContainer(this, 2, 2);
    final ResultContainer result = new ResultContainer();
    final Player player;
    final ContainerLevelAccess callable;

    public WorktableContainer(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public WorktableContainer(int id, Inventory inventory, ContainerLevelAccess callable) {
        super(Registry.WORKTABLE_CONTAINER.get(), id);
        this.player = inventory.player;
        this.callable = callable;
        this.addSlot(new WorktableResultSlot(inventory.player, core, extras, result, 0, 163, 58));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.core, j + i * 3, 40 + j * 18, 40 + i * 18));
            }
        }
        this.addSlot(new Slot(this.extras, 0, 58, 18));
        this.addSlot(new Slot(this.extras, 1, 98, 58));
        this.addSlot(new Slot(this.extras, 2, 58, 98));
        this.addSlot(new Slot(this.extras, 3, 18, 58));

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(inventory, i1 + k * 9 + 9, 16 + i1 * 18, 142 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 16 + l * 18, 200));
        }
    }

    protected void updateCraftingResult(int id, Level world, Player player, CraftingContainer inventory, ResultContainer inventoryResult) {
        if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ItemStack itemstack = ItemStack.EMPTY;
            var worktableRecipeOptional = world.getRecipeManager().getAllRecipesFor(WorktableRecipe.Type.INSTANCE);
            if (!extras.isEmpty()) {
                for (WorktableRecipe worktableRecipe : worktableRecipeOptional) {
                    if (worktableRecipe.matches(core, extras)) {
                        itemstack = worktableRecipe.getResult();
                        break;
                    }
                }
            }
            if (itemstack.isEmpty()) {
                Optional<CraftingRecipe> optional = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, inventory, world);
                if (optional.isPresent()) {
                    CraftingRecipe icraftingrecipe = optional.get();
                    if (inventoryResult.setRecipeUsed(world, serverPlayer, icraftingrecipe)) {
                        itemstack = icraftingrecipe.assemble(inventory, world.registryAccess());
                    }
                }
            }

            inventoryResult.setItem(0, itemstack);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(id, incrementStateId(), 0, itemstack));
        }
    }

    @Override
    public void slotsChanged(@NotNull Container inventoryIn) {
        callable.execute((p_217069_1_, p_217069_2_) -> updateCraftingResult(this.containerId, p_217069_1_, player, core, result));
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        callable.execute((p_217068_2_, p_217068_3_) -> {
            this.clearContainer(playerIn, this.core);
            this.clearContainer(playerIn, this.extras);
        });
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(this.callable, playerIn, Registry.WORKTABLE.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                callable.execute((p_217067_2_, p_217067_3_) -> itemstack1.getItem().onCraftedBy(itemstack1, p_217067_2_, playerIn));
                if (!this.moveItemStackTo(itemstack1, 14, 50, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= 14 && index < 50) {
                if (!this.moveItemStackTo(itemstack1, 1, 14, false)) {
                    if (index < 41) {
                        if (!this.moveItemStackTo(itemstack1, 41, 50, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 14, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 14, 50, false)) {
                return ItemStack.EMPTY;
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
    public int getSize() {
        return 14;
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slotIn) {
        return slotIn.container != result && super.canTakeItemForPickAll(stack, slotIn);
    }

    public int getOutputSlot() {
        return 0;
    }
}
