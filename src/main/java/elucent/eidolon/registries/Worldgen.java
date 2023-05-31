package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class Worldgen {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Eidolon.MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFG_FEATURES = DeferredRegister.create(net.minecraft.core.Registry.CONFIGURED_FEATURE_REGISTRY, Eidolon.MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(net.minecraft.core.Registry.PLACED_FEATURE_REGISTRY, Eidolon.MODID);
    static RegistryObject<PlacedFeature> LEAD_ORE_GEN, SILVER_ORE_GEN;
    static RegistryObject<ConfiguredFeature<?, ?>> LEAD_ORE_CFG, SILVER_ORE_CFG;

    static {

        LEAD_ORE_CFG = CONFG_FEATURES.register("lead_ore",
                () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
                        OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Registry.LEAD_ORE.get().defaultBlockState()),
                        OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Registry.DEEP_LEAD_ORE.get().defaultBlockState())),
                        6)));

        SILVER_ORE_CFG = CONFG_FEATURES.register("silver_ore",
                () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
                        OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Registry.SILVER_ORE.get().defaultBlockState()),
                        OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Registry.DEEP_SILVER_ORE.get().defaultBlockState())),
                        6)));

        LEAD_ORE_GEN = PLACED_FEATURES.register("lead_ore_placed", () -> new PlacedFeature(LEAD_ORE_CFG.getHolder().get(),
                commonOrePlacement(6, // VeinsPerChunk
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-27), VerticalAnchor.absolute(41))
                )));
        SILVER_ORE_GEN = PLACED_FEATURES.register("silver_ore_placed", () -> new PlacedFeature(SILVER_ORE_CFG.getHolder().get(),
                commonOrePlacement(5, // VeinsPerChunk
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-60), VerticalAnchor.absolute(33)))
        ));
    }

    public static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    public static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);

    }
}