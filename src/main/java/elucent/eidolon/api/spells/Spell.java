package elucent.eidolon.api.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class Spell {
    final ResourceLocation registryName;

    public Spell(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public abstract boolean matches(SignSequence signs);

    public abstract boolean canCast(Level world, BlockPos pos, Player player, SignSequence signs);

    public abstract void cast(Level world, BlockPos pos, Player player, SignSequence signs);

    public abstract void setSigns(SignSequence signs);

    public int getCost() {
        return 0;
    }

    public int getDelay() {
        return 10;
    }

}
