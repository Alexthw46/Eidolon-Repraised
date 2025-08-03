package alexthw.eidolon_repraised.common.block;

import alexthw.eidolon_repraised.registries.Registry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class HerbBlockBase extends BushBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape[] SHAPES = new VoxelShape[]{Block.box(5, 0, 5, 11, 4, 11), Block.box(5, 0, 5, 11, 4, 11), Block.box(4, 0, 4, 12, 8, 12), Block.box(4, 0, 4, 12, 8, 12)};

    public HerbBlockBase(BlockBehaviour.Properties builder) {
        super(builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    public static MapCodec<HerbBlockBase> CODEC = simpleCodec(HerbBlockBase::new);

    @Override
    protected @NotNull MapCodec<? extends BushBlock> codec() {
        return CODEC;
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
    public @NotNull TriState canSustainPlant(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos soilPosition, @NotNull Direction facing, BlockState plant) {
        if (plant.getBlock() instanceof HerbBlockBase && state.is(Registry.PLANTER.get())) {
            return TriState.TRUE;
        }
        return TriState.DEFAULT;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return state.is(Registry.PLANTER.get());
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return canGrow(state);
    }

    @Override
    public void randomTick(@NotNull BlockState pState, @NotNull ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource random) {
        int i = this.getAge(pState);
        if (i < this.getMaxAge() && mayPlaceOn(worldIn.getBlockState(pos.below()), worldIn, pos.below())
                && net.neoforged.neoforge.common.CommonHooks.canCropGrow(worldIn, pos, pState, random.nextInt(20) == 0)) {
            growCrops(worldIn, pos, pState);
            net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(worldIn, pos, pState);
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
        int i = this.getAge(pState) + 1;
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        pLevel.setBlock(pPos, this.getStateForAge(i), 2);
    }

    private int getMaxAge() {
        return 3;
    }

    private boolean canGrow(BlockState pState) {
        return pState.getValue(AGE) < this.getMaxAge();
    }

    protected BlockState getStateForAge(int pAge) {
        return this.defaultBlockState().setValue(AGE, pAge);
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return this.canGrow(state);
    }

    public boolean isBonemealSuccess(@NotNull Level pLevel, @NotNull RandomSource pRandom, @NotNull BlockPos pPos, @NotNull BlockState pState) {
        return pRandom.nextFloat() < 0.35D;
    }

    public void performBonemeal(@NotNull ServerLevel pLevel, @NotNull RandomSource pRandom, @NotNull BlockPos pPos, @NotNull BlockState pState) {
        this.growCrops(pLevel, pPos, pState);
    }
}
