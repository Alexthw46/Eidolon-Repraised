package elucent.eidolon.codex;

import com.mojang.blaze3d.vertex.PoseStack;

import elucent.eidolon.Eidolon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import elucent.eidolon.recipe.WorktableRecipe;
import elucent.eidolon.recipe.WorktableRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class WorktablePage extends RecipePage<WorktableRecipe> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_worktable_page.png");

    @Deprecated
    public WorktablePage(ItemStack result, ItemStack... inputs) {
        super(BACKGROUND, null, result);
        cachedRecipe = new WorktableRecipe(inputs, result);
    }

    public WorktablePage(ItemStack result, ResourceLocation id) {
        super(BACKGROUND, id, result);
    }

    public WorktablePage(Item result) {
        this(result.getDefaultInstance(), ForgeRegistries.ITEMS.getKey(result));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, PoseStack mStack, int x, int y, int mouseX, int mouseY) {

        if (cachedRecipe == null) return;
        Ingredient[] core = cachedRecipe.getCore();
        Ingredient[] outer = cachedRecipe.getOuter();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                if (index < core.length && !core[index].isEmpty())
                    drawItems(mStack, core[index], x + 39 + j * 17, y + 33 + i * 17, mouseX, mouseY);
            }
        }

        drawItems(gui,mStack, outer[0], x + 56, y + 11, mouseX, mouseY);
        drawItems(gui,mStack, outer[1], x + 95, y + 50, mouseX, mouseY);
        drawItems(gui,mStack, outer[2], x + 56, y + 89, mouseX, mouseY);
        drawItems(gui,mStack, outer[3], x + 17, y + 50, mouseX, mouseY);
        drawItem(gui,mStack, result, x + 56, y + 129, mouseX, mouseY);
    }

    @Override
    public WorktableRecipe getRecipe(ResourceLocation id) {
        return WorktableRegistry.find(id);
    }
}
