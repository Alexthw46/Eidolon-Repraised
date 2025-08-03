package alexthw.eidolon_repraised.client.particle;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.registries.Signs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SignParticleData implements ParticleOptions {
    final Sign sign;

    public static MapCodec<SignParticleData> codecFor(ParticleType<?> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("sign").forGetter((d) -> d.sign.getRegistryName().toString())
        ).apply(instance, (sign) -> new SignParticleData(Signs.find(ResourceLocation.parse(sign)))));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, SignParticleData> streamCodecFor(ParticleType<?> type) {
        return StreamCodec.of(
                (buf, data) -> ByteBufCodecs.STRING_UTF8.encode(buf, data.sign.getRegistryName().toString()),
                (buf) -> new SignParticleData(Signs.find(ResourceLocation.parse(ByteBufCodecs.STRING_UTF8.decode(buf))))
        );
    }

    public SignParticleData(Sign sign) {
        this.sign = sign;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return EidolonParticles.SIGN_PARTICLE.get();
    }

}
