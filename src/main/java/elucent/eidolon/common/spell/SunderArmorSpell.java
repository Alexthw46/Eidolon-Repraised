package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class SunderArmorSpell extends StaticSpell {
    public SunderArmorSpell(ResourceLocation name, Sign... signs) {
        super(name, 50, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        return rayTrace(player, player.getBlockReach(), 0, true) instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

    }
}
