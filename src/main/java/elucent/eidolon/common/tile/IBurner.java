package elucent.eidolon.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IBurner {

    void startBurning(Player player, Level world, BlockPos pos);

    boolean canStartBurning();

}

