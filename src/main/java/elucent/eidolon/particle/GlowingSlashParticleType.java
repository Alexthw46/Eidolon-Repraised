package elucent.eidolon.particle;

import com.mojang.serialization.Codec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

public class GlowingSlashParticleType extends ParticleType<SlashParticleData> {
    public GlowingSlashParticleType() {
        super(false, SlashParticleData.DESERIALIZER);
    }

    @Override
    public @NotNull Codec<SlashParticleData> codec() {
        return SlashParticleData.codecFor(this);
    }

    public static class Factory implements ParticleProvider<SlashParticleData> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@NotNull SlashParticleData data, @NotNull ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
            GlowingSlashParticle ret = new GlowingSlashParticle(world, data, x, y, z, mx, my, mz);
            ret.pickSprite(sprite);
            return ret;
        }
    }
}
