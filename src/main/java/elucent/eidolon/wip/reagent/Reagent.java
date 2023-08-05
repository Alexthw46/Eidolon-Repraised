package elucent.eidolon.wip.reagent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Reagent {
    final ResourceLocation name;
    final ResourceLocation texture;
    final boolean gas;

    public Reagent(ResourceLocation name, ResourceLocation texture, boolean isGas) {
        this.name = name;
        this.texture = texture;
        this.gas = isGas;
    }

    public ResourceLocation getRegistryName() {
        return name;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @OnlyIn(Dist.CLIENT)
    public TextureAtlasSprite getSprite() {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
    }

    public boolean isGas() {
        return gas;
    }

    public abstract void worldEffect(Level world, BlockPos pos, int amount);
}
