package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.capability.IReputation;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.deity.Deities;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
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
        IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
        double favor = reputation != null ? reputation.getReputation(Deities.LIGHT_DEITY.getId()) : 0;
        return favor > 30;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

    }
}
