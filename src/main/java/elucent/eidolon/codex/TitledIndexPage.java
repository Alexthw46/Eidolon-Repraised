package elucent.eidolon.codex;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TitledIndexPage extends IndexPage {
    final String title;

    public TitledIndexPage(String textKey, IndexEntry... pages) {
        super(pages);
        this.title = textKey + ".title";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean click(CodexGui gui, int x, int y, int mouseX, int mouseY) {
        return super.click(gui, x, y + 16, mouseX, mouseY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, @NotNull GuiGraphics guiGraphics, ResourceLocation bg, int x, int y, int mouseX, int mouseY) {
        guiGraphics.blit(bg, x, y, 128, 64, 128, 32);
        String title = I18n.get(this.title);
        int titleWidth = Minecraft.getInstance().font.width(title);
        drawText(guiGraphics, title, x + 64 - titleWidth / 2, y + 15 - Minecraft.getInstance().font.lineHeight);

        RenderSystem.setShaderTexture(0, BACKGROUND);
        super.render(gui, guiGraphics, bg, x, y + 16, mouseX, mouseY);
    }
}
