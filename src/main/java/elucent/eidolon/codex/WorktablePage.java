package elucent.eidolon.codex;

import elucent.eidolon.Eidolon;
import elucent.eidolon.recipe.WorktableRecipe;
import elucent.eidolon.recipe.WorktableRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class WorktablePage extends RecipePage<WorktableRecipe> {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/gui/codex_worktable_page.png" );

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
        Ingredient[] core = cachedRecipe.getCoreA();
        Ingredient[] outer = cachedRecipe.getOuterA();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                if (index < core.length && !core[index].isEmpty())
                    drawItems(mStack, core[index], x + 39 + j * 17, y + 33 + i * 17, mouseX, mouseY);
            }
        }
        drawItems(mStack, outer[0], x + 56, y + 11, mouseX, mouseY);
        drawItems(mStack, outer[1], x + 95, y + 50, mouseX, mouseY);
        drawItems(mStack, outer[2], x + 56, y + 89, mouseX, mouseY);
        drawItems(mStack, outer[3], x + 17, y + 50, mouseX, mouseY);
        drawItem(mStack, result, x + 56, y + 129, mouseX, mouseY);
    }

    @Override
    public WorktableRecipe getRecipe(ResourceLocation id) {
        return WorktableRegistry.find(id);
    }
}
