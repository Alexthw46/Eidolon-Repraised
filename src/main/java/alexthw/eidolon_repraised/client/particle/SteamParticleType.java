package alexthw.eidolon_repraised.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class SteamParticleType extends ParticleType<GenericParticleData> {
    public SteamParticleType() {
        super(false);
    }

    @Override
    public @NotNull MapCodec<GenericParticleData> codec() {
        return GenericParticleData.codecFor(this);
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, GenericParticleData> streamCodec() {
        return GenericParticleData.streamCodecFor(this);
    }

    public static class Factory implements ParticleProvider<GenericParticleData> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@NotNull GenericParticleData data, @NotNull ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
            SteamParticle ret = new SteamParticle(world, data, x, y, z, mx, my, mz);
            ret.pickSprite(sprite);
            return ret;
        }
    }
}
