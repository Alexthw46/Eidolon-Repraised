package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class ChilledEffectPacket extends AbstractPacket {

    public static final Type<ChilledEffectPacket> TYPE = new Type<>(Eidolon.prefix("chilled_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChilledEffectPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            ChilledEffectPacket::getX,
            ByteBufCodecs.DOUBLE,
            ChilledEffectPacket::getY,
            ByteBufCodecs.DOUBLE,
            ChilledEffectPacket::getZ,
            ChilledEffectPacket::new
    );

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    final double x;
    final double y;
    final double z;

    public ChilledEffectPacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (player != null) {
            Level world = player.level();
            world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
            for (int i = 0; i < 5; i++) {
                world.addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()),
                        this.x, this.y, this.z,
                        0.05f * world.random.nextGaussian(), 0.05f * world.random.nextGaussian(), 0.05f * world.random.nextGaussian()
                );
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
