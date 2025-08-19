package alexthw.eidolon_repraised.codex;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public abstract class RecipePage<T extends Recipe<?>> extends Page {
    int ingredientRotationTimer;

    public RecipePage(ResourceLocation background, ResourceLocation recipeId, ItemStack result) {
        super(background);
        this.recipeId = recipeId;
        this.result = result;
    }

    T cachedRecipe;

    final ResourceLocation recipeId;

    final ItemStack result;

    @Override
    public void fullRender(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        ingredientRotationTimer++;
        if (recipeId != null && cachedRecipe == null) {
            cachedRecipe = getRecipe(recipeId);
            if (cachedRecipe == null && !result.isEmpty()) {
                mStack.drawScrollingString(gui.getMinecraft().font, Component.literal("No matching recipe found for " + recipeId), x + 10, x + 120, y + 10, 0x000000);
            }
        }
        super.fullRender(gui, mStack, x, y, mouseX, mouseY);
    }

    @Nullable
    public abstract T getRecipe(ResourceLocation id);

}
