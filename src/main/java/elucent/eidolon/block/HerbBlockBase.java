package elucent.eidolon.block;

import elucent.eidolon.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class HerbBlockBase extends BushBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
    private static final VoxelShape[] SHAPES = new VoxelShape[]{Block.box(5, 0, 5, 11, 4, 11), Block.box(4, 0, 4, 12, 8, 12)};

    public HerbBlockBase(BlockBehaviour.Properties builder) {
        super(builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES[state.getValue(AGE)];
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean canSustainPlant(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull Direction facing, @NotNull IPlantable plantable) {
        return state.is(Registry.PLANTER.get());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return state.is(Registry.PLANTER.get());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 1;
    }

    @Override
    public void randomTick(@NotNull BlockState pState, @NotNull ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource random) {
        int i = this.getAge(pState);
        if (i < this.getMaxAge() && mayPlaceOn(worldIn.getBlockState(pos.below()), worldIn, pos.below())
            && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, pState, random.nextInt(20) == 0)) {
            growCrops(worldIn, pos, pState);
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, pState);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    protected int getAge(BlockState pState) {
        return pState.getValue(AGE);
    }

    public void growCrops(Level pLevel, BlockPos pPos, BlockState pState) {
        int i = this.getAge(pState) + this.getBonemealAgeIncrease(pLevel);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        pLevel.setBlock(pPos, this.getStateForAge(i), 2);
    }

    private int getMaxAge() {
        return 1;
    }

    private boolean isMaxAge(BlockState pState) {
        return pState.getValue(AGE) >= this.getMaxAge();
    }

    protected BlockState getStateForAge(int pAge) {
        return this.defaultBlockState().setValue(AGE, pAge);
    }

    protected int getBonemealAgeIncrease(Level pLevel) {
        return Mth.nextInt(pLevel.random, 2, 5);
    }


    public boolean isValidBonemealTarget(@NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, boolean pIsClient) {
        return !this.isMaxAge(pState);
    }

    public boolean isBonemealSuccess(@NotNull Level pLevel, @NotNull RandomSource pRandom, @NotNull BlockPos pPos, @NotNull BlockState pState) {
        return true;
    }

    public void performBonemeal(@NotNull ServerLevel pLevel, @NotNull RandomSource pRandom, @NotNull BlockPos pPos, @NotNull BlockState pState) {
        this.growCrops(pLevel, pPos, pState);
    }
}
