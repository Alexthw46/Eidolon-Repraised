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

public class SlashParticleType extends ParticleType<SlashParticleData> {
    public SlashParticleType() {
        super(false);
    }

    @Override
    public @NotNull MapCodec<SlashParticleData> codec() {
        return SlashParticleData.codecFor(this);
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, SlashParticleData> streamCodec() {
        return SlashParticleData.streamCodecFor(this);
    }

    public static class Factory implements ParticleProvider<SlashParticleData> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@NotNull SlashParticleData data, @NotNull ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
            SlashParticle ret = new SlashParticle(world, data, x, y, z, mx, my, mz);
            ret.pickSprite(sprite);
            return ret;
        }
    }
}
