package elucent.eidolon.registries;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static elucent.eidolon.Eidolon.MODID;

public class Worldgen {

    public static final TagKey<Structure> CATACOMBS = TagKey.create(Registries.STRUCTURE, new ResourceLocation(MODID, "catacombs"));

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);

    public static ResourceKey<Feature<?>> registerFeatureKey(String name) {
        return ResourceKey.create(Registries.FEATURE, new ResourceLocation(MODID, name));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerConfKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MODID, name));
    }

    public static ResourceKey<PlacedFeature> registerPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MODID, name));
    }

    static final ResourceKey<PlacedFeature> LEAD_ORE_GEN = registerPlacedKey("lead_ore_placed");
    static final ResourceKey<PlacedFeature> SILVER_ORE_GEN = registerPlacedKey("silver_ore_placed");
    static final ResourceKey<PlacedFeature> ILLWOOD_CHECKED = registerPlacedKey("illwood_checked");
    static final ResourceKey<PlacedFeature> ILLWOOD_PLACED = registerPlacedKey("illwood_placed");

    public static final ResourceKey<ConfiguredFeature<?, ?>> LEAD_ORE_CFG = registerConfKey("lead_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_ORE_CFG = registerConfKey("silver_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ILLWOOD_TREE_CFG = registerConfKey("illwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ILLWOOD_SPAWN = registerConfKey("illwood_spawn");

    public static void bootstrapConfiguredFeatures(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest ruletest1 = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest ruletest2 = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);


        context.register(LEAD_ORE_CFG, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
                OreConfiguration.target(ruletest1, Registry.LEAD_ORE.get().defaultBlockState()),
                OreConfiguration.target(ruletest2, Registry.DEEP_LEAD_ORE.get().defaultBlockState())),
                6)));
        context.register(SILVER_ORE_CFG, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
                OreConfiguration.target(ruletest1, Registry.SILVER_ORE.get().defaultBlockState()),
                OreConfiguration.target(ruletest2, Registry.DEEP_SILVER_ORE.get().defaultBlockState())),
                6)));

        context.register(ILLWOOD_TREE_CFG, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Registry.ILLWOOD_LOG.get()),
                new DarkOakTrunkPlacer(5, 1, 3),
                BlockStateProvider.simple(Registry.ILLWOOD_LEAVES.get()),
                new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0)),
                new TwoLayersFeatureSize(1, 0, 2)).build()));

        context.register(ILLWOOD_SPAWN, new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(placed.getOrThrow(ILLWOOD_CHECKED)))));

    }

    public static void bootstrapPlacedFeatures(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(LEAD_ORE_GEN, new PlacedFeature(configured.get(LEAD_ORE_CFG).get(),
                commonOrePlacement(6, // VeinsPerChunk
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-47), VerticalAnchor.absolute(41))
                )));

        context.register(SILVER_ORE_GEN, new PlacedFeature(configured.get(SILVER_ORE_CFG).get(),
                commonOrePlacement(5, // VeinsPerChunk
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-60), VerticalAnchor.absolute(33)))
        ));

        context.register(ILLWOOD_CHECKED,
                new PlacedFeature(configured.get(ILLWOOD_TREE_CFG).get(), List.of(PlacementUtils.filteredByBlockSurvival(Registry.ILLWOOD_SAPLING.get()))));

        context.register(ILLWOOD_PLACED, new PlacedFeature(configured.get(ILLWOOD_SPAWN).get(), VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.01F, 0))));
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