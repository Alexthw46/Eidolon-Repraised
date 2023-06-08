package elucent.eidolon.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import elucent.eidolon.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EnchantedAshBlock extends BlockBase {
    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
    public static final Map<Direction, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
    private static final VoxelShape BARRIER_SHAPE = Shapes.box(0, -4, 0, 1, 5, 1);
    private static final VoxelShape BASE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
    private static final Map<Direction, VoxelShape> SIDE_TO_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Direction.SOUTH, Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Direction.EAST, Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Direction.WEST, Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
    private static final Map<Direction, VoxelShape> SIDE_TO_ASCENDING_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Shapes.or(SIDE_TO_SHAPE.get(Direction.NORTH), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), Direction.SOUTH, Shapes.or(SIDE_TO_SHAPE.get(Direction.SOUTH), Block.box(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), Direction.EAST, Shapes.or(SIDE_TO_SHAPE.get(Direction.EAST), Block.box(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), Direction.WEST, Shapes.or(SIDE_TO_SHAPE.get(Direction.WEST), Block.box(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
    private final Map<BlockState, VoxelShape> stateToShapeMap = Maps.newHashMap();
    private final BlockState sideBaseState;

    public EnchantedAshBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, RedstoneSide.NONE).setValue(EAST, RedstoneSide.NONE).setValue(SOUTH, RedstoneSide.NONE).setValue(WEST, RedstoneSide.NONE));
        this.sideBaseState = this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE).setValue(EAST, RedstoneSide.SIDE).setValue(SOUTH, RedstoneSide.SIDE).setValue(WEST, RedstoneSide.SIDE);

        for(BlockState blockstate : this.getStateDefinition().getPossibleStates()) {
            this.stateToShapeMap.put(blockstate, this.getShapeForState(blockstate));
        }
    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelshape = BASE_SHAPE;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = state.getValue(FACING_PROPERTY_MAP.get(direction));
            if (redstoneside == RedstoneSide.SIDE) {
                voxelshape = Shapes.or(voxelshape, SIDE_TO_SHAPE.get(direction));
            } else if (redstoneside == RedstoneSide.UP) {
                voxelshape = Shapes.or(voxelshape, SIDE_TO_ASCENDING_SHAPE.get(direction));
            }
        }

        return voxelshape;
    }

    private static boolean areAllSidesInvalid(BlockState state) {
        return !state.getValue(NORTH).isConnected() && !state.getValue(SOUTH).isConnected() && !state.getValue(EAST).isConnected() && !state.getValue(WEST).isConnected();
    }

    private static boolean areAllSidesValid(BlockState state) {
        return state.getValue(NORTH).isConnected() && state.getValue(SOUTH).isConnected() && state.getValue(EAST).isConnected() && state.getValue(WEST).isConnected();
    }

    private BlockState recalculateFacingState(BlockGetter reader, BlockState state, BlockPos pos) {
        boolean flag = !reader.getBlockState(pos.above()).isRedstoneConductor(reader, pos);

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (!state.getValue(FACING_PROPERTY_MAP.get(direction)).isConnected()) {
                RedstoneSide redstoneside = this.recalculateSide(reader, pos, direction, flag);
                state = state.setValue(FACING_PROPERTY_MAP.get(direction), redstoneside);
            }
        }

        return state;
    }

    private RedstoneSide getSide(BlockGetter worldIn, BlockPos pos, Direction face) {
        RedStoneWireBlock r;
        return this.recalculateSide(worldIn, pos, face, !worldIn.getBlockState(pos.above()).isRedstoneConductor(worldIn, pos));
    }

    public void updateIndirectNeighbourShapes(@NotNull BlockState state, @NotNull LevelAccessor worldIn, @NotNull BlockPos pos, int flags, int recursionLeft) {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = state.getValue(FACING_PROPERTY_MAP.get(direction));
            if (redstoneside != RedstoneSide.NONE && !worldIn.getBlockState(blockpos$mutable.setWithOffset(pos, direction)).is(this)) {
                blockpos$mutable.move(Direction.DOWN);
                BlockState blockstate = worldIn.getBlockState(blockpos$mutable);
                if (!blockstate.is(Blocks.OBSERVER)) {
                    BlockPos blockpos = blockpos$mutable.relative(direction.getOpposite());
                    BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), worldIn.getBlockState(blockpos), worldIn, blockpos$mutable, blockpos);
                    updateOrDestroy(blockstate, blockstate1, worldIn, blockpos$mutable, flags, recursionLeft);
                }

                blockpos$mutable.setWithOffset(pos, direction).move(Direction.UP);
                BlockState blockstate3 = worldIn.getBlockState(blockpos$mutable);
                if (!blockstate3.is(Blocks.OBSERVER)) {
                    BlockPos blockpos1 = blockpos$mutable.relative(direction.getOpposite());
                    BlockState blockstate2 = blockstate3.updateShape(direction.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, blockpos$mutable, blockpos1);
                    updateOrDestroy(blockstate3, blockstate2, worldIn, blockpos$mutable, flags, recursionLeft);
                }
            }
        }

    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (facing == Direction.DOWN) {
            return stateIn;
        } else if (facing == Direction.UP) {
            return this.getUpdatedState(worldIn, stateIn, currentPos);
        } else {
            RedstoneSide redstoneside = this.getSide(worldIn, currentPos, facing);
            return redstoneside.isConnected() == stateIn.getValue(FACING_PROPERTY_MAP.get(facing)).isConnected() && !areAllSidesValid(stateIn) ? stateIn.setValue(FACING_PROPERTY_MAP.get(facing), redstoneside) : this.getUpdatedState(worldIn, this.sideBaseState.setValue(FACING_PROPERTY_MAP.get(facing), redstoneside), currentPos);
        }
    }

    private RedstoneSide recalculateSide(BlockGetter reader, BlockPos pos, Direction direction, boolean nonNormalCubeAbove) {
        BlockPos blockpos = pos.relative(direction);
        BlockState blockstate = reader.getBlockState(blockpos);
        if (nonNormalCubeAbove) {
            boolean flag = this.canPlaceOnTopOf(reader, blockpos, blockstate);
            if (flag && canConnectTo(reader.getBlockState(blockpos.above()), reader, blockpos.above(), null) ) {
                if (blockstate.isFaceSturdy(reader, blockpos, direction.getOpposite())) {
                    return RedstoneSide.UP;
                }

                return RedstoneSide.SIDE;
            }
        }

        return !canConnectTo(blockstate, reader, blockpos, direction) && (blockstate.isRedstoneConductor(reader, blockpos) || !canConnectTo(reader.getBlockState(blockpos.below()), reader, blockpos.below(), null)) ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    private boolean canPlaceOnTopOf(BlockGetter reader, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(reader, pos, Direction.UP);
    }

    private BlockState getUpdatedState(BlockGetter reader, BlockState state, BlockPos pos) {
        boolean flag = areAllSidesInvalid(state);
        state = this.recalculateFacingState(reader, this.defaultBlockState(), pos);
        if (flag && areAllSidesInvalid(state)) {
            return state;
        } else {
            boolean flag1 = state.getValue(NORTH).isConnected();
            boolean flag2 = state.getValue(SOUTH).isConnected();
            boolean flag3 = state.getValue(EAST).isConnected();
            boolean flag4 = state.getValue(WEST).isConnected();
            boolean flag5 = !flag1 && !flag2;
            boolean flag6 = !flag3 && !flag4;
            if (!flag4 && flag5) {
                state = state.setValue(WEST, RedstoneSide.SIDE);
            }

            if (!flag3 && flag5) {
                state = state.setValue(EAST, RedstoneSide.SIDE);
            }

            if (!flag1 && flag6) {
                state = state.setValue(NORTH, RedstoneSide.SIDE);
            }

            if (!flag2 && flag6) {
                state = state.setValue(SOUTH, RedstoneSide.SIDE);
            }

            return state;
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.stateToShapeMap.get(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getUpdatedState(context.getLevel(), this.sideBaseState, context.getClickedPos());
    }

    @Override
    public void onPlace(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock()) && !worldIn.isClientSide) {
            for (Direction direction : Direction.Plane.VERTICAL) {
                worldIn.updateNeighborsAt(pos.relative(direction), this);
            }

            this.updateNeighboursStateChange(worldIn, pos);
        }
    }

    @Override
    public boolean collisionExtendsVertically(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return entity instanceof LivingEntity && ((LivingEntity)entity).isInvertedHealAndHarm();
    }

    boolean isBlocked(Entity entity) {
        if (entity == null) return false;
        if (entity instanceof LivingEntity living) {
            if (living.isInvertedHealAndHarm()) return true;
        }
        return entity.getPassengers().stream().anyMatch((e) -> e instanceof LivingEntity && ((LivingEntity) e).isInvertedHealAndHarm());
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return ctx instanceof EntityCollisionContext
               && ((EntityCollisionContext) ctx).getEntity() != null
               && isBlocked(((EntityCollisionContext) ctx).getEntity()) ? BARRIER_SHAPE : super.getCollisionShape(state, world, pos, ctx);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return this.canPlaceOnTopOf(worldIn, blockpos, blockstate);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!isMoving && !state.is(newState.getBlock())) {
            super.onRemove(state, worldIn, pos, newState, isMoving);
            if (!worldIn.isClientSide) {
                for (Direction direction : Direction.values()) {
                    worldIn.updateNeighborsAt(pos.relative(direction), this);
                }

                this.updateNeighboursStateChange(worldIn, pos);
            }
        }
    }

    private void updateChangedConnections(Level world, BlockPos pos, BlockState prevState, BlockState newState) {
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.relative(direction);
            if (prevState.getValue(FACING_PROPERTY_MAP.get(direction)).isConnected() != newState.getValue(FACING_PROPERTY_MAP.get(direction)).isConnected() && world.getBlockState(blockpos).isRedstoneConductor(world, blockpos)) {
                world.updateNeighborsAtExceptFromFacing(blockpos, newState.getBlock(), direction.getOpposite());
            }
        }

    }

    private void notifyWireNeighborsOfStateChange(Level worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).is(this)) {
            worldIn.updateNeighborsAt(pos, this);

            for(Direction direction : Direction.values()) {
                worldIn.updateNeighborsAt(pos.relative(direction), this);
            }
        }
    }

    private void updateNeighboursStateChange(Level world, BlockPos pos) {
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(world, pos.relative(direction));
        }

        for(Direction direction1 : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.relative(direction1);
            if (world.getBlockState(blockpos).isRedstoneConductor(world, blockpos)) {
                this.notifyWireNeighborsOfStateChange(world, blockpos.above());
            } else {
                this.notifyWireNeighborsOfStateChange(world, blockpos.below());
            }
        }

    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isClientSide) {
            if (!state.canSurvive(worldIn, pos)) {
                dropResources(state, worldIn, pos);
                worldIn.removeBlock(pos, false);
            }
        }
    }

    protected static boolean canConnectTo(BlockState blockState, BlockGetter world, BlockPos pos, Direction side) {
        return blockState.is(Registry.ENCHANTED_ASH.get());
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean isSignalSource(@NotNull BlockState state) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }
}
