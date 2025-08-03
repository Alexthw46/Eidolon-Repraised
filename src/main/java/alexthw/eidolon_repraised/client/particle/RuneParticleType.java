package alexthw.eidolon_repraised.client.particle;

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

public class RuneParticleType extends ParticleType<RuneParticleData> {
    public RuneParticleType() {
        super(false);
    }

    @Override
    public @NotNull MapCodec<RuneParticleData> codec() {
        return RuneParticleData.codecFor();
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, RuneParticleData> streamCodec() {
        return RuneParticleData.streamCodecFor();
    }

    public static class Factory implements ParticleProvider<RuneParticleData> {
        public Factory() {
            //
        }

        @Override
        public Particle createParticle(@NotNull RuneParticleData data, @NotNull ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
            RuneParticle ret = new RuneParticle(world, data, x, y, z, mx, my, mz);
            ret.pickSprite(new SpriteSet() {
                @Override
                public @NotNull TextureAtlasSprite get(int particleAge, int particleMaxAge) {
                    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ret.rune.getSprite());
                }

                @Override
                public @NotNull TextureAtlasSprite get(@NotNull RandomSource rand) {
                    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ret.rune.getSprite());
                }
            });
            return ret;
        }
    }
}
