package elucent.eidolon.codex;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public abstract class RecipePage<T extends Recipe<?>> extends Page {
    public RecipePage(ResourceLocation background, ResourceLocation recipeId, ItemStack result) {
        super(background);
        this.recipeId = recipeId;
        this.result = result;
    }

    T cachedRecipe;

    ResourceLocation recipeId;

    final ItemStack result;

    @Override
    public void fullRender(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        if (recipeId != null && cachedRecipe == null) {
            cachedRecipe = getRecipe(recipeId);
        }
        super.fullRender(gui, mStack, x, y, mouseX, mouseY);
    }

    public abstract T getRecipe(ResourceLocation id);

}
