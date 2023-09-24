package elucent.eidolon.codex;

import elucent.eidolon.Eidolon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class SmeltingPage extends RecipePage<AbstractCookingRecipe> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_smelting_page.png");
    final ItemStack input;

    public SmeltingPage(ItemStack result, ItemStack defaultinput, ResourceLocation recipeId) {
        super(BACKGROUND, recipeId, result);
        this.input = defaultinput;
    }

    public SmeltingPage(ItemStack result, ItemStack defaultinput) {
        super(BACKGROUND, ForgeRegistries.ITEMS.getKey(result.getItem()), result);
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
        return (AbstractCookingRecipe) Eidolon.proxy.getWorld().getRecipeManager().byKey(id).orElse(null);
    }
}
