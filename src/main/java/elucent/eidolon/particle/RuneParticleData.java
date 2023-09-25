package elucent.eidolon.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import elucent.eidolon.api.spells.Rune;
import elucent.eidolon.registries.EidolonParticles;
import elucent.eidolon.registries.Runes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RuneParticleData implements ParticleOptions {
    final Rune rune;
    final float r1;
    final float g1;
    final float b1;
    final float r2;
    final float g2;
    final float b2;

    public static Codec<RuneParticleData> codecFor(ParticleType<?> type) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("rune").forGetter((d) -> d.rune.getRegistryName().toString()),
                Codec.FLOAT.fieldOf("r1").forGetter((d) -> d.r1),
                Codec.FLOAT.fieldOf("g1").forGetter((d) -> d.g1),
                Codec.FLOAT.fieldOf("b1").forGetter((d) -> d.b1),
                Codec.FLOAT.fieldOf("r2").forGetter((d) -> d.r2),
                Codec.FLOAT.fieldOf("g2").forGetter((d) -> d.g2),
                Codec.FLOAT.fieldOf("b2").forGetter((d) -> d.b2)
        ).apply(instance, (rune, r1, g1, b1, r2, g2, b2) -> new RuneParticleData(Runes.find(new ResourceLocation(rune)), r1, g1, b1, r2, g2, b2)));
    }

    public RuneParticleData(Rune rune, float r1, float g1, float b1, float r2, float g2, float b2) {
        this.rune = rune;
        this.r1 = r1;
        this.g1 = g1;
        this.b1 = b1;
        this.r2 = r2;
        this.g2 = g2;
        this.b2 = b2;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return EidolonParticles.RUNE_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(rune.toString());
        buffer.writeFloat(r1);
        buffer.writeFloat(g1);
        buffer.writeFloat(b1);
        buffer.writeFloat(r2);
        buffer.writeFloat(g2);
        buffer.writeFloat(b2);
    }

    @Override
    public @NotNull String writeToString() {
        return getClass().getSimpleName() + ":internal";
    }

    public static final Deserializer<RuneParticleData> DESERIALIZER = new Deserializer<>() {
        @Override
        public @NotNull RuneParticleData fromCommand(@NotNull ParticleType<RuneParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            String loc = reader.readString();
            reader.expect(' ');
            float r1 = reader.readFloat();
            reader.expect(' ');
            float g1 = reader.readFloat();
            reader.expect(' ');
            float b1 = reader.readFloat();
            reader.expect(' ');
            float r2 = reader.readFloat();
            reader.expect(' ');
            float g2 = reader.readFloat();
            reader.expect(' ');
            float b2 = reader.readFloat();
            return new RuneParticleData(Runes.find(new ResourceLocation(loc)), r1, g1, b1, r2, g2, b2);
        }

        @Override
        public @NotNull RuneParticleData fromNetwork(@NotNull ParticleType<RuneParticleData> type, FriendlyByteBuf buf) {
            String loc = buf.readUtf();
            float r1 = buf.readFloat();
            float g1 = buf.readFloat();
            float b1 = buf.readFloat();
            float r2 = buf.readFloat();
            float g2 = buf.readFloat();
            float b2 = buf.readFloat();
            return new RuneParticleData(Runes.find(new ResourceLocation(loc)), r1, g1, b1, r2, g2, b2);
        }
    };
}
