package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MagicBurstEffectPacket extends AbstractPacket {
    public static final Type<MagicBurstEffectPacket> TYPE = new Type<>(Eidolon.prefix("magic_burst_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MagicBurstEffectPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            pkt -> pkt.x,
            ByteBufCodecs.DOUBLE,
            pkt -> pkt.y,
            ByteBufCodecs.DOUBLE,
            pkt -> pkt.z,
            ByteBufCodecs.INT,
            pkt -> pkt.c1,
            ByteBufCodecs.INT,
            pkt -> pkt.c2,
            MagicBurstEffectPacket::new
    );

    final double x;
    final double y;
    final double z;
    final int c1;
    final int c2;

    public MagicBurstEffectPacket(BlockPos pos, int color1, int color2) {
        this(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, color1, color2);
    }

    public MagicBurstEffectPacket(double x, double y, double z, int color1, int color2) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.c1 = color1;
        this.c2 = color2;
    }

    public static void encode(MagicBurstEffectPacket object, FriendlyByteBuf buffer) {
        buffer.writeDouble(object.x).writeDouble(object.y).writeDouble(object.z);
        buffer.writeInt(object.c1).writeInt(object.c2);
    }

    public static MagicBurstEffectPacket decode(FriendlyByteBuf buffer) {
        return new MagicBurstEffectPacket(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();
        double x = this.x, y = this.y, z = this.z;

        float r1 = ColorUtil.getRed(this.c1) / 255.0f, g1 = ColorUtil.getGreen(this.c1) / 255.0f, b1 = ColorUtil.getBlue(this.c1) / 255.0f;
        float r2 = ColorUtil.getRed(this.c2) / 255.0f, g2 = ColorUtil.getGreen(this.c2) / 255.0f, b2 = ColorUtil.getBlue(this.c2) / 255.0f;
        Particles.create(EidolonParticles.WISP_PARTICLE.get())
                .setAlpha(0.5f, 0).setScale(0.25f, 0).setLifetime(20)
                .randomOffset(0.125, 0.125).randomVelocity(0.0625f, 0.0625f)
                .setColor(r1, g1, b1, r2, g2, b2)
                .repeat(world, x, y, z, 12);
        Particles.create(EidolonParticles.SPARKLE_PARTICLE.get())
                .setAlpha(1, 0).setScale(0.0625f, 0).setLifetime(80)
                .randomOffset(0.0625, 0).randomVelocity(0.125f, 0.125f)
                .addVelocity(0, 0.25f, 0)
                .setColor(r1, g1, b1, r2, g2, b2)
                .enableGravity().setSpin(0.4f)
                .repeat(world, x, y, z, world.random.nextInt(4) + 3);
        Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                .setAlpha(0.25f, 0).setScale(0.375f, 0).setLifetime(20)
                .randomOffset(0.25, 0.25).randomVelocity(0.015625f, 0.015625f)
                .setColor(r2, g2, b2)
                .repeat(world, x, y, z, 6);
    }

    public @NotNull Type<MagicBurstEffectPacket> type() {
        return TYPE;
    }
}
