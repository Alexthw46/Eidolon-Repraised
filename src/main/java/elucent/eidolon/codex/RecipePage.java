package elucent.eidolon.codex;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public abstract class RecipePage<T extends Recipe<?>> extends Page {
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
        if (recipeId != null && cachedRecipe == null) {
            cachedRecipe = getRecipe(recipeId);
            if (cachedRecipe == null && !result.isEmpty()) {
                mStack.drawString(gui.getMinecraft().font, "No matching recipe found for " + recipeId, x + 10, y + 10, 0x000000);
            }
        }
        super.fullRender(gui, mStack, x, y, mouseX, mouseY);
    }

    @Nullable
    public abstract T getRecipe(ResourceLocation id);

}
