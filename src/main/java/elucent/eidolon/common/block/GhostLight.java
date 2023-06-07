package elucent.eidolon.common.block;

import elucent.eidolon.particle.Particles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static elucent.eidolon.registries.EidolonParticles.FLAME_PARTICLE;

public class GhostLight extends BlockBase {

    public static final BooleanProperty DEITY = BlockStateProperties.LIT;

    public GhostLight(Properties properties) {
        super(properties.noCollission().noLootTable());
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(DEITY, true));
    }

    protected static final VoxelShape SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public void animateTick(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        var vec = Vec3.atCenterOf(new Vec3i(pPos.getX(), pPos.getY(), pPos.getZ()));
        float r, g, b;
        if (pState.getValue(DEITY)) {
            r = 150 / 255F;
            g = 150 / 255F;
            b = 100 / 255F;
        } else {
            r = 0.7f;
            g = 0.25f;
            b = 1.0f;
        }
        Particles.create(FLAME_PARTICLE)
                .setColor(r, g, b)
                .setAlpha(0.5f, 0)
                .setScale(0.25f, 0.125f)
                .randomOffset(0.25f)
                .randomVelocity(0.0025f).addVelocity(0, 0.005f, 0)
                .repeat(pLevel, vec.x, vec.y, vec.z, 8);

    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.INVISIBLE;
    }


    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
        builder.add(DEITY);
    }
}
