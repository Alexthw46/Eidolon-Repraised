package elucent.eidolon.common.block;

import net.minecraft.core.Direction;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class Scriptorium extends HorizontalBlockBase {
    public Scriptorium(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

}
