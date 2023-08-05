package elucent.eidolon.codex;

import com.mojang.blaze3d.systems.RenderSystem;
import elucent.eidolon.Eidolon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ListPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_index_page.png");
    final ListEntry[] entries;

    public static class ListEntry {
        final String key;
        final ItemStack icon;

        public ListEntry(String key, ItemStack icon) {
            this.key = key;
            this.icon = icon;
        }
    }

    final String key;

    public ListPage(String key, ListEntry... pages) {
        super(BACKGROUND);
        this.key = key;
        this.entries = pages;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics mStack, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        for (int i = 0; i < entries.length; i++) {
            mStack.blit(BACKGROUND, x + 1, y + 7 + i * 20, 128, 0, 122, 18);
        }

        for (int i = 0; i < entries.length; i++) {
            drawItem(gui, mStack, entries[i].icon, x + 2, y + 8 + i * 20, mouseX, mouseY);
            drawText(mStack, I18n.get(key + "." + entries[i].key), x + 24, y + 20 + i * 20 - Minecraft.getInstance().font.lineHeight);
        }
    }
}
