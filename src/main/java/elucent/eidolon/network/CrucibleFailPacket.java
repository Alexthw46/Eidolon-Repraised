package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.registries.EidolonParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CrucibleFailPacket extends AbstractPacket {
    public static final Type<CrucibleFailPacket> TYPE = new Type<>(Eidolon.prefix("crucible_fail"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleFailPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            pkt -> pkt.pos,
            CrucibleFailPacket::new
    );

    final BlockPos pos;

    public CrucibleFailPacket(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();
        BlockPos pos = this.pos;
        double x = pos.getX() + 0.5, y = pos.getY() + 0.625, z = pos.getZ() + 0.5;
        world.playSound(Eidolon.proxy.getPlayer(), x, y, z, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);

        Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                .setAlpha(0.125f, 0).setScale(0.3125f, 0.125f).setLifetime(40)
                .randomOffset(0.375, 0.125).randomVelocity(0.0125f, 0.0125f)
                .setColor(0.5f, 0.5f, 0.5f, 0.25f, 0.25f, 0.25f)
                .repeat(world, x, y, z, 20);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
