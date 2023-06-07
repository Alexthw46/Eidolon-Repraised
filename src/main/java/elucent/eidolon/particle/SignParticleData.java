package elucent.eidolon.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.registries.EidolonParticles;
import elucent.eidolon.spell.Signs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SignParticleData implements ParticleOptions {
    final Sign sign;

    public static Codec<SignParticleData> codecFor(ParticleType<?> type) {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("sign").forGetter((d) -> d.sign.getRegistryName().toString())
        ).apply(instance, (sign) -> {
            return new SignParticleData(Signs.find(new ResourceLocation(sign)));
        }));
    }

    public SignParticleData(Sign sign) {
        this.sign = sign;
    }

    @Override
    public ParticleType<?> getType() {
        return EidolonParticles.SIGN_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(sign.toString());
    }

    @Override
    public String writeToString() {
        return getClass().getSimpleName() + ":internal";
    }

    public static final Deserializer<SignParticleData> DESERIALIZER = new Deserializer<>() {
        @Override
        public SignParticleData fromCommand(ParticleType<SignParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            String loc = reader.readString();
            return new SignParticleData(Signs.find(new ResourceLocation(loc)));
        }

        @Override
        public SignParticleData fromNetwork(ParticleType<SignParticleData> type, FriendlyByteBuf buf) {
            String loc = buf.readUtf();
            return new SignParticleData(Signs.find(new ResourceLocation(loc)));
        }
    };
}
