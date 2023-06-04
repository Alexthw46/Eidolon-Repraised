package elucent.eidolon.wip.reagent;

import elucent.eidolon.Eidolon;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class CrimsolReagent extends Reagent {
    public CrimsolReagent(ResourceLocation name) {
        super(name, new ResourceLocation(Eidolon.MODID, "block/crimsol"), false);
    }

    @Override
    public void worldEffect(Level world, BlockPos pos, int amount) {
        if (!world.isClientSide)
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1, 1);
    }
}
