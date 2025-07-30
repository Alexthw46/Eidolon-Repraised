package elucent.eidolon.codex;

import elucent.eidolon.Eidolon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class SmeltingPage extends RecipePage<AbstractCookingRecipe> {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/gui/codex_smelting_page.png" );
    final ItemStack input;

    public SmeltingPage(ItemStack result, ItemStack defaultinput, ResourceLocation recipeId) {
        super(BACKGROUND, recipeId, result);
        this.input = defaultinput;
    }

    public SmeltingPage(ItemStack result, ItemStack defaultinput) {
        super(BACKGROUND, BuiltInRegistries.ITEM.getKey(result.getItem()), result);
        this.input = defaultinput;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        drawItems(mStack, cachedRecipe == null ? Ingredient.of(input) : cachedRecipe.getIngredients().get(0), x + 56, y + 34, mouseX, mouseY);
        drawItem(mStack, result, x + 56, y + 107, mouseX, mouseY);
    }

    @Override
    public @Nullable AbstractCookingRecipe getRecipe(ResourceLocation id) {
        RecipeHolder<?> recipeHolder = Eidolon.proxy.getWorld().getRecipeManager().byKey(id).orElse(null);
        if (recipeHolder == null || !(recipeHolder.value() instanceof AbstractCookingRecipe recipe)) {
            return null;
        }
        return recipe;
    }
}
