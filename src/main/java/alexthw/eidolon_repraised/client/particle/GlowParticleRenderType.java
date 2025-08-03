package alexthw.eidolon_repraised.client.particle;

import alexthw.eidolon_repraised.client.ClientRegistry;
import alexthw.eidolon_repraised.event.ClientEvents;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class GlowParticleRenderType implements ParticleRenderType {
    public static final GlowParticleRenderType INSTANCE = new GlowParticleRenderType();

    @Override
    public @Nullable BufferBuilder begin(Tesselator tesselator, @NotNull TextureManager textureManager) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShader(ClientRegistry::getGlowingSpriteShader);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        ClientEvents.particleMVMatrix = RenderSystem.getModelViewMatrix();
        return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }
}
