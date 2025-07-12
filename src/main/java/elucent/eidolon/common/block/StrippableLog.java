package elucent.eidolon.common.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StrippableLog extends RotatedPillarBlock {
    final Supplier<Block> strippedState;

    public StrippableLog(Properties properties, Supplier<Block> stateSupplier) {
        super(properties);
        this.strippedState = stateSupplier;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(@NotNull BlockState state, @NotNull UseOnContext context, @NotNull ItemAbility itemAbility, boolean simulate) {
        return itemAbility == ItemAbilities.AXE_STRIP ? strippedState.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)) : super.getToolModifiedState(state, context, itemAbility, simulate);
    }


}