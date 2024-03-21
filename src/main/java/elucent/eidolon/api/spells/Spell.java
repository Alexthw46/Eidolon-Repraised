package elucent.eidolon.api.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

public abstract class Spell {
    final ResourceLocation registryName;
    public @Nullable ForgeConfigSpec CONFIG;
    public @Nullable ForgeConfigSpec.ConfigValue<Integer> DELAY;

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

    public abstract int getCost();

    public int getDelay() {
        return DELAY == null ? 10 : DELAY.get();
    }

    public void buildConfig(ForgeConfigSpec.Builder spellBuilder) {
    }

}
