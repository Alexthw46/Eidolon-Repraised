package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class Worldgen {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Eidolon.MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFG_FEATURES = DeferredRegister.create(net.minecraft.core.Registry.CONFIGURED_FEATURE_REGISTRY, Eidolon.MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(net.minecraft.core.Registry.PLACED_FEATURE_REGISTRY, Eidolon.MODID);

    static RegistryObject<PlacedFeature> LEAD_ORE_GEN, SILVER_ORE_GEN, ILLWOOD_CHECKED, ILLWOOD_PLACED;

    public static RegistryObject<ConfiguredFeature<?, ?>> LEAD_ORE_CFG, SILVER_ORE_CFG, ILLWOOD_TREE_CFG, ILLWOOD_SPAWN;

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

        ILLWOOD_TREE_CFG = CONFG_FEATURES.register("illwood_tree", () -> new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Registry.ILLWOOD_LOG.get()),
                new DarkOakTrunkPlacer(5, 1, 3),
                BlockStateProvider.simple(Registry.ILLWOOD_LEAVES.get()),
                new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0)),
                new TwoLayersFeatureSize(1, 0, 2)).build()));

        ILLWOOD_CHECKED = PLACED_FEATURES.register("illwood_checked",
                () -> new PlacedFeature(Holder.direct(ILLWOOD_TREE_CFG.get()), List.of(PlacementUtils.filteredByBlockSurvival(Registry.ILLWOOD_SAPLING.get()))));

        ILLWOOD_SPAWN = CONFG_FEATURES.register("illwood_spawn",
                () -> new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(ILLWOOD_CHECKED.getHolder().get()))));

        ILLWOOD_PLACED = PLACED_FEATURES.register("illwood_placed",
                () -> new PlacedFeature(Holder.direct(ILLWOOD_SPAWN.get()), VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1F, 1))));
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