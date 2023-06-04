package elucent.eidolon.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class TableBlockBase extends BlockBase implements SimpleWaterloggedBlock {
    VoxelShape NORMAL = Shapes.box(0, 0.75, 0, 1, 1, 1),
            CORNER = Shapes.joinUnoptimized(
                    NORMAL,
                    Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.75, 0.9375),
                    BooleanOp.OR
            );

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty NX = BooleanProperty.create("nx");
    public static final BooleanProperty PX = BooleanProperty.create("px");
    public static final BooleanProperty NZ = BooleanProperty.create("nz");
    public static final BooleanProperty PZ = BooleanProperty.create("pz");

    public TableBlockBase(Properties properties) {
        super(properties);
        registerDefaultState(super.defaultBlockState()
                .setValue(NX, false)
                .setValue(PX, false)
                .setValue(NZ, false)
                .setValue(PZ, false));
    }

    public TableBlockBase setMainShape(VoxelShape shape) {
        NORMAL = shape;
        CORNER = Shapes.joinUnoptimized(
            NORMAL,
            Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.75, 0.9375),
            BooleanOp.OR
        );
        return this;
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        boolean nx = state.getValue(NX), px = state.getValue(PX), nz = state.getValue(NZ), pz = state.getValue(PZ);
        if ((!nx && !nz) || (!nx && !pz) || (!px && !pz) || (!px && !nz)) return CORNER;
        return NORMAL;
    }

    protected BlockState updateCorners(BlockGetter world, BlockPos pos, BlockState state) {
        BlockState blockstate = world.getBlockState(pos.north());
        BlockState blockstate1 = world.getBlockState(pos.east());
        BlockState blockstate2 = world.getBlockState(pos.south());
        BlockState blockstate3 = world.getBlockState(pos.west());
        boolean conn1 = blockstate.getBlock() == this,
            conn2 = blockstate1.getBlock() == this,
            conn3 = blockstate2.getBlock() == this,
            conn4 = blockstate3.getBlock() == this;
        return state
            .setValue(NZ, conn1).setValue(PX, conn2)
            .setValue(PZ, conn3).setValue(NX, conn4);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return updateCorners(iblockreader, blockpos, super.getStateForPlacement(context)).setValue(WATERLOGGED, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return updateCorners(world, pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NX, PX, NZ, PZ, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }
}
