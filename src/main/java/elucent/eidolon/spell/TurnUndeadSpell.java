package elucent.eidolon.spell;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.deity.Deities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicReference;

public class TurnUndeadSpell extends StaticSpell {
    public TurnUndeadSpell(ResourceLocation name, Sign... signs) {
        super(name, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        AtomicReference<Double> favor = new AtomicReference<>((double) 0);
        world.getCapability(IReputation.INSTANCE).ifPresent(
                (reputation) -> favor.set(reputation.getReputation(player, Deities.LIGHT_DEITY.getId()))
        );
        return favor.get() > 30;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

    }
}
