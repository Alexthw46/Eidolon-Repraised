package elucent.eidolon.gui;

import elucent.eidolon.Eidolon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class WorktableScreen extends AbstractContainerScreen<WorktableContainer> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID,"textures/gui/worktable.png");

    public WorktableScreen(WorktableContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageHeight = 224;
        this.imageWidth = 192;
    }

    @Override
    public void render(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics matrixStack, int x, int y) {
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics matrixStack, float partialTicks, int x, int y) {
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
