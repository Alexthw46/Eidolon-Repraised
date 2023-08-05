package elucent.eidolon.codex;

import com.mojang.blaze3d.systems.RenderSystem;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Page {
    final ResourceLocation bg;

    public Page(ResourceLocation background) {
        this.bg = background;
    }

    public void reset() {
        //
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawItem(CodexGui gui, GuiGraphics guiGraphics, ItemStack stack, int x, int y, int mouseX, int mouseY) {
        guiGraphics.renderItem(stack, x, y);
        var font = Minecraft.getInstance().font;
        guiGraphics.renderItemDecorations(font, stack, x, y, null);
        if (mouseX >= x && mouseY >= y && mouseX <= x + 16 && mouseY <= y + 16) {
            guiGraphics.renderTooltip(font, stack, mouseX, mouseY);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawText(GuiGraphics guiGraphics, String text, int x, int y) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, text, x, y - 1, ColorUtil.packColor(128, 255, 255, 255), false);
        guiGraphics.drawString(font, text, x - 1, y, ColorUtil.packColor(128, 219, 212, 184), false);
        guiGraphics.drawString(font, text, x + 1, y, ColorUtil.packColor(128, 219, 212, 184), false);
        guiGraphics.drawString(font, text, x, y + 1, ColorUtil.packColor(128, 191, 179, 138), false);
        guiGraphics.drawString(font, text, x, y, ColorUtil.packColor(255, 79, 59, 47), false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawWrappingText(GuiGraphics mStack, String text, int x, int y, int w) {
        Font font = Minecraft.getInstance().font;
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        String line = "";
        for (String s : words) {
            if (font.width(line) + font.width(s) > w) {
                lines.add(line);
                line = s + " ";
            } else line += s + " ";
        }
        if (!line.isEmpty()) lines.add(line);
        for (int i = 0; i < lines.size(); i ++) {
            drawText(mStack, lines.get(i), x, y + i * (font.lineHeight + 1));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void fullRender(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, bg);
        renderBackground(gui, mStack, x, y, mouseX, mouseY);
        render(gui, mStack, bg, x, y, mouseX, mouseY);
        renderIngredients(gui, mStack, x, y, mouseX, mouseY);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderBackground(CodexGui gui, @NotNull GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        mStack.blit(bg, x, y, 0, 0, 128, 160);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean click(CodexGui gui, int x, int y, int mouseX, int mouseY) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics mStack, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
    }

    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
    }
}
