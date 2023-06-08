package elucent.eidolon.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CandlestickBlock extends BlockBase {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", (d) -> d != Direction.DOWN);
    protected static final VoxelShape UP_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 14.0D, 10.0D);
    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.box(5.5D, 3.0D, 11.0D, 10.5D, 16.0D, 16.0D),
            Block.box(5.5D, 3.0D, 0.0D, 10.5D, 16.0D, 5.0D),
            Block.box(11.0D, 3.0D, 5.5D, 16.0D, 16.0D, 10.5D),
            Block.box(0.0D, 3.0D, 5.5D, 5.0D, 16.0D, 10.5D)
    };

    public CandlestickBlock(Properties properties) {
        super(properties);
    }

    public static VoxelShape getShapeForState(BlockState state) {
        Direction dir = state.getValue(FACING);
        if (dir == Direction.UP) return UP_SHAPE;
        else return SHAPES[dir.ordinal() - 2];
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShapeForState(state);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.isFaceSturdy(worldIn, blockpos, direction);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        Direction direction = state.getValue(FACING);
        if (direction == Direction.UP)
            return facing == Direction.DOWN && !this.canSurvive(state, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, worldIn, currentPos, facingPos);
        else
            return facing.getOpposite() == state.getValue(FACING) && !state.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader iworldreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction[] adirection = context.getNearestLookingDirections();

        for(Direction direction : adirection) {
            if (direction != Direction.UP) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (blockstate.canSurvive(iworldreader, blockpos)) {
                    return blockstate;
                }
            }
        }

        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level worldIn, BlockPos pos, Random rand) {
        Direction direction = state.getValue(FACING);
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY() + 0.925D;
        double d2 = (double) pos.getZ() + 0.5D;
        if (direction != Direction.UP) {
            d0 -= 0.3 * direction.getStepX();
            d1 += 0.125;
            d2 -= 0.3 * direction.getStepZ();
        }
        worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, @NotNull Rotation rot) {
        return state.getValue(FACING) != Direction.UP ? state.setValue(FACING, rot.rotate(state.getValue(FACING))) : state;
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, @NotNull Mirror mirrorIn) {
        return state.getValue(FACING) != Direction.UP ? state.rotate(mirrorIn.getRotation(state.getValue(FACING))) : state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
