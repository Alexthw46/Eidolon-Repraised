package alexthw.eidolon_repraised.client;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.model.*;
import alexthw.eidolon_repraised.client.renderer.*;
import alexthw.eidolon_repraised.common.item.ChantScrollItem;
import alexthw.eidolon_repraised.common.item.curio.RavenCloakRenderer;
import alexthw.eidolon_repraised.common.item.curio.SanguineAmuletItem;
import alexthw.eidolon_repraised.common.item.model.*;
import alexthw.eidolon_repraised.common.tile.CrucibleTileRenderer;
import alexthw.eidolon_repraised.registries.EidolonEntities;
import alexthw.eidolon_repraised.registries.Registry;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.io.IOException;

@EventBusSubscriber(modid = Eidolon.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistry {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(SanguineAmuletItem.SanguineAmuletTooltipInfo.class, SanguineAmuletItem.SanguineAmuletTooltipComponent::new);
        event.register(ChantScrollItem.ChantTooltipInfo.class, ChantScrollItem.ChantTooltipComponent::new);
    }

    public static final ModelLayerLocation SILVER_ARMOR_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "silver_armor"), "main");
    public static final ModelLayerLocation WARLOCK_ARMOR_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "warlock_armor"), "main");
    public static final ModelLayerLocation BONELORD_ARMOR_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "bonelord_armor"), "main");
    public static final ModelLayerLocation TOP_HAT_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "top_hat"), "main");
    public static final ModelLayerLocation RAVEN_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "raven"), "main");
    public static final ModelLayerLocation NECROMANCER_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "necromancer"), "main");
    public static final ModelLayerLocation WRAITH_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "wraith"), "main");
    public static final ModelLayerLocation ZOMBIE_BRUTE_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "zombie_brute"), "main");
    public static final ModelLayerLocation SLUG_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "slimy_slug"), "main");
    public static final ModelLayerLocation GIANT_SKEL_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "giant_skeleton"), "main");

    public static final ModelLayerLocation CRUCIBLE_STIRRER_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "crucible_stirrer"), "main");
    public static final ModelLayerLocation RAVEN_CLOAK_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "raven_cloak"), "main");

    public static WarlockArmorModel WARLOCK_ARMOR_MODEL = null;
    public static BonelordArmorModel BONELORD_ARMOR_MODEL = null;
    public static TopHatModel TOP_HAT_MODEL = null;
    public static SilverArmorModel SILVER_ARMOR_MODEL = null;
    public static ZombieBruteModel ZOMBIE_BRUTE_MODEL = null;
    public static BruteSkeletonModel GIANT_SKEL_MODEL = null;
    public static WraithModel WRAITH_MODEL = null;
    public static RavenModel RAVEN_MODEL = null;
    public static NecromancerModel NECROMANCER_MODEL = null;
    public static SlimySlugModel SLUG_MODEL = null;

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WARLOCK_ARMOR_LAYER, WarlockArmorModel::createBodyLayer);
        event.registerLayerDefinition(BONELORD_ARMOR_LAYER, BonelordArmorModel::createBodyLayer);
        event.registerLayerDefinition(TOP_HAT_LAYER, TopHatModel::createBodyLayer);
        event.registerLayerDefinition(SILVER_ARMOR_LAYER, SilverArmorModel::createBodyLayer);
        event.registerLayerDefinition(RAVEN_CLOAK_LAYER, RavenCloakModel::createBodyLayer);

        event.registerLayerDefinition(RAVEN_LAYER, RavenModel::createBodyLayer);
        event.registerLayerDefinition(ZOMBIE_BRUTE_LAYER, ZombieBruteModel::createBodyLayer);
        event.registerLayerDefinition(WRAITH_LAYER, WraithModel::createBodyLayer);
        event.registerLayerDefinition(NECROMANCER_LAYER, NecromancerModel::createBodyLayer);
        event.registerLayerDefinition(SLUG_LAYER, SlimySlugModel::createBodyLayer);
        event.registerLayerDefinition(GIANT_SKEL_LAYER, BruteSkeletonModel::createBodyLayer);

        event.registerLayerDefinition(CRUCIBLE_STIRRER_LAYER, CrucibleTileRenderer::createModelLayer);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.AddLayers event) {
        WARLOCK_ARMOR_MODEL = new WarlockArmorModel(event.getEntityModels().bakeLayer(WARLOCK_ARMOR_LAYER));
        BONELORD_ARMOR_MODEL = new BonelordArmorModel(event.getEntityModels().bakeLayer(BONELORD_ARMOR_LAYER));
        TOP_HAT_MODEL = new TopHatModel(event.getEntityModels().bakeLayer(TOP_HAT_LAYER));
        SILVER_ARMOR_MODEL = new SilverArmorModel(event.getEntityModels().bakeLayer(SILVER_ARMOR_LAYER));

        RAVEN_MODEL = new RavenModel(event.getEntityModels().bakeLayer(RAVEN_LAYER));
        ZOMBIE_BRUTE_MODEL = new ZombieBruteModel(event.getEntityModels().bakeLayer(ZOMBIE_BRUTE_LAYER));
        GIANT_SKEL_MODEL = new BruteSkeletonModel(event.getEntityModels().bakeLayer(GIANT_SKEL_LAYER));
        WRAITH_MODEL = new WraithModel(event.getEntityModels().bakeLayer(WRAITH_LAYER));
        NECROMANCER_MODEL = new NecromancerModel(event.getEntityModels().bakeLayer(NECROMANCER_LAYER));
        SLUG_MODEL = new SlimySlugModel(event.getEntityModels().bakeLayer(SLUG_LAYER));
    }

    @SubscribeEvent
    public static void onRegisterEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        EntityRenderers.register(EidolonEntities.ZOMBIE_BRUTE.get(), ZombieBruteRenderer::new);
        EntityRenderers.register(EidolonEntities.GIANT_SKEL.get(), GiantSkeletonRenderer::new);
        EntityRenderers.register(EidolonEntities.WRAITH.get(), WraithRenderer::new);
        EntityRenderers.register(EidolonEntities.NECROMANCER.get(), NecromancerRenderer::new);
        EntityRenderers.register(EidolonEntities.SOULFIRE_PROJECTILE.get(), NoopRenderer::new);
        EntityRenderers.register(EidolonEntities.BONECHILL_PROJECTILE.get(), NoopRenderer::new);
        EntityRenderers.register(EidolonEntities.NECROMANCER_SPELL.get(), NoopRenderer::new);
        EntityRenderers.register(EidolonEntities.CHANT_CASTER.get(), ChantCasterRenderer::new);
        EntityRenderers.register(EidolonEntities.RAVEN.get(), RavenRenderer::new);
        EntityRenderers.register(EidolonEntities.SLIMY_SLUG.get(), SlimySlugRenderer::new);
    }

    public static ShaderInstance GLOWING_SHADER, GLOWING_SPRITE_SHADER, GLOWING_PARTICLE_SHADER, VAPOR_SHADER, GLOWING_ENTITY_SHADER, SPRITE_PARTICLE_SHADER;

    public static ShaderInstance getGlowingShader() {
        return GLOWING_SHADER;
    }

    public static ShaderInstance getGlowingSpriteShader() {
        return GLOWING_SPRITE_SHADER;
    }

    public static ShaderInstance getGlowingParticleShader() {
        return GLOWING_PARTICLE_SHADER;
    }

    public static ShaderInstance getGlowingEntityShader() {
        return GLOWING_ENTITY_SHADER;
    }

    public static ShaderInstance getVaporShader() {
        return VAPOR_SHADER;
    }

    public static ShaderInstance getSpriteParticleShader() {
        return SPRITE_PARTICLE_SHADER;
    }

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "glowing"), DefaultVertexFormat.POSITION_COLOR),
                shader -> GLOWING_SHADER = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "glowing_sprite"), DefaultVertexFormat.POSITION_TEX_COLOR),
                shader -> GLOWING_SPRITE_SHADER = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "glowing_particle"), DefaultVertexFormat.PARTICLE),
                shader -> GLOWING_PARTICLE_SHADER = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "glowing_entity"), DefaultVertexFormat.NEW_ENTITY),
                shader -> GLOWING_ENTITY_SHADER = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "vapor"), DefaultVertexFormat.BLOCK),
                shader -> VAPOR_SHADER = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "sprite_particle"), DefaultVertexFormat.PARTICLE),
                shader -> SPRITE_PARTICLE_SHADER = shader);
    }

    public static void initCurios() {
        CuriosRendererRegistry.register(Registry.RAVEN_CLOAK.get(), RavenCloakRenderer::new);
    }

}
