package elucent.eidolon.network;

import java.util.function.Supplier;

import elucent.eidolon.Eidolon;
import elucent.eidolon.particle.Particles;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;



public class CrucibleFailPacket {
    BlockPos pos;

    public CrucibleFailPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(CrucibleFailPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
    }

    public static CrucibleFailPacket decode(FriendlyByteBuf buffer) {
        return new CrucibleFailPacket(buffer.readBlockPos());
    }

    public static void consume(CrucibleFailPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Level world = Eidolon.proxy.getWorld();
            if (world != null) {
                BlockPos pos = packet.pos;
                double x = pos.getX() + 0.5, y = pos.getY() + 0.625, z = pos.getZ() + 0.5;
                world.playSound(Eidolon.proxy.getPlayer(), x, y, z, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);

                Particles.create(elucent.eidolon.registries.Particles.SMOKE_PARTICLE)
                    .setAlpha(0.125f, 0).setScale(0.3125f, 0.125f).setLifetime(40)
                    .randomOffset(0.375, 0.125).randomVelocity(0.0125f, 0.0125f)
                    .setColor(0.5f, 0.5f, 0.5f, 0.25f, 0.25f, 0.25f)
                    .repeat(world, x, y, z, 20);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
