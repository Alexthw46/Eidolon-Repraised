package alexthw.eidolon_repraised.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class GenericParticleData implements ParticleOptions {
    float r1 = 1, g1 = 1, b1 = 1, a1 = 1, r2 = 1, g2 = 1, b2 = 1, a2 = 0;
    float scale1 = 1, scale2 = 0;
    int lifetime = 20;
    float spin = 0;
    boolean gravity = false;

    public static MapCodec<GenericParticleData> codecFor(ParticleType<?> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("r1").forGetter(d -> d.r1),
                Codec.FLOAT.fieldOf("g1").forGetter(d -> d.g1),
                Codec.FLOAT.fieldOf("b1").forGetter(d -> d.b1),
                Codec.FLOAT.fieldOf("a1").forGetter(d -> d.a1),
                Codec.FLOAT.fieldOf("r2").forGetter(d -> d.r2),
                Codec.FLOAT.fieldOf("g2").forGetter(d -> d.g2),
                Codec.FLOAT.fieldOf("b2").forGetter(d -> d.b2),
                Codec.FLOAT.fieldOf("a2").forGetter(d -> d.a2),
                Codec.FLOAT.fieldOf("scale1").forGetter(d -> d.scale1),
                Codec.FLOAT.fieldOf("scale2").forGetter(d -> d.scale2),
                Codec.INT.fieldOf("lifetime").forGetter(d -> d.lifetime),
                Codec.FLOAT.fieldOf("spin").forGetter(d -> d.spin),
                Codec.BOOL.fieldOf("gravity").forGetter(d -> d.gravity)
        ).apply(instance, (r1, g1, b1, a1, r2, g2, b2, a2, scale1, scale2,
                           lifetime, spin, gravity) -> {
            GenericParticleData data = new GenericParticleData(type);
            data.r1 = r1;
            data.g1 = g1;
            data.b1 = b1;
            data.a1 = a1;
            data.r2 = r2;
            data.g2 = g2;
            data.b2 = b2;
            data.a2 = a2;
            data.scale1 = scale1;
            data.scale2 = scale2;
            data.lifetime = lifetime;
            data.spin = spin;
            data.gravity = gravity;
            return data;
        }));
    }

    final ParticleType<?> type;

    public GenericParticleData(ParticleType<?> type) {
        this.type = type;
    }

    public static @NotNull StreamCodec<RegistryFriendlyByteBuf, GenericParticleData> streamCodecFor(ParticleType<? extends GenericParticleData> featherParticleType) {
        return StreamCodec.of(
                GenericParticleData::writeToNetwork,
                buf -> {
                    float r1 = buf.readFloat();
                    float g1 = buf.readFloat();
                    float b1 = buf.readFloat();
                    float a1 = buf.readFloat();
                    float r2 = buf.readFloat();
                    float g2 = buf.readFloat();
                    float b2 = buf.readFloat();
                    float a2 = buf.readFloat();
                    float scale1 = buf.readFloat();
                    float scale2 = buf.readFloat();
                    int lifetime = buf.readInt();
                    float spin = buf.readFloat();
                    boolean gravity = buf.readBoolean();
                    GenericParticleData data = new GenericParticleData(featherParticleType);
                    data.r1 = r1;
                    data.g1 = g1;
                    data.b1 = b1;
                    data.a1 = a1;
                    data.r2 = r2;
                    data.g2 = g2;
                    data.b2 = b2;
                    data.a2 = a2;
                    data.scale1 = scale1;
                    data.scale2 = scale2;
                    data.lifetime = lifetime;
                    data.spin = spin;
                    data.gravity = gravity;
                    return data;
                }

        );
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return type;
    }

    public static void writeToNetwork(FriendlyByteBuf buffer, GenericParticleData data) {
        buffer.writeFloat(data.r1).writeFloat(data.g1).writeFloat(data.b1).writeFloat(data.a1);
        buffer.writeFloat(data.r2).writeFloat(data.g2).writeFloat(data.b2).writeFloat(data.a2);
        buffer.writeFloat(data.scale1).writeFloat(data.scale2);
        buffer.writeInt(data.lifetime);
        buffer.writeFloat(data.spin);
        buffer.writeBoolean(data.gravity);
    }
}
