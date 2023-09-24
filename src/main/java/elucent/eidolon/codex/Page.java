package elucent.eidolon.codex;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import elucent.eidolon.Eidolon;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

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
    public static void drawItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y, int mouseX, int mouseY) {
        guiGraphics.renderItem(stack, x, y);
        var font = Minecraft.getInstance().font;
        guiGraphics.renderItemDecorations(font, stack, x, y, null);
        if (mouseX >= x && mouseY >= y && mouseX <= x + 16 && mouseY <= y + 16) {
            guiGraphics.renderTooltip(font, stack, mouseX, mouseY);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawItems(GuiGraphics guiGraphics, Ingredient ingredient, int x, int y, int mouseX, int mouseY) {
        if (ingredient.isEmpty()) return;
        ItemStack[] items = ingredient.getItems();
        ItemStack stack = items[((int) Eidolon.proxy.getWorld().getGameTime() / 20) % items.length];
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
        for (int i = 0; i < lines.size(); i++) {
            drawText(mStack, lines.get(i), x, y + i * (font.lineHeight + 1));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void fullRender(CodexGui gui, GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, bg);
        renderBackground(mStack, x, y, mouseX, mouseY);
        render(gui, mStack, bg, x, y, mouseX, mouseY);
        renderIngredients(gui, mStack, x, y, mouseX, mouseY);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderBackground(@NotNull GuiGraphics mStack, int x, int y, int mouseX, int mouseY) {
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

    @OnlyIn(Dist.CLIENT)
    static void colorBlit(PoseStack mStack, int x, int y, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight, int color) {
        Matrix4f matrix = mStack.last().pose();
        int maxX = x + width, maxY = y + height;
        float minU = (float) uOffset / textureWidth, minV = (float) vOffset / textureHeight;
        float maxU = minU + (float) width / textureWidth, maxV = minV + (float) height / textureHeight;
        int r = ColorUtil.getRed(color),
                g = ColorUtil.getGreen(color),
                b = ColorUtil.getBlue(color);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix, (float) x, (float) maxY, 0).uv(minU, maxV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float) maxX, (float) maxY, 0).uv(maxU, maxV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float) maxX, (float) y, 0).uv(maxU, minV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float) x, (float) y, 0).uv(minU, minV).color(r, g, b, 255).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
