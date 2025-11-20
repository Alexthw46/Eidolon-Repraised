package alexthw.eidolon_repraised.common.block;

import alexthw.eidolon_repraised.common.tile.ScriptoriumTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class Scriptorium extends HorizontalBlockBase implements EntityBlock {
    public Scriptorium(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack pStack, @NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        } else {
            if (worldIn.getBlockEntity(pos) instanceof ScriptoriumTile tile) {
                player.openMenu(new SimpleMenuProvider(tile, Component.translatable("eidolon_repraised.gui.scriptorium")), pos);
            }
            return ItemInteractionResult.CONSUME;
        }
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ScriptoriumTile(pos, state);
    }

}
