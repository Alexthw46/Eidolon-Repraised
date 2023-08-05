package elucent.eidolon.common.world;

import com.mojang.serialization.Codec;
import elucent.eidolon.registries.Worldgen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

public class EidolonAbstractTreeFeature extends Feature<TreeConfiguration> {

    private static final ResourceLocation ILLWOOD_TREE1 = new ResourceLocation("eidolon:illwood_tree1");
    private static final ResourceLocation ILLWOOD_TREE2 = new ResourceLocation("eidolon:illwood_tree2");
    private static final ResourceLocation ILLWOOD_TREE3 = new ResourceLocation("eidolon:illwood_tree3");
    private static final ResourceLocation[] ILLWOOD_TREE = new ResourceLocation[]{ILLWOOD_TREE1, ILLWOOD_TREE2, ILLWOOD_TREE3};

    public EidolonAbstractTreeFeature(Codec<TreeConfiguration> codec) {
        super(codec);
    }

    public static boolean isAirOrLeavesAt(LevelSimulatedReader reader, BlockPos pos) {
        return reader.isStateAtPosition(pos, (state) -> {
            return state.isAir() || state.is(BlockTags.LEAVES);
        });
    }

    public static boolean isAirOrLeavesOrLogsAt(LevelSimulatedReader reader, BlockPos pos) {
        return reader.isStateAtPosition(pos, (state) -> state.isAir() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS));
    }

    private static boolean isDirtOrFarmlandAt(LevelSimulatedReader reader, BlockPos pos) {
        return reader.isStateAtPosition(pos, (state) -> {
            Block block = state.getBlock();
            return isDirt(state) || block == Blocks.FARMLAND;
        });
    }

    //    (WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, TreeConfiguration config)
    @Override
    public boolean place(FeaturePlaceContext<TreeConfiguration> context) {
        WorldGenLevel reader = context.level();
        TreeConfiguration config = context.config();
        BlockPos pos = context.origin();
        RandomSource rand = context.random();


        int i = rand.nextInt(ILLWOOD_TREE.length);

        if (!isDirtOrFarmlandAt(reader, pos.below()))
            return false;

        for (int j = 0; j < 8; j++) {

            BlockPos upPos = new BlockPos(pos).above();
            for (int k = 0; k < j; k++) {
                upPos = upPos.above();
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos)) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.north())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.south())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.east())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.east().north())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.east().south())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.west())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.west().north())) {
                return false;
            }
            if (!isAirOrLeavesOrLogsAt(reader, upPos.west().south())) {
                return false;
            }
        }


        if (isAirOrLeavesOrLogsAt(reader, pos.below().north()))
            return false;
        if (isAirOrLeavesOrLogsAt(reader, pos.below().south()))
            return false;
        if (isAirOrLeavesOrLogsAt(reader, pos.below().east()))
            return false;
        if (isAirOrLeavesOrLogsAt(reader, pos.below().west()))
            return false;

        BlockRotProcessor BlockRotProcessor = new BlockRotProcessor(0.9F);

        StructureTemplateManager templatemanager = reader.getLevel().getServer().getStructureManager();
        StructureTemplate template = templatemanager.getOrCreate(ILLWOOD_TREE[i]);

        Rotation rotation = Rotation.getRandom(rand);

        // For proper offsetting the feature, so it rotate properly around position parameter.
        BlockPos halfLengths = new BlockPos(
                template.getSize().getX() / 2,
                template.getSize().getY() / 2,
                template.getSize().getZ() / 2);

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos().set(pos);

        StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setRotation(rotation).setRotationPivot(halfLengths).setIgnoreEntities(false);

        BlockPos pos1 = mutable.set(pos).move(-halfLengths.getX(), 0, -halfLengths.getZ());
        template.placeInWorld(reader, pos1, pos1, placementsettings, rand, 2);

        return true;
    }

    public static class TreeGrower extends AbstractTreeGrower {
        @Override
        protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource p_60014_, boolean p_60015_) {
            return Worldgen.ILLWOOD_TREE_CFG;
        }
    }

}