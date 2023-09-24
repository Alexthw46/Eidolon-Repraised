package elucent.eidolon.codex;

import elucent.eidolon.Eidolon;
import elucent.eidolon.recipe.CrucibleRecipe;
import elucent.eidolon.recipe.CrucibleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class CruciblePage extends RecipePage<CrucibleRecipe> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_crucible_page.png");
    public CrucibleStep[] steps;

    @Override
    public CrucibleRecipe getRecipe(ResourceLocation id) {
        return CrucibleRegistry.find(id);
    }

    public static class CrucibleStep {
        public final ItemStack[] stacks;
        public final int stirs;

        public CrucibleStep(int stirs) {
            this(stirs, ItemStack.EMPTY);
        }

        public CrucibleStep(ItemStack... stacks) {
            this(0, stacks);
        }

        public CrucibleStep(int stirs, ItemStack... stacks) {
            this.stacks = stacks;
            this.stirs = stirs;
        }
    }

    @Deprecated
    public CruciblePage(ItemStack result, CrucibleStep... steps) {
        super(BACKGROUND, null, result);
        this.steps = steps;
        this.cachedRecipe = new CrucibleRecipe(steps, result);
    }

    public CruciblePage(ItemStack result, ResourceLocation id) {
        super(BACKGROUND, id, result);
    }

    public CruciblePage(ItemStack result) {
        this(result, ForgeRegistries.ITEMS.getKey(result.getItem()));
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics guiGraphics, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
        if (cachedRecipe == null) return;
        var steps = cachedRecipe.getSteps();
        int h = steps.size() * 20 + 32;
        int yoff = 80 - h / 2;
        for (int i = 0; i < steps.size(); i++) {
            int tx = x, ty = y + yoff + i * 20;
            guiGraphics.blit(bg, tx, ty, 128, 0, 128, 20);
            tx += 24;
            for (int j = 0; j < steps.get(i).matches.size(); j++) {
                if (!steps.get(i).matches.get(j).isEmpty()) {
                    guiGraphics.blit(bg, tx, ty + 1, 176, 32, 16, 17);
                    tx += 17;
                }
            }
            for (int j = 0; j < steps.get(i).stirs; j++) {
                guiGraphics.blit(bg, tx, ty + 1, 192, 32, 16, 17);
                tx += 17;
            }
        }
        guiGraphics.blit(bg, x, y + yoff + steps.size() * 20, 128, 64, 128, 32);

        Font font = Minecraft.getInstance().font;
        for (int i = 0; i < steps.size(); i++) {
            int tx = x, ty = y + yoff + i * 20;
            drawText(guiGraphics, I18n.get("enchantment.level." + (i + 1)) + ".", tx + 7, ty + 17 - font.lineHeight);
            tx += 24;
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        if (cachedRecipe == null) return;
        var steps = cachedRecipe.getSteps();

        int h = steps.size() * 20 + 32;
        int yoff = 80 - h / 2;
        for (int i = 0; i < steps.size(); i++) {
            int tx = x, ty = y + yoff + i * 20;
            tx += 24;
            for (int j = 0; j < steps.get(i).matches.size(); j++) {
                if (!steps.get(i).matches.get(j).isEmpty()) {
                    drawItems(mStack, steps.get(i).matches.get(j), tx, ty + 1, mouseX, mouseY);
                    tx += 17;
                }
            }
        }
        drawItem(mStack, result, x + 56, y + yoff + steps.size() * 20 + 11, mouseX, mouseY);
    }

    public CruciblePage linkRecipe(String modid, String recipe) {
        return linkRecipe(new ResourceLocation(modid, recipe));
    }

    public CruciblePage linkRecipe(ResourceLocation recipe) {
        CrucibleRegistry.linkPage(recipe, this);
        return this;
    }
}
