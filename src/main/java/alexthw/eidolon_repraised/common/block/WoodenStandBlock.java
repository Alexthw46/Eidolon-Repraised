package alexthw.eidolon_repraised.common.block;

import alexthw.eidolon_repraised.common.tile.WoodenStandTileEntity;
import alexthw.eidolon_repraised.gui.WoodenBrewingStandContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class WoodenStandBlock extends BrewingStandBlock {
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D));

    public WoodenStandBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new WoodenStandTileEntity(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof WoodenStandTileEntity standTile) {
                player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new WoodenBrewingStandContainer(id, inventory, standTile, standTile.dataAccess), standTile.getDisplayName()), pos);
                player.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }

            return InteractionResult.CONSUME;
        }
    }

//    @Override
//    public void setPlacedBy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull LivingEntity placer, ItemStack stack) {
//        if (stack.hasCustomHoverName()) {
//            BlockEntity tileentity = worldIn.getBlockEntity(pos);
//            if (tileentity instanceof WoodenStandTileEntity) {
//                ((WoodenStandTileEntity) tileentity).setCustomName(stack.getHoverName());
//            }
//        }
//    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        // no particles with this one
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof WoodenStandTileEntity) {
                Containers.dropContents(worldIn, pos, (WoodenStandTileEntity) tileentity);
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return (level1, pos, state1, tile) -> ((WoodenStandTileEntity) tile).tick();
    }
}
