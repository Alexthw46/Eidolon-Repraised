package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GenericParticlePacket {

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

    public static GenericParticlePacket decode(FriendlyByteBuf pBuffer) {
        ParticleType<?> particletype = pBuffer.readById(BuiltInRegistries.PARTICLE_TYPE);
        double x = pBuffer.readDouble();
        double y = pBuffer.readDouble();
        double z = pBuffer.readDouble();
        double xSpeed = pBuffer.readDouble();
        double ySpeed = pBuffer.readDouble();
        double zSpeed = pBuffer.readDouble();
        var particle = readParticle(pBuffer, particletype);
        return new GenericParticlePacket(x, y, z, xSpeed, ySpeed, zSpeed, particle);
    }

    private static <T extends ParticleOptions> T readParticle(FriendlyByteBuf pBuffer, ParticleType<T> pParticleType) {
        return pParticleType.getDeserializer().fromNetwork(pParticleType, pBuffer);
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeId(BuiltInRegistries.PARTICLE_TYPE, this.particle.getType());
        pBuffer.writeDouble(this.x);
        pBuffer.writeDouble(this.y);
        pBuffer.writeDouble(this.z);
        pBuffer.writeDouble(this.xSpeed);
        pBuffer.writeDouble(this.ySpeed);
        pBuffer.writeDouble(this.zSpeed);
        this.particle.writeToNetwork(pBuffer);
    }

    public static void consume(GenericParticlePacket packet, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            if (pContext.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level world = Eidolon.proxy.getWorld();
                if (world != null) {
                    world.addParticle(packet.particle, packet.x, packet.y, packet.z, packet.xSpeed, packet.ySpeed, packet.zSpeed);
                }
            }

        });
        pContext.get().setPacketHandled(true);
    }

}
