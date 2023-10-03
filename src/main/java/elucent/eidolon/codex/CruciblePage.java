package elucent.eidolon.codex;

import com.mojang.blaze3d.vertex.PoseStack;
import elucent.eidolon.Eidolon;
import elucent.eidolon.recipe.CrucibleRecipe;
import elucent.eidolon.recipe.CrucibleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class CruciblePage extends RecipePage<CrucibleRecipe> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_crucible_page.png");

    @Override
    public CrucibleRecipe getRecipe(ResourceLocation id) {
        return CrucibleRegistry.find(id);
    }

    public CruciblePage(ItemStack result, ResourceLocation id) {
        super(BACKGROUND, id, result);
    }

    public CruciblePage(ItemStack result) {
        this(result, ForgeRegistries.ITEMS.getKey(result.getItem()));
    }

    public CruciblePage(Item result) {
        this(result.getDefaultInstance(), ForgeRegistries.ITEMS.getKey(result));
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, PoseStack mStack, int x, int y, int mouseX, int mouseY) {
        if (cachedRecipe == null) return;
        var steps = cachedRecipe.getSteps();
        int h = steps.size() * 20 + 32;
        int yoff = 80 - h / 2;
        for (int i = 0; i < steps.size(); i++) {
            int tx = x, ty = y + yoff + i * 20;
            gui.blit(mStack, tx, ty, 128, 0, 128, 20);
            tx += 24;

            for (int j = 0; j < steps.get(i).matches.size(); j++) {
                if (!steps.get(i).matches.get(j).isEmpty()) {
                    gui.blit(mStack, tx, ty + 1, 176, 32, 16, 17);
                    tx += 17;
                }
            }
            for (int j = 0; j < steps.get(i).stirs; j++) {
                gui.blit(mStack, tx, ty + 1, 192, 32, 16, 17);
                tx += 17;
            }
        }
        gui.blit(mStack, x, y + yoff + steps.size() * 20, 128, 64, 128, 32);

        Font font = Minecraft.getInstance().font;
        for (int i = 0; i < steps.size(); i++) {
            int tx = x, ty = y + yoff + i * 20;
            drawText(gui, mStack, I18n.get("enchantment.level." + (i + 1)) + ".", tx + 7, ty + 17 - font.lineHeight);
            tx += 24;

        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, PoseStack mStack, int x, int y, int mouseX, int mouseY) {

        if (cachedRecipe == null) return;
        var steps = cachedRecipe.getSteps();

        int h = steps.size() * 20 + 32;
        int yoff = 80 - h / 2;
        for (int i = 0; i < steps.size(); i++) {
            int tx = x, ty = y + yoff + i * 20;
            tx += 24;
            for (int j = 0; j < steps.get(i).matches.size(); j++) {
                if (!steps.get(i).matches.get(j).isEmpty()) {
                    drawItems(gui, mStack, steps.get(i).matches.get(j), tx, ty + 1, mouseX, mouseY);
                    tx += 17;
                }
            }
        }

        drawItem(gui, mStack, result,x + 56, y + yoff + steps.size() * 20 + 11, mouseX, mouseY);

    }

}
