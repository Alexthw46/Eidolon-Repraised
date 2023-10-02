package elucent.eidolon.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import elucent.eidolon.client.ClientConfig;
import elucent.eidolon.event.ClientEvents;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.NotNull;

public class LineWispParticle extends GenericParticle {
    final double ix;
    final double iy;
    final double iz;
    final double tx;
    final double ty;
    final double tz;

    public LineWispParticle(ClientLevel world, GenericParticleData data, double x, double y, double z, double vx, double vy, double vz) {
        super(world, data, x, y, z, vx, vy, vz);
        this.ix = x;
        this.iy = y;
        this.iz = z;
        this.tx = xd;
        this.ty = yd;
        this.tz = zd;
        xd = yd = zd = 0;
    }

    @Override
    public void tick() {
        super.tick();
        xo = x;
        yo = y;
        zo = z;
        float coeff = (float)age / lifetime;
        coeff *= coeff;
        x = Mth.lerp(coeff, ix, tx);
        y = Mth.lerp(1 - (1 - coeff) * (1 - coeff), iy, ty);
        z = Mth.lerp(coeff, iz, tz);
        SpawnEggItem i;
    }

    @Override
    protected int getLightColor(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public void render(@NotNull VertexConsumer b, @NotNull Camera info, float pticks) {
        super.render(ClientConfig.BETTER_LAYERING.get() ? ClientEvents.getDelayedRender().getBuffer(RenderUtil.GLOWING_PARTICLE) : b, info, pticks);
    }
}
