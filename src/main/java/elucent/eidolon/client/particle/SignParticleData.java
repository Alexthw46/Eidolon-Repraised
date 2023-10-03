package elucent.eidolon.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.registries.EidolonParticles;
import elucent.eidolon.registries.Signs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SignParticleData implements ParticleOptions {
    final Sign sign;

    public static Codec<SignParticleData> codecFor(ParticleType<?> type) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("sign").forGetter((d) -> d.sign.getRegistryName().toString())
        ).apply(instance, (sign) -> new SignParticleData(Signs.find(new ResourceLocation(sign)))));
    }

    public SignParticleData(Sign sign) {
        this.sign = sign;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return EidolonParticles.SIGN_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(sign.toString());
    }

    @Override
    public @NotNull String writeToString() {
        return getClass().getSimpleName() + ":internal";
    }

    public static final Deserializer<SignParticleData> DESERIALIZER = new Deserializer<>() {
        @Override
        public @NotNull SignParticleData fromCommand(@NotNull ParticleType<SignParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            String loc = reader.readString();
            return new SignParticleData(Signs.find(new ResourceLocation(loc)));
        }

        @Override
        public @NotNull SignParticleData fromNetwork(@NotNull ParticleType<SignParticleData> type, FriendlyByteBuf buf) {
            String loc = buf.readUtf();
            return new SignParticleData(Signs.find(new ResourceLocation(loc)));
        }
    };
}
