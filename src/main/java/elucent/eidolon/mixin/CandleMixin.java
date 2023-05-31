package elucent.eidolon.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractCandleBlock.class)
public interface CandleMixin {

    @Invoker
    void callSetLit(LevelAccessor pLevel, BlockState pState, BlockPos pPos, boolean pLit);

}
