package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.particle.Particles;
import elucent.eidolon.registries.EidolonParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class IgniteEffectPacket {
    final BlockPos pos;
    final float r;
    final float g;
    final float b;

    public IgniteEffectPacket(BlockPos pos, float r, float g, float b) {
        this.pos = pos;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static void encode(IgniteEffectPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
        buffer.writeFloat(object.r).writeFloat(object.g).writeFloat(object.b);
    }

    public static IgniteEffectPacket decode(FriendlyByteBuf buffer) {
        return new IgniteEffectPacket(buffer.readBlockPos(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
    }

    public static void consume(IgniteEffectPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Level world = Eidolon.proxy.getWorld();
            if (world != null) {
                BlockPos pos = packet.pos;
                double x = pos.getX() + 0.5, y = pos.getY() + 1, z = pos.getZ() + 0.5;
                world.playSound(Eidolon.proxy.getPlayer(), x, y, z, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

                Particles.create(EidolonParticles.FLAME_PARTICLE)
                    .setAlpha(0.75f, 0).setScale(0.5f, 0.25f).setLifetime(20)
                    .randomOffset(0.5, 0.125).randomVelocity(0.00625f, 0.01875f)
                    .addVelocity(0, 0.00625f, 0)
                    .setColor(packet.r, packet.g, packet.b, packet.r, packet.g * 0.5f, packet.b * 1.5f)
                    .repeat(world, x, y, z, 10);
                Particles.create(EidolonParticles.SPARKLE_PARTICLE)
                    .setAlpha(1, 0).setScale(0.0625f, 0).setLifetime(40)
                    .randomOffset(0.0625, 0).randomVelocity(0.125f, 0)
                    .addVelocity(0, 0.125f, 0)
                    .setColor(packet.r, packet.g * 1.5f, packet.b * 2.0f, packet.r, packet.g, packet.b)
                    .enableGravity().setSpin(0.4f)
                    .repeat(world, x, y, z, world.random.nextInt(2) + 2);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
