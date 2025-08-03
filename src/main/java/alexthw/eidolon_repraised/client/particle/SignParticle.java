package alexthw.eidolon_repraised.client.particle;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.client.ClientConfig;
import alexthw.eidolon_repraised.event.ClientEvents;
import alexthw.eidolon_repraised.util.RenderUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SignParticle extends TextureSheetParticle {
    final Sign sign;
    public SignParticle(ClientLevel world, Sign sign, double x, double y, double z, double vx, double vy, double vz) {
        super(world, x, y, z, vx, vy, vz);
        this.setPos(x, y, z);
        this.sign = sign;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.setLifetime(20);
        this.gravity = -0.05f;
        setColor(sign.getRed(), sign.getGreen(), sign.getBlue());
        updateTraits();
    }

    protected float getCoeff() {
        return (float)this.age / this.lifetime;
    }

    protected void updateTraits() {
        float coeff = getCoeff();
        quadSize = Mth.lerp(coeff, 0.25f, 0.125f);
        setAlpha(Mth.lerp(coeff * coeff, 0.75f, 0));
    }

    @Override
    public void tick() {
        updateTraits();
        super.tick();
        xd *= 0.98;
        yd *= 0.98;
        zd *= 0.98;
    }

    @Override
    public void render(@NotNull VertexConsumer b, @NotNull Camera info, float pticks) {
        super.render(ClientConfig.BETTER_LAYERING.get() ? ClientEvents.getDelayedRender().getBuffer(RenderUtil.GLOWING_BLOCK_PARTICLE) : b, info, pticks);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return SignParticleRenderType.INSTANCE;
    }
}
