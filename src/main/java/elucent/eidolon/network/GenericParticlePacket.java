package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GenericParticlePacket extends AbstractPacket {
    public static final Type<GenericParticlePacket> TYPE = new Type<>(Eidolon.prefix("generic_particle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GenericParticlePacket> CODEC = StreamCodec.ofMember(
            GenericParticlePacket::encode,
            GenericParticlePacket::decode
    );

    private final double x;
    private final double y;
    private final double z;
    private final double xSpeed;
    private final double ySpeed;
    private final double zSpeed;
    private final ParticleOptions particle;

    public GenericParticlePacket(double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ParticleOptions particle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;
        this.particle = particle;
    }

    public static GenericParticlePacket decode(RegistryFriendlyByteBuf pBuffer) {
        double x = pBuffer.readDouble();
        double y = pBuffer.readDouble();
        double z = pBuffer.readDouble();
        double xSpeed = pBuffer.readDouble();
        double ySpeed = pBuffer.readDouble();
        double zSpeed = pBuffer.readDouble();
        var particle = ParticleTypes.STREAM_CODEC.decode(pBuffer);
        return new GenericParticlePacket(x, y, z, xSpeed, ySpeed, zSpeed, particle);
    }

    public void encode(RegistryFriendlyByteBuf pBuffer) {
        pBuffer.writeDouble(this.x);
        pBuffer.writeDouble(this.y);
        pBuffer.writeDouble(this.z);
        pBuffer.writeDouble(this.xSpeed);
        pBuffer.writeDouble(this.ySpeed);
        pBuffer.writeDouble(this.zSpeed);
        ParticleTypes.STREAM_CODEC.encode(pBuffer, this.particle);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level();
        world.addParticle(this.particle, this.x, this.y, this.z, this.xSpeed, this.ySpeed, this.zSpeed);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
