package elucent.eidolon.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import elucent.eidolon.client.ClientConfig;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SteamParticle extends GenericParticle {
    public SteamParticle(ClientLevel world, GenericParticleData data, double x, double y, double z, double vx, double vy, double vz) {
        super(world, data, x, y, z, vx, vy, vz);
    }

    @Override
    protected float getCoeff() {
        return 1.0f - Mth.sin((float)Math.PI * (float)this.age / this.lifetime);
    }

    @Override
    public void tick() {
        super.tick();
        yd *= 0.99;
    }

    @Override
    public void render(@NotNull VertexConsumer b, @NotNull Camera info, float pticks) {
        super.render(ClientConfig.BETTER_LAYERING.get() ? ClientEvents.getDelayedRender().getBuffer(RenderUtil.GLOWING_PARTICLE) : b, info, pticks);
    }
}
