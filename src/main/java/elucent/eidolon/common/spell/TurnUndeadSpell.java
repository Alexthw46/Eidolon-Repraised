package elucent.eidolon.common.spell;

import elucent.eidolon.api.capability.IReputation;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.common.deity.Deities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TurnUndeadSpell extends StaticSpell {
    public TurnUndeadSpell(ResourceLocation name, Sign... signs) {
        super(name, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        IReputation reputation = player.getCapability(elucent.eidolon.registries.EidolonCapabilities.REPUTATION_CAPABILITY);
        double favor = reputation != null ? reputation.getReputation(Deities.LIGHT_DEITY.getId()) : 0;
        return favor > 30;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

    }
}
