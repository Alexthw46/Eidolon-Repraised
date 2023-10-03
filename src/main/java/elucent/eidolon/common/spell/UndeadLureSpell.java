package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class UndeadLureSpell extends StaticSpell {
    public UndeadLureSpell(ResourceLocation name, Sign... signs) {
        super(name, 50, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getBlockReach(), 0, true);
        return ray instanceof BlockHitResult;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

    }


}
