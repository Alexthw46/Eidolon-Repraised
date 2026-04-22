package alexthw.eidolon_repraised.gui;

import alexthw.eidolon_repraised.recipe.WorktableRecipe;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static net.neoforged.neoforge.common.CommonHooks.setCraftingPlayer;

public class WorktableResultSlot extends Slot {
    private final CraftingContainer core, extras;
    private final Player player;
    private int amountCrafted;

    public WorktableResultSlot(Player player, CraftingContainer core, CraftingContainer extras, Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
        this.core = core;
        this.extras = extras;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.amountCrafted += Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    @Override
    protected void onQuickCraft(@NotNull ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.checkTakeAchievements(stack);
    }

    protected void onSwapCraft(int numItemsCrafted) {
        this.amountCrafted += numItemsCrafted;
    }

    @Override
    protected void checkTakeAchievements(@NotNull ItemStack stack) {
        if (this.amountCrafted > 0) {
            stack.onCraftedBy(this.player.level(), this.player, this.amountCrafted);
            EventHooks.firePlayerCraftingEvent(this.player, stack, core);
        }

        if (this.container instanceof ResultContainer resultContainer) {
            resultContainer.awardUsedRecipes(this.player, Collections.singletonList(stack));
        }

        player.playSound(SoundEvents.SMITHING_TABLE_USE, 1.0f, 1.0f);

        this.amountCrafted = 0;
    }

    @Override
    public void onTake(@NotNull Player thePlayer, @NotNull ItemStack stack) {
        this.checkTakeAchievements(stack);
        CommonHooks.setCraftingPlayer(thePlayer);
        RecipeHolder<WorktableRecipe> recipeHolder = thePlayer.level().getRecipeManager().getAllRecipesFor(EidolonRecipes.WORKTABLE_TYPE.get()).stream().filter(
                holder -> holder.value().matches(core, extras)
        ).findFirst().orElse(null);
        WorktableRecipe recipe = recipeHolder != null ? recipeHolder.value() : null;
        NonNullList<ItemStack> items = null;
        if (recipe != null) {
            items = recipe.getRemainingItems(core, extras);
        } else {
            // Get remaining items for normal crafting recipes. Some recipes return a list
            // shorter than 9 (e.g. smaller shaped recipes). Ensure we pad to 9 so we
            // process all core slots (3x3) and then append extras.
            NonNullList<ItemStack> remaining = thePlayer.level().getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, core.asCraftInput(), thePlayer.level());
            NonNullList<ItemStack> padded = NonNullList.withSize(9, ItemStack.EMPTY);
            for (int i = 0; i < remaining.size() && i < 9; i++) {
                padded.set(i, remaining.get(i));
            }
            items = NonNullList.create();
            items.addAll(padded);
            for (int i = 0; i < 4; i++) items.add(extras.getItem(i));
        }
        setCraftingPlayer(null);
        assert items != null;

        int n = recipe == null ? Math.min(9, items.size()) : items.size();
        for (int i = 0; i < n; ++i) {
            Container inv = i < 9 ? core : extras;
            int index = i < 9 ? i : i - 9;
            ItemStack item = inv.getItem(index);
            ItemStack remaining = items.get(i);
            if (!item.isEmpty()) {
                inv.removeItem(index, 1);
                item = inv.getItem(index);
            }

            if (!remaining.isEmpty()) {
                if (item.isEmpty()) {
                    inv.setItem(index, remaining);
                } else if (ItemStack.isSameItem(item, remaining) && ItemStack.isSameItemSameComponents(item, remaining)) {
                    remaining.grow(item.getCount());
                    inv.setItem(index, remaining);
                } else if (!this.player.getInventory().add(remaining)) {
                    this.player.drop(remaining, false);
                }
            }
        }
    }
}
