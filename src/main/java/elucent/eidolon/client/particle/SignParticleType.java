package elucent.eidolon.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

public class SignParticleType extends ParticleType<SignParticleData> {
    public SignParticleType() {
        super(false, SignParticleData.DESERIALIZER);
    }

    @Override
    public @NotNull Codec<SignParticleData> codec() {
        return SignParticleData.codecFor(this);
    }

    public static class Factory implements ParticleProvider<SignParticleData> {
        public Factory() {
            //
        }

        @Override
        public Particle createParticle(SignParticleData data, @NotNull ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
            SignParticle ret = new SignParticle(world, data.sign, x, y, z, mx, my, mz);
            ret.pickSprite(new SpriteSet() {
                @Override
                public @NotNull TextureAtlasSprite get(int particleAge, int particleMaxAge) {
                    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ret.sign.getSprite());
                }

                @Override
                public @NotNull TextureAtlasSprite get(@NotNull RandomSource rand) {
                    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ret.sign.getSprite());
                }
            });
            return ret;
        }
    }
}
