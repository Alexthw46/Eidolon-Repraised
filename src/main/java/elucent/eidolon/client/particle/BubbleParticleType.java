package elucent.eidolon.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

public class BubbleParticleType extends ParticleType<GenericParticleData> {
    public BubbleParticleType() {
        super(false, GenericParticleData.DESERIALIZER);
    }

    @Override
    public @NotNull Codec<GenericParticleData> codec() {
        return GenericParticleData.codecFor(this);
    }

    public static class Factory implements ParticleProvider<GenericParticleData> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@NotNull GenericParticleData data, @NotNull ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
            BubbleParticle ret = new BubbleParticle(world, data, sprite, x, y, z, mx, my, mz);
            ret.setSpriteFromAge(sprite);
            return ret;
        }
    }
}
