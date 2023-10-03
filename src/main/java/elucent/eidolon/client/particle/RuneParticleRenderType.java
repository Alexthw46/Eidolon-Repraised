package elucent.eidolon.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import elucent.eidolon.event.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class RuneParticleRenderType extends SpriteParticleRenderType {
    public static final RuneParticleRenderType INSTANCE = new RuneParticleRenderType();

    private static void beginRenderCommon(BufferBuilder bufferBuilder, TextureManager textureManager) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        ClientEvents.particleMVMatrix = RenderSystem.getModelViewMatrix();
        bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }

    private static void endRenderCommon() {
        Minecraft.getInstance().textureManager.getTexture(InventoryMenu.BLOCK_ATLAS).restoreLastBlurMipmap();
        RenderSystem.depthMask(true);
    }

    @Override
    public void begin(@NotNull BufferBuilder b, @NotNull TextureManager tex) {
        beginRenderCommon(b, tex);
    }

    @Override
    public void end(Tesselator t) {
        t.end();
        RenderSystem.enableDepthTest();
        endRenderCommon();
    }
}
