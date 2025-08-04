package alexthw.eidolon_repraised.codex;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.recipe.WorktableRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class WorktablePage extends RecipePage<WorktableRecipe> {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "textures/gui/codex_worktable_page.png");

    public WorktablePage(ItemStack result) {
        super(BACKGROUND, BuiltInRegistries.ITEM.getKey(result.getItem()), result);
    }

    public WorktablePage(ItemStack result, ResourceLocation id) {
        super(BACKGROUND, id, result);
    }

    public WorktablePage(Item result) {
        this(result.getDefaultInstance(), BuiltInRegistries.ITEM.getKey(result));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        if (cachedRecipe == null) return;
        List<Ingredient> core = cachedRecipe.getCore();
        List<Ingredient> outer = cachedRecipe.getOuter();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                if (index < core.size() && !core.get(index).isEmpty())
                    drawItems(mStack, core.get(index), x + 39 + j * 17, y + 33 + i * 17, mouseX, mouseY);
            }
        }

        drawItems(mStack, outer.get(0), x + 56, y + 11, mouseX, mouseY);
        drawItems(mStack, outer.get(1), x + 95, y + 50, mouseX, mouseY);
        drawItems(mStack, outer.get(2), x + 56, y + 89, mouseX, mouseY);
        drawItems(mStack, outer.get(3), x + 17, y + 50, mouseX, mouseY);
        drawItem(mStack, result, x + 56, y + 129, mouseX, mouseY);
    }

    @Override
    public WorktableRecipe getRecipe(ResourceLocation id) {
        RecipeHolder<?> recipeHolder = Eidolon.proxy.getWorld().getRecipeManager().byKey(id).orElse(null);
        if (recipeHolder == null || !(recipeHolder.value() instanceof WorktableRecipe recipe)) {
            return null;
        }
        return recipe;
    }
}
