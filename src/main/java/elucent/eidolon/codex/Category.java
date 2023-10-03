package elucent.eidolon.codex;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import static elucent.eidolon.codex.Page.colorBlit;

public class Category {
    final ItemStack icon;
    final String key;
    final Index chapter;
    final int color;
    float hoveramount = 0;

    public Category(String name, ItemStack icon, int color, Index chapter) {
        this.icon = icon;
        this.key = name;
        this.chapter = chapter;
        this.color = color;
    }

    protected void reset() {
        hoveramount = 0;
    }

    public boolean click(CodexGui gui, int x, int y, boolean right, int mouseX, int mouseY) {
        int w = 36;
        if (!right) x -= 36;
        w += (int) (hoveramount * 12);
        if (!right) x -= (int) (hoveramount * 12);

        boolean hover = mouseX >= x && mouseY >= y && mouseX <= x + w && mouseY <= y + 19;
        if (hover) {
            gui.currentPage = 0;
            gui.lastChapter = gui.currentChapter;
            gui.currentChapter = chapter;
            gui.resetPages();
            return true;
        }
        return false;
    }

    public void draw(CodexGui gui, PoseStack mStack, int x, int y, boolean right, int mouseX, int mouseY) {
        int w = 36;
        if (!right) x -= 36;
        w += (int) (hoveramount * 12);
        if (!right) x -= (int) (hoveramount * 12);

        boolean hover = mouseX >= x && mouseY >= y && mouseX <= x + w && mouseY <= y + 19;
        if (hover && hoveramount < 1) hoveramount += Minecraft.getInstance().getFrameTime() / 4;
        else if (!hover && hoveramount > 0) hoveramount -= Minecraft.getInstance().getFrameTime() / 4;
        hoveramount = Mth.clamp(hoveramount, 0, 1);

        if (right) {
            x -= 12;
            x += (int) (hoveramount * 12);
        }

        RenderSystem.setShaderTexture(0, CodexGui.CODEX_BACKGROUND);
        colorBlit(mStack, x, y, 208, right ? 208 : 227, 48, 19, 512, 512, color);
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, x + (right ? 23 : 9), y + 1);
    }

    public void drawTooltip(CodexGui gui, PoseStack mStack, int x, int y, boolean right, int mouseX, int mouseY) {
        int w = 36;
        if (!right) x -= 36;
        w += (int) (hoveramount * 12);
        if (!right) x -= (int) (hoveramount * 12);

        boolean hover = mouseX >= x && mouseY >= y && mouseX <= x + w && mouseY <= y + 19;
        if (hover) gui.renderTooltip(mStack, Component.translatable("eidolon.codex.category." + key), mouseX, mouseY);
    }
}
