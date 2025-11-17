package alexthw.eidolon_repraised.codex;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.altar.AltarEntry;
import alexthw.eidolon_repraised.registries.AltarEntries;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ListPage extends Page {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/gui/codex_index_page.png" );
    final ListEntry[] entries;

    public static class ListEntry {
        final String key;
        final ItemStack icon;
        private final AltarEntry entry;

        public ListEntry(String key, ItemStack icon) {
            this.key = key;
            this.icon = icon;
            this.entry = AltarEntries.find(icon.getItem());
        }

        public ListEntry(String key, ItemStack icon, Block entry) {
            this.key = key;
            this.icon = icon;
            this.entry = AltarEntries.find(entry);
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
            ItemStack icon = entries[i].icon;
            AltarEntry entry = entries[i].entry;
            drawItem(mStack, icon, x + 2, y + 8 + i * 20, mouseX, mouseY);
            String text = "";
            try {
                if (entry.power() > 0) {
                    text += (int) entry.power() + " " + I18n.get("eidolon_repraised.codex.altar_power");
                }
                if (entry.capacity() > 0) {
                    if (!text.isEmpty()) {
                        text += ", ";
                    }
                    text += (int) entry.capacity() + " " + I18n.get("eidolon_repraised.codex.altar_capacity");
                }
            } catch (Exception e) {
                text = "Invalid Entry. Likely mod conflict.";
            }
            drawText(mStack, text, x + 24, y + 20 + i * 20 - Minecraft.getInstance().font.lineHeight);

        }

    }
}
