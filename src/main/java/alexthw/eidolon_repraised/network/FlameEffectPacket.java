package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.registries.EidolonParticles;
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

public class FlameEffectPacket extends AbstractPacket {
    public static final Type<FlameEffectPacket> TYPE = new Type<>(Eidolon.prefix("flame_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FlameEffectPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            pkt -> pkt.pos,
            ByteBufCodecs.FLOAT,
            pkt -> pkt.r,
            ByteBufCodecs.FLOAT,
            pkt -> pkt.g,
            ByteBufCodecs.FLOAT,
            pkt -> pkt.b,
            FlameEffectPacket::new
    );

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

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();
        BlockPos pos = this.pos;
        double x = pos.getX() + 0.5, y = pos.getY() + 1, z = pos.getZ() + 0.5;
        world.playSound(Eidolon.proxy.getPlayer(), x, y, z, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0f, 0.75f);

        Particles.create(EidolonParticles.FLAME_PARTICLE.get())
                .setAlpha(0.75f, 0).setScale(0.5f, 0.25f).setLifetime(40)
                .randomOffset(0.5, 0.125).randomVelocity(0.003125f, 0.009375f)
                .addVelocity(0, 0.003125f, 0)
                .setColor(this.r, this.g, this.b, this.r, this.g * 0.5f, this.b * 1.5f)
                .repeat(world, x, y, z, 10);
        Particles.create(EidolonParticles.SPARKLE_PARTICLE.get())
                .setAlpha(1, 0).setScale(0.0625f, 0).setLifetime(80)
                .randomOffset(0.0625, 0).randomVelocity(0.125f, 0.125f)
                .addVelocity(0, 0.25f, 0)
                .setColor(this.r, this.g * 1.5f, this.b * 2.0f, this.r, this.g, this.b)
                .enableGravity().setSpin(0.4f)
                .repeat(world, x, y, z, world.random.nextInt(4) + 3);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
