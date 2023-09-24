package elucent.eidolon.codex;

import elucent.eidolon.Eidolon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class CraftingPage extends RecipePage<CraftingRecipe> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_crafting_page.png");

    public CraftingPage(ItemStack result) {
        super(BACKGROUND, ForgeRegistries.ITEMS.getKey(result.getItem()), result);
    }

    public CraftingPage(Item result) {
        super(BACKGROUND, ForgeRegistries.ITEMS.getKey(result), result.getDefaultInstance());
    }

    public CraftingPage(ItemStack result, ResourceLocation recipeId) {
        super(BACKGROUND, recipeId, result);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        if (cachedRecipe == null) return;

        NonNullList<Ingredient> inputs = cachedRecipe.getIngredients();

        int width = 3, height = 3;

        if (cachedRecipe instanceof ShapedRecipe shaped) {
            width = shaped.getWidth();
            height = shaped.getHeight();
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int index = i * width + j;
                if (index < inputs.size() && !inputs.get(index).isEmpty())
                    drawItems(mStack, inputs.get(index), x + 36 + j * 20, y + 36 + i * 20, mouseX, mouseY);
            }
        }
        drawItem(mStack, result, x + 56, y + 112, mouseX, mouseY);
    }

    @Override
    public CraftingRecipe getRecipe(ResourceLocation id) {
        return (CraftingRecipe) Eidolon.proxy.getWorld().getRecipeManager().byKey(id).orElse(null);
    }
}
