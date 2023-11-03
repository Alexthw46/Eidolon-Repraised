package elucent.eidolon.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import elucent.eidolon.registries.DecoBlockPack;
import elucent.eidolon.registries.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class ModLootTables extends LootTableProvider {

    public ModLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn.getPackOutput(), new HashSet<>(), List.of(new SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)));
    }

    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public static class BlockLootTable extends BlockLootSubProvider {
        public final List<Block> list = new ArrayList<>();

        protected BlockLootTable() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), new HashMap<>());
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> p_249322_) {
            this.generate();
            Set<ResourceLocation> set = new HashSet<>();

            for (Block block : list) {
                if (block.isEnabled(this.enabledFeatures)) {
                    ResourceLocation resourcelocation = block.getLootTable();
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