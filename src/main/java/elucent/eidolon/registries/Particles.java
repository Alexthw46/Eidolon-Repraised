package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Particles {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Eidolon.MODID);
    public static RegistryObject<RuneParticleType>
            RUNE_PARTICLE = PARTICLES.register("rune_particle", RuneParticleType::new);
    public static RegistryObject<GlowingSlashParticleType>
            GLOWING_SLASH_PARTICLE = PARTICLES.register("glowing_slash_particle", GlowingSlashParticleType::new);
    public static RegistryObject<SlashParticleType>
            SLASH_PARTICLE = PARTICLES.register("slash_particle", SlashParticleType::new);
    public static RegistryObject<SignParticleType>
            SIGN_PARTICLE = PARTICLES.register("sign_particle", SignParticleType::new);
    public static RegistryObject<SteamParticleType>
            STEAM_PARTICLE = PARTICLES.register("steam_particle", SteamParticleType::new);
    public static RegistryObject<LineWispParticleType>
            LINE_WISP_PARTICLE = PARTICLES.register("line_wisp_particle", LineWispParticleType::new);
    public static RegistryObject<BubbleParticleType>
            BUBBLE_PARTICLE = PARTICLES.register("bubble_particle", BubbleParticleType::new);
    public static RegistryObject<WispParticleType>
            WISP_PARTICLE = PARTICLES.register("wisp_particle", WispParticleType::new);
    public static RegistryObject<SparkleParticleType>
            SPARKLE_PARTICLE = PARTICLES.register("sparkle_particle", SparkleParticleType::new);
    public static RegistryObject<SmokeParticleType>
            SMOKE_PARTICLE = PARTICLES.register("smoke_particle", SmokeParticleType::new);
    public static RegistryObject<FlameParticleType>
            FLAME_PARTICLE = PARTICLES.register("flame_particle", FlameParticleType::new);
}
