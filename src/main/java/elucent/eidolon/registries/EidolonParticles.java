package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.client.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class EidolonParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Eidolon.MODID);
    public static final DeferredHolder<ParticleType<?>, RuneParticleType>
            RUNE_PARTICLE = PARTICLES.register("rune_particle", RuneParticleType::new);
    public static final DeferredHolder<ParticleType<?>, GlowingSlashParticleType>
            GLOWING_SLASH_PARTICLE = PARTICLES.register("glowing_slash_particle", GlowingSlashParticleType::new);
    public static final DeferredHolder<ParticleType<?>, SlashParticleType>
            SLASH_PARTICLE = PARTICLES.register("slash_particle", SlashParticleType::new);
    public static final DeferredHolder<ParticleType<?>, SignParticleType>
            SIGN_PARTICLE = PARTICLES.register("sign_particle", SignParticleType::new);
    public static final DeferredHolder<ParticleType<?>, SteamParticleType>
            STEAM_PARTICLE = PARTICLES.register("steam_particle", SteamParticleType::new);
    public static final DeferredHolder<ParticleType<?>, LineWispParticleType>
            LINE_WISP_PARTICLE = PARTICLES.register("line_wisp_particle", LineWispParticleType::new);
    public static final DeferredHolder<ParticleType<?>, BubbleParticleType>
            BUBBLE_PARTICLE = PARTICLES.register("bubble_particle", BubbleParticleType::new);
    public static final DeferredHolder<ParticleType<?>, WispParticleType>
            WISP_PARTICLE = PARTICLES.register("wisp_particle", WispParticleType::new);
    public static final DeferredHolder<ParticleType<?>, SparkleParticleType>
            SPARKLE_PARTICLE = PARTICLES.register("sparkle_particle", SparkleParticleType::new);
    public static final DeferredHolder<ParticleType<?>, SmokeParticleType>
            SMOKE_PARTICLE = PARTICLES.register("smoke_particle", SmokeParticleType::new);
    public static final DeferredHolder<ParticleType<?>, FlameParticleType>
            FLAME_PARTICLE = PARTICLES.register("flame_particle", FlameParticleType::new);
    public static final DeferredHolder<ParticleType<?>, FeatherParticleType>
            FEATHER_PARTICLE = PARTICLES.register("feather_particle", FeatherParticleType::new);

}
