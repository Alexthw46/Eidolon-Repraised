package alexthw.eidolon_repraised.client.particle;

import alexthw.eidolon_repraised.event.ClientEvents;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class RuneParticleRenderType extends SpriteParticleRenderType {
    public static final RuneParticleRenderType INSTANCE = new RuneParticleRenderType();

    @Override
    public @Nullable BufferBuilder begin(@NotNull Tesselator tesselator, @NotNull TextureManager textureManager) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        ClientEvents.particleMVMatrix = RenderSystem.getModelViewMatrix();
        return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }

}
