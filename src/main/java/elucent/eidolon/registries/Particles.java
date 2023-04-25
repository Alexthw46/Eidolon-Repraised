package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Particles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Eidolon.MODID);
    public static final RegistryObject<RuneParticleType>
            RUNE_PARTICLE = PARTICLES.register("rune_particle", RuneParticleType::new);
    public static final RegistryObject<GlowingSlashParticleType>
            GLOWING_SLASH_PARTICLE = PARTICLES.register("glowing_slash_particle", GlowingSlashParticleType::new);
    public static final RegistryObject<SlashParticleType>
            SLASH_PARTICLE = PARTICLES.register("slash_particle", SlashParticleType::new);
    public static final RegistryObject<SignParticleType>
            SIGN_PARTICLE = PARTICLES.register("sign_particle", SignParticleType::new);
    public static final RegistryObject<SteamParticleType>
            STEAM_PARTICLE = PARTICLES.register("steam_particle", SteamParticleType::new);
    public static final RegistryObject<LineWispParticleType>
            LINE_WISP_PARTICLE = PARTICLES.register("line_wisp_particle", LineWispParticleType::new);
    public static final RegistryObject<BubbleParticleType>
            BUBBLE_PARTICLE = PARTICLES.register("bubble_particle", BubbleParticleType::new);
    public static final RegistryObject<WispParticleType>
            WISP_PARTICLE = PARTICLES.register("wisp_particle", WispParticleType::new);
    public static final RegistryObject<SparkleParticleType>
            SPARKLE_PARTICLE = PARTICLES.register("sparkle_particle", SparkleParticleType::new);
    public static final RegistryObject<SmokeParticleType>
            SMOKE_PARTICLE = PARTICLES.register("smoke_particle", SmokeParticleType::new);
    public static final RegistryObject<FlameParticleType>
            FLAME_PARTICLE = PARTICLES.register("flame_particle", FlameParticleType::new);
}
