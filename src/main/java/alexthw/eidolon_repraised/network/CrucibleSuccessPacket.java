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

public class CrucibleSuccessPacket extends AbstractPacket {
    public static final Type<CrucibleSuccessPacket> TYPE = new Type<>(Eidolon.prefix("crucible_success"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleSuccessPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            pkt -> pkt.pos,
            ByteBufCodecs.FLOAT,
            pkt -> pkt.r,
            ByteBufCodecs.FLOAT,
            pkt -> pkt.g,
            ByteBufCodecs.FLOAT,
            pkt -> pkt.b,
            CrucibleSuccessPacket::new
    );

    final BlockPos pos;
    final float r;
    final float g;
    final float b;

    public CrucibleSuccessPacket(BlockPos pos, float r, float g, float b) {
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
        world.playSound(Eidolon.proxy.getPlayer(), x, y, z, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 0.75f);
        world.playSound(Eidolon.proxy.getPlayer(), x, y, z, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0f, 0.75f);

        Particles.create(EidolonParticles.STEAM_PARTICLE.get())
                .setAlpha(0.0625f, 0).setScale(0.375f, 0.125f).setLifetime(40)
                .randomOffset(0.375, 0.125).randomVelocity(0.025f, 0.0125f)
                .addVelocity(0, 0.0125f, 0)
                .setColor(this.r, this.g, this.b)
                .repeat(world, pos.getX() + 0.5, pos.getY() + 0.875, pos.getZ() + 0.5, 20);
        Particles.create(EidolonParticles.SPARKLE_PARTICLE.get())
                .setAlpha(0.0625f, 0).setScale(0.125f, 0.0f).setLifetime(80)
                .randomOffset(0.375, 0.375).randomVelocity(0.00625f, 0.00625f)
                .addVelocity(0, 0, 0)
                .setColor(this.r, this.g, this.b)
                .setSpin(0.1f)
                .repeat(world, pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5, 8);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
