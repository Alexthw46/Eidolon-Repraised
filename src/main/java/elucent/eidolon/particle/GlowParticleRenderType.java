package elucent.eidolon.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import elucent.eidolon.ClientRegistry;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class GlowParticleRenderType implements ParticleRenderType {
    public static final GlowParticleRenderType INSTANCE = new GlowParticleRenderType();

    private static void beginRenderCommon(BufferBuilder bufferBuilder, TextureManager textureManager) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        ClientEvents.particleMVMatrix = RenderSystem.getModelViewMatrix();
        bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }

    private static void endRenderCommon() {
        Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES).restoreLastBlurMipmap();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    @Override
    public void begin(@NotNull BufferBuilder b, @NotNull TextureManager tex) {
        beginRenderCommon(b, tex);
    }

    @Override
    public void end(Tesselator t) {
        t.end();
        ClientEvents.getDelayedRender().getBuffer(RenderUtil.GLOWING_PARTICLE);
        RenderSystem.enableDepthTest();
        endRenderCommon();
    }
}
