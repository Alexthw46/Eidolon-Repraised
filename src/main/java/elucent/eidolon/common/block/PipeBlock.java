package elucent.eidolon.common.block;

import elucent.eidolon.common.tile.reagent.PipeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PipeBlock extends BlockBase implements EntityBlock {
    static final VoxelShape
        UP = Shapes.box(0.375, 0.625, 0.375, 0.625, 1, 0.625),
        DOWN = Shapes.box(0.375, 0, 0.375, 0.625, 0.375, 0.625),
        EAST = Shapes.box(0.625, 0.375, 0.375, 1, 0.625, 0.625),
        WEST = Shapes.box(0, 0.375, 0.375, 0.375, 0.625, 0.625),
        NORTH = Shapes.box(0.375, 0.375, 0, 0.625, 0.625, 0.375),
        SOUTH = Shapes.box(0.375, 0.375, 0.625, 0.625, 0.625, 1),
        CENTER = Shapes.box(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);

    static final VoxelShape[] SHAPES = new VoxelShape[36];

    static {
        VoxelShape[] FACES = new VoxelShape[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
        for (Direction in : Direction.values()) {
            for (Direction out : Direction.values()) {
                SHAPES[in.ordinal() * 6 + out.ordinal()] = Shapes.or(FACES[in.ordinal()], FACES[out.ordinal()], CENTER);
            }
        }
    }

    public static final DirectionProperty
            IN = DirectionProperty.create("in", direction -> true),
            OUT = DirectionProperty.create("out", direction -> true);
    public static final BooleanProperty
            IN_ATTACHED = BooleanProperty.create("attachin"),
            OUT_ATTACHED = BooleanProperty.create("attachout");

    public PipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return getInteractionShape(state, world, pos);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return getInteractionShape(state, world, pos);
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return SHAPES[state.getValue(IN).ordinal() * 6 + state.getValue(OUT).ordinal()];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
            .setValue(IN, context.getClickedFace().getOpposite())
            .setValue(OUT, context.getClickedFace())
            .setValue(IN_ATTACHED,
                context.getLevel()
                    .getBlockState(context.getClickedPos().relative(context.getClickedFace().getOpposite()))
                    .getBlock() == this)
            .setValue(OUT_ATTACHED,
                context.getLevel()
                    .getBlockState(context.getClickedPos().relative(context.getClickedFace()))
                    .getBlock() == this &&
                    context.getLevel().getBlockState(context.getClickedPos().relative(context.getClickedFace()))
                        .getValue(IN) == context.getClickedFace());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (facing == state.getValue(OUT))
            state = state.setValue(OUT_ATTACHED, facingState.getBlock() == this && facingState.getValue(IN) == facing.getOpposite());
        if (facing == state.getValue(IN))
            state = state.setValue(IN_ATTACHED, facingState.getBlock() == this && facingState.getValue(OUT) == facing.getOpposite());
        if (facingState.getBlock() == this && facingPos.relative(facingState.getValue(IN)).equals(pos)
            && facing != state.getValue(IN)) {
            state = state.setValue(OUT, facing).setValue(OUT_ATTACHED, true);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IN).add(OUT).add(IN_ATTACHED).add(OUT_ATTACHED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeTileEntity(pos, state);
    }
}
