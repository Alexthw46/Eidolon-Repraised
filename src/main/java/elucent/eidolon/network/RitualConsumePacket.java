package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.registries.EidolonParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public class RitualConsumePacket extends AbstractPacket {
    public static final Type<RitualConsumePacket> TYPE = new Type<>(Eidolon.prefix("ritual_consume"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RitualConsumePacket> CODEC = StreamCodec.composite(
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
            RitualConsumePacket::new
    );

    final BlockPos src;
    final BlockPos dst;
    final float r;
    final float g;
    final float b;

    public RitualConsumePacket(BlockPos src, BlockPos dst, float r, float g, float b) {
        this.src = src;
        this.dst = dst;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();

        BlockPos src = this.src, dst = this.dst;
        world.playSound(Eidolon.proxy.getPlayer(), src.getX() + 0.5, src.getY() + 0.5, src.getZ() + 0.5, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
        for (int i = 0; i < 10; i++) {
            Particles.create(EidolonParticles.LINE_WISP_PARTICLE.get())
                    .setAlpha(0.75f, 0).setScale(0.25f + 0.125f * world.random.nextFloat(), 0).setLifetime(16 + world.random.nextInt(4))
                    .randomOffset(0.375, 0.375).randomVelocity(0.125, 0.125)
                    .addVelocity(dst.getX() + 0.5, dst.getY() + 1, dst.getZ() + 0.5)
                    .setColor(this.r, this.g, this.b, this.r, this.g * 0.5f, this.b * 1.5f)
                    .spawn(world, src.getX() + 0.5, src.getY() + 0.5, src.getZ() + 0.5);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
