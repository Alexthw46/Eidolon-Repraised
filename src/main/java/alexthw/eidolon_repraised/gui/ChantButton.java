package alexthw.eidolon_repraised.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static alexthw.eidolon_repraised.codex.CodexGui.blit;

public class ChantButton extends Button {
    protected ChantButton(int pX, int pY, int pWidth, int pHeight, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight,  Component.empty(), pOnPress, Button.DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        blit(pGuiGraphics, getX(), getY() - 4, 336, 65 + (isHovered ? 224 : 208), 17, 16, 512, 512);
        if (isHovered)
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("eidolon_repraised.codex.chant_hover"), pMouseX, pMouseY);
    }
}
