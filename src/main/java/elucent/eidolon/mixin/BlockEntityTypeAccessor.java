package elucent.eidolon.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAccessor {

    @Mutable
    @Final
    @Accessor("validBlocks")
    void setValidBlocks(Set<Block> validBlocks);

    @Accessor("validBlocks")
    Set<Block> getValidBlocks();
}