package elucent.eidolon.common.block;

import elucent.eidolon.common.tile.NecroticFocusTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class NecroticFocusBlock extends HorizontalWaterloggableBlock implements EntityBlock {
    final VoxelShape SOUTH = Shapes.box(0, 0, 0, 1, 1, 0.5);
    final VoxelShape NORTH = Shapes.box(0, 0, 0.5, 1, 1, 1);
    final VoxelShape WEST = Shapes.box(0.5, 0, 0, 1, 1, 1);
    final VoxelShape EAST = Shapes.box(0, 0, 0, 0.5, 1, 1);

    public NecroticFocusBlock(Properties properties) {
        super(properties);
    }

    VoxelShape shapeForState(BlockState state) {
        return switch (state.getValue(HORIZONTAL_FACING)) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> EAST;
        };
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return shapeForState(state);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return shapeForState(state);
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return shapeForState(state);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new NecroticFocusTileEntity(pos, state);
    }
}
