package elucent.eidolon.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import elucent.eidolon.registries.DecoBlockPack;
import elucent.eidolon.registries.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModLootTables extends LootTableProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables;

    public ModLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        this.tables = ImmutableList.of(Pair.of(ModLootTables.BlockLootTable::new, LootContextParamSets.BLOCK));
    }

    protected @NotNull List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return this.tables;
    }

    protected void validate(Map<ResourceLocation, LootTable> map, @NotNull ValidationContext validationTracker) {
        map.forEach((resourceLocation, lootTable) -> LootTables.validate(validationTracker, resourceLocation, lootTable));
    }

    public @NotNull String getName() {
        return "Eidolon Loot Tables";
    }

    public static class BlockLootTable extends BlockLoot {
        public final List<Block> list = new ArrayList<>();

        public BlockLootTable() {
        }

        protected void addTables() {

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
            //createDoorTable(Registry.POLISHED_PLANKS.getDoor());
            //createDoorTable(Registry.ILLWOOD_PLANKS.getDoor());
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
                //blocks.add(woodDecoBlock.getTrapdoor());
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