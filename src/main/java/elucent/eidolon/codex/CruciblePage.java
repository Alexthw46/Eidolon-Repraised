package elucent.eidolon.codex;

import elucent.eidolon.Eidolon;
import elucent.eidolon.recipe.CrucibleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class CruciblePage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_crucible_page.png");
    final ItemStack result;
    final CrucibleStep[] steps;

    public static class CrucibleStep {
        final ItemStack[] stacks;
        final int stirs;

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

    public CruciblePage(ItemStack result, CrucibleStep... steps) {
        super(BACKGROUND);
        this.result = result;
        this.steps = steps;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics guiGraphics, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
        int h = steps.length * 20 + 32;
        int yoff = 80 - h / 2;
        for (int i = 0; i < steps.length; i++) {
            int tx = x, ty = y + yoff + i * 20;
            guiGraphics.blit(bg, tx, ty, 128, 0, 128, 20);
            tx += 24;
            for (int j = 0; j < steps[i].stacks.length; j++) {
                if (!steps[i].stacks[j].isEmpty()) {
                    guiGraphics.blit(bg, tx, ty + 1, 176, 32, 16, 17);
                    tx += 17;
                }
            }
            for (int j = 0; j < steps[i].stirs; j++) {
                guiGraphics.blit(bg, tx, ty + 1, 192, 32, 16, 17);
                tx += 17;
            }
        }
        guiGraphics.blit(bg, x, y + yoff + steps.length * 20, 128, 64, 128, 32);

        Font font = Minecraft.getInstance().font;
        for (int i = 0; i < steps.length; i ++) {
            int tx = x, ty = y + yoff + i * 20;
            drawText(gui, guiGraphics, I18n.get("enchantment.level." + (i + 1)) + ".", tx + 7, ty + 17 - font.lineHeight);
            tx += 24;
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        int h = steps.length * 20 + 32;
        int yoff = 80 - h / 2;
        for (int i = 0; i < steps.length; i++) {
            int tx = x, ty = y + yoff + i * 20;
            tx += 24;
            for (int j = 0; j < steps[i].stacks.length; j++) {
                if (!steps[i].stacks[j].isEmpty()) {
                    drawItem(gui, mStack, steps[i].stacks[j], tx, ty + 1, mouseX, mouseY);
                    tx += 17;
                }
            }
        }
        drawItem(gui, mStack, result, x + 56, y + yoff + steps.length * 20 + 11, mouseX, mouseY);
    }

    public CruciblePage linkRecipe(String modid, String recipe) {
        return linkRecipe(new ResourceLocation(modid, recipe));
    }

    public CruciblePage linkRecipe(ResourceLocation recipe) {
        CrucibleRegistry.linkPage(recipe, this);
        return this;
    }
}
