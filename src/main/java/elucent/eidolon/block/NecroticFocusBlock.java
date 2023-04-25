package elucent.eidolon.block;

import net.minecraft.world.level.block.state.BlockState;
import elucent.eidolon.tile.NecroticFocusTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return shapeForState(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return shapeForState(state);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return shapeForState(state);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NecroticFocusTileEntity(pos, state);
    }
}
