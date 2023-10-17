package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.registries.EidolonParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FeatherEffectPacket {
    final float x;
    final float y;
    final float z;

    public FeatherEffectPacket(BlockPos pos) {
        this(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public FeatherEffectPacket(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public static void encode(FeatherEffectPacket object, FriendlyByteBuf buffer) {
        buffer.writeFloat(object.x).writeFloat(object.y).writeFloat(object.z);
    }

    public static FeatherEffectPacket decode(FriendlyByteBuf buffer) {
        return new FeatherEffectPacket(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
    }

    public static void consume(FeatherEffectPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Level world = Eidolon.proxy.getWorld();
            if (world != null) {
                double x = packet.x, y = packet.y + 0.5, z = packet.z;

                Particles.create(EidolonParticles.FEATHER_PARTICLE)
                        .setAlpha(0.5f, 0).setScale(0.5f, 0).setLifetime(20)
                        .randomOffset(0.125, 0.125).randomVelocity(0.0625f)
                        .setColor(0.2F, 0.2F, 0.7F)
                        .repeat(world, x, y, z, 6);

                Particles.create(EidolonParticles.FEATHER_PARTICLE)
                        .setAlpha(0.25f, 0).setScale(0.5f, 0).setLifetime(10)
                        .randomOffset(0.125, 0.125).randomVelocity(0.0625f)
                        .setColor(0.3F, 0.3F, 0.7F)
                        .repeat(world, x + 0.5, y, z + 0.5, 6);

                Particles.create(EidolonParticles.FEATHER_PARTICLE)
                        .setAlpha(0.5f, 0).setScale(0.5f, 0).setLifetime(10)
                        .randomOffset(0.125, 0.125).randomVelocity(0.0625f)
                        .setColor(0.3F, 0.3F, 0.7F)
                        .repeat(world, x - 0.5, y, z - 0.5, 6);

                Particles.create(EidolonParticles.SPARKLE_PARTICLE)
                        .setAlpha(1, 0).setScale(0.0625f, 0).setLifetime(80)
                        .randomOffset(0.0625, 0).randomVelocity(0.125f, 0.125f)
                        .addVelocity(0, 0.25f, 0)
                        .setColor(0.2F, 0.2F, 0.7F)
                        .enableGravity().setSpin(0.4f)
                        .repeat(world, x, y, z, world.random.nextInt(4) + 3);
                Particles.create(EidolonParticles.SMOKE_PARTICLE)
                        .setAlpha(0.25f, 0).setScale(0.375f, 0).setLifetime(20)
                        .randomOffset(0.25, 0.25).randomVelocity(0.015625f, 0.015625f)
                        .setColor(0.2F, 0.2F, 0.7F)
                        .repeat(world, x, y, z, 6);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}