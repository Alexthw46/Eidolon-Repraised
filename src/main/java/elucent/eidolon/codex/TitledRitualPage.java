package elucent.eidolon.codex;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import elucent.eidolon.api.ritual.Ritual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static elucent.eidolon.util.RegistryUtil.getRegistryName;

public class TitledRitualPage extends RitualPage {
    final String title;

    public TitledRitualPage(String textKey, ResourceLocation recipeName) {
        super(recipeName);
        this.title = textKey + ".title";
    }

    public TitledRitualPage(String textKey, ItemStack result) {
        super(getRegistryName(result.getItem()), result);
        this.title = textKey + ".title";
    }

    public TitledRitualPage(String textKey, Ritual ritual) {
        super(ritual);
        this.title = textKey + ".title";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, PoseStack mStack, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        gui.blit(mStack, x, y, 128, 64, 128, 24);
        String title = I18n.get(this.title);
        int titleWidth = Minecraft.getInstance().font.width(title);
        drawText(gui, mStack, title, x + 64 - titleWidth / 2, y + 15 - Minecraft.getInstance().font.lineHeight);

        RenderSystem.setShaderTexture(0, BACKGROUND);
        super.render(gui, mStack, x, y, mouseX, mouseY);
    }
}
