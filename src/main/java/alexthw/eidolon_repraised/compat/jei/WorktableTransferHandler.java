package alexthw.eidolon_repraised.compat.jei;

import alexthw.eidolon_repraised.gui.WorktableContainer;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.WorktableTransferPacket;
import alexthw.eidolon_repraised.recipe.WorktableRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static alexthw.eidolon_repraised.compat.jei.JEIdolon.WORKTABLE_CATEGORY;
import static alexthw.eidolon_repraised.registries.Registry.WORKTABLE_CONTAINER;

public class WorktableTransferHandler implements IRecipeTransferHandler<WorktableContainer, WorktableRecipe> {

    IRecipeTransferHandlerHelper transferHelper;

    public WorktableTransferHandler(IRecipeTransferHandlerHelper jeiHelpers) {
        this.transferHelper = jeiHelpers;
    }

    @Override
    public @NotNull Class<? extends WorktableContainer> getContainerClass() {
        return WorktableContainer.class;
    }

    @Override
    public @NotNull Optional<MenuType<WorktableContainer>> getMenuType() {
        return WORKTABLE_CONTAINER.asOptional();
    }

    @Override
    public @NotNull RecipeType<WorktableRecipe> getRecipeType() {
        return WORKTABLE_CATEGORY;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(
            @NotNull WorktableContainer container,
            WorktableRecipe recipe,
            @NotNull IRecipeSlotsView recipeSlots,
            Player player,
            boolean maxTransfer,
            boolean doTransfer
    ) {
        Inventory inv = player.getInventory();

        // --- Select ingredients ---
        List<ItemStack> coreStacks = pickIngredientStacks(inv, recipe.getCore());
        List<ItemStack> outerStacks = pickIngredientStacks(inv, recipe.getOuter());

        // --- missing items? (build list of JEI slot views to highlight as error) ---
        List<IRecipeSlotView> missingSlots = new ArrayList<>();
        // core slots start at offset 0
        missingSlots.addAll(collectMissingSlotViews(recipeSlots, coreStacks, recipe.getCore(), 0));
        // outer slots follow core slots
        missingSlots.addAll(collectMissingSlotViews(recipeSlots, outerStacks, recipe.getOuter(), recipe.getCore().size()));

        boolean missing = !missingSlots.isEmpty();

        if (!doTransfer) { // preview mode
            if (missing) {
                return transferHelper.createUserErrorForMissingSlots(
                        Component.literal("Missing required ingredients"),
                        missingSlots
                );
            }
            return null; // JEI shows normal highlight
        }

        // --- Do-transfer mode → deliver chosen stacks to packet layer ---
        if (missing) {
            return transferHelper.createUserErrorForMissingSlots(
                    Component.literal("Missing required ingredients"),
                    missingSlots
            );
        }

        Networking.sendToServer(new WorktableTransferPacket(
                container.containerId,
                recipe.pattern_core.width(),
                recipe.pattern_core.height(),
                coreStacks,
                outerStacks
        ));

        return null;
    }

    // ------------------ helper methods ------------------

    private static boolean containsMissing(List<ItemStack> list, List<Ingredient> ingredients) {

        for (int i = 0; i < list.size(); i++) {
            ItemStack s = list.get(i);
            Ingredient ingredient = ingredients.get(i);
            if (s.isEmpty() && !ingredient.isEmpty()) return true;
        }
        return false;
    }

    private static List<ItemStack> pickIngredientStacks(Inventory playerInv, List<Ingredient> ingredients) {
        List<ItemStack> result = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            ItemStack found = findMatchingItem(playerInv, ingredient);
            result.add(found); // may be empty if not found
        }

        return result;
    }

    private static ItemStack findMatchingItem(Inventory inv, Ingredient ing) {
        if (ing.isEmpty()) return ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (ing.test(stack)) {
                return stack.copyWithCount(1);
            }
        }

        return ItemStack.EMPTY;
    }

    private static List<IRecipeSlotView> collectMissingSlotViews(IRecipeSlotsView recipeSlots, List<ItemStack> picked, List<Ingredient> ingredients, int offset) {
        List<IRecipeSlotView> result = new ArrayList<>();
        List<IRecipeSlotView> slots = recipeSlots.getSlotViews();
        for (int i = 0; i < picked.size(); i++) {
            ItemStack s = picked.get(i);
            Ingredient ing = ingredients.get(i);
            if (s.isEmpty() && !ing.isEmpty()) {
                int idx = offset + i;
                if (idx >= 0 && idx < slots.size()) {
                    result.add(slots.get(idx));
                }
            }
        }
        return result;
    }

}
