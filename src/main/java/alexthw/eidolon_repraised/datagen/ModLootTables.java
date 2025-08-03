package alexthw.eidolon_repraised.datagen;

import alexthw.eidolon_repraised.registries.DecoBlockPack;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModLootTables extends LootTableProvider {

    public ModLootTables(DataGenerator dataGeneratorIn, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(dataGeneratorIn.getPackOutput(), new HashSet<>(), List.of(new SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)), lookupProvider);
    }

    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public static class BlockLootTable extends BlockLootSubProvider {
        public final List<Block> list = new ArrayList<>();

        protected BlockLootTable(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> p_249322_) {
            this.generate();
            Set<ResourceKey<LootTable>> set = new HashSet<>();

            for (Block block : list) {
                if (block.isEnabled(this.enabledFeatures)) {
                    ResourceKey<LootTable> resourcelocation = block.getLootTable();
                    if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                        LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                        if (loottable$builder == null) {
                            continue;
                        }

                        p_249322_.accept(resourcelocation, loottable$builder);
                    }
                }
            }
        }

        protected void generate() {

            registerLeavesAndSticks(Registry.ILLWOOD_LEAVES.get(), Registry.ILLWOOD_SAPLING.get());
            registerDropSelf(Registry.ILLWOOD_SAPLING.get());
            registerDropSelf(Registry.ILLWOOD_LOG.get());
            registerDropSelf(Registry.ILLWOOD_BARK.get());
            registerDropSelf(Registry.STRIPPED_ILLWOOD_LOG.get());
            registerDropSelf(Registry.STRIPPED_ILLWOOD_BARK.get());
            registerDropSelf(Registry.ILLWOOD_PLANKS);
            registerDropSelf(Registry.ELDER_BRICKS);
            registerDropSelf(Registry.ELDER_MASONRY);
            registerDropSelf(Registry.SMOOTH_STONE_MASONRY);
            registerDropSelf(Registry.BONE_PILE);
            registerDropSelf(Registry.ELDER_PILLAR.get());
            registerDropSelf(Registry.ELDER_BRICKS_EYE.get());
            registerDropSelf(Registry.SMOOTH_STONE_ARCH.get());
            registerDropSelf(Registry.MOSSY_SMOOTH_STONE_BRICKS.get());
        }

        public void registerDropSelf(Block block) {
            this.list.add(block);
            this.dropSelf(block);
        }

        public void registerDropSelf(DecoBlockPack blockpack) {
            List<Block> blocks = new ArrayList<>();
            blocks.add(blockpack.getBlock());
            blocks.add(blockpack.getSlab());
            blocks.add(blockpack.getStairs());
            blocks.add(blockpack.getWall());

            if (blockpack instanceof DecoBlockPack.WoodDecoBlock woodDecoBlock) {
                blocks.add(woodDecoBlock.getFence());
                blocks.add(woodDecoBlock.getFenceGate());
                blocks.add(woodDecoBlock.getButton());
                blocks.add(woodDecoBlock.getPressurePlate());
                blocks.add(woodDecoBlock.getStandingSign());
                blocks.add(woodDecoBlock.getWallSign());
                blocks.add(woodDecoBlock.getHangingSign());
                blocks.add(woodDecoBlock.getHangingWallSign());
            }

            for (var block : blocks) {
                if (block == null) continue;
                this.list.add(block);
                this.dropSelf(block);
            }
        }

        public void registerLeavesAndSticks(Block leaves, Block sapling) {
            list.add(leaves);
            this.add(leaves, l_state -> createLeavesDrops(l_state, sapling, DEFAULT_SAPLING_DROP_RATES));
        }

        private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

        protected @NotNull Iterable<Block> getKnownBlocks() {
            return this.list;
        }
    }
}