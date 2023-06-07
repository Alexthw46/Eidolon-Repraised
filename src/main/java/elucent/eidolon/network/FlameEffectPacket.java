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

public class FlameEffectPacket {
    final BlockPos pos;
    final float r;
    final float g;
    final float b;

    public FlameEffectPacket(BlockPos pos, float r, float g, float b) {
        this.pos = pos;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static void encode(FlameEffectPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
        buffer.writeFloat(object.r).writeFloat(object.g).writeFloat(object.b);
    }

    public static FlameEffectPacket decode(FriendlyByteBuf buffer) {
        return new FlameEffectPacket(buffer.readBlockPos(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
    }

    public static void consume(FlameEffectPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Level world = Eidolon.proxy.getWorld();
            if (world != null) {
                BlockPos pos = packet.pos;
                double x = pos.getX() + 0.5, y = pos.getY() + 1, z = pos.getZ() + 0.5;
                world.playSound(Eidolon.proxy.getPlayer(), x, y, z, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0f, 0.75f);

                Particles.create(EidolonParticles.FLAME_PARTICLE)
                    .setAlpha(0.75f, 0).setScale(0.5f, 0.25f).setLifetime(40)
                    .randomOffset(0.5, 0.125).randomVelocity(0.003125f, 0.009375f)
                    .addVelocity(0, 0.003125f, 0)
                    .setColor(packet.r, packet.g, packet.b, packet.r, packet.g * 0.5f, packet.b * 1.5f)
                    .repeat(world, x, y, z, 10);
                Particles.create(EidolonParticles.SPARKLE_PARTICLE)
                    .setAlpha(1, 0).setScale(0.0625f, 0).setLifetime(80)
                    .randomOffset(0.0625, 0).randomVelocity(0.125f, 0.125f)
                    .addVelocity(0, 0.25f, 0)
                    .setColor(packet.r, packet.g * 1.5f, packet.b * 2.0f, packet.r, packet.g, packet.b)
                    .enableGravity().setSpin(0.4f)
                    .repeat(world, x, y, z, world.random.nextInt(4) + 3);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
