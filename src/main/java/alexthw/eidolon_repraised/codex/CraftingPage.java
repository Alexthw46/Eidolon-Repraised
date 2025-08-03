package alexthw.eidolon_repraised.codex;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

public class CraftingPage extends RecipePage<CraftingRecipe> {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/gui/codex_crafting_page.png" );

    public CraftingPage(ItemStack result) {
        super(BACKGROUND, BuiltInRegistries.ITEM.getKey(result.getItem()), result);
    }

    public CraftingPage(Item result) {
        super(BACKGROUND, BuiltInRegistries.ITEM.getKey(result), result.getDefaultInstance());
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

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int index = i * width + j;
                if (index < inputs.size() && !inputs.get(index).isEmpty())
                    drawItems(mStack, inputs.get(index), x + 36 + j * 20, y + 36 + i * 20, mouseX, mouseY);
            }
        }
        drawItem(mStack, result, x + 56, y + 112, mouseX, mouseY);
    }

    @Override
    public CraftingRecipe getRecipe(ResourceLocation id) {
        Optional<RecipeHolder<?>> recipeHolder = Eidolon.proxy.getWorld().getRecipeManager().byKey(id);
        return recipeHolder.isEmpty() || !(recipeHolder.get().value() instanceof CraftingRecipe recipe) ? null : recipe;
    }
}
