package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class LifestealEffectPacket extends AbstractPacket {
    public static final Type<LifestealEffectPacket> TYPE = new Type<>(Eidolon.prefix("lifesteal_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LifestealEffectPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            pkt -> pkt.src,
            BlockPos.STREAM_CODEC,
            pkt -> pkt.dst,
            ByteBufCodecs.FLOAT,
            pkt -> pkt.r,
            ByteBufCodecs.FLOAT,
            pkt -> pkt.g,
            ByteBufCodecs.FLOAT,
            pkt -> pkt.b,
            LifestealEffectPacket::new
    );

    final BlockPos src;
    final BlockPos dst;
    final float r;
    final float g;
    final float b;

    public LifestealEffectPacket(BlockPos src, BlockPos dst, float r, float g, float b) {
        this.src = src;
        this.dst = dst;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static void encode(LifestealEffectPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.src).writeBlockPos(object.dst);
        buffer.writeFloat(object.r).writeFloat(object.g).writeFloat(object.b);
    }

    public static LifestealEffectPacket decode(FriendlyByteBuf buffer) {
        return new LifestealEffectPacket(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
    }


    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();

        BlockPos src = this.src, dst = this.dst;
        for (int i = 0; i < 10; i++) {
            Particles.create(EidolonParticles.LINE_WISP_PARTICLE.get())
                    .setAlpha(0.75f, 0).setScale(0.25f + 0.125f * world.random.nextFloat(), 0).setLifetime(16 + world.random.nextInt(4))
                    .randomOffset(0.375, 0.375).randomVelocity(0.125, 0.125)
                    .addVelocity(dst.getX() + 0.5, dst.getY() + 0.5, dst.getZ() + 0.5)
                    .setColor(this.r, this.g, this.b, this.r, this.g * 0.5f, this.b * 1.5f)
                    .spawn(world, src.getX() + 0.5, src.getY() + 0.5, src.getZ() + 0.5);
        }

    }


    public @NotNull Type<LifestealEffectPacket> type() {
        return TYPE;
    }
}
