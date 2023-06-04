package elucent.eidolon.common.block;

import elucent.eidolon.common.tile.TileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockBase extends Block {
    VoxelShape shape = null;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockBase(Properties properties) {
        super(properties);
    }

    public BlockBase setShape(VoxelShape shape) {
        this.shape = shape;
        return this;
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
        return shape != null ? shape : Shapes.block();
    }

    @Override
    public void playerWillDestroy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        breakBlock(state, world, pos);
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        breakBlock(state, world, pos);
        super.onBlockExploded(state, world, pos, explosion);
    }

    public void breakBlock(BlockState state, Level world, BlockPos pos) {
        if (this instanceof EntityBlock) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof TileEntityBase tile) {
                tile.onDestroyed(state, pos);
            }
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
        if (this instanceof EntityBlock) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof TileEntityBase tile) {
                return tile.onActivated(state, pos, player, hand);
            }
        }
        return super.use(state, world, pos, player, hand, ray);
    }
}
