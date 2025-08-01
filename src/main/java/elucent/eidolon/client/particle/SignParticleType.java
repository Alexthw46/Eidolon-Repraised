package elucent.eidolon.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

public class SignParticleType extends ParticleType<SignParticleData> {
    public SignParticleType() {
        super(false);
    }

    @Override
    public @NotNull MapCodec<SignParticleData> codec() {
        return SignParticleData.codecFor(this);
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, SignParticleData> streamCodec() {
        return SignParticleData.streamCodecFor(this);
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
                    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ret.sign.sprite());
                }

                @Override
                public @NotNull TextureAtlasSprite get(@NotNull RandomSource rand) {
                    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ret.sign.sprite());
                }
            });
            return ret;
        }
    }
}
