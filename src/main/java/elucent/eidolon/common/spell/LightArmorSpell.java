package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LightArmorSpell extends StaticSpell {
    public LightArmorSpell(ResourceLocation name, Sign... signs) {
        super(name, 50, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

    }
}
