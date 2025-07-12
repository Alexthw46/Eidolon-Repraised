package elucent.eidolon.api.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

public abstract class Spell {
    final ResourceLocation registryName;
    public @Nullable ModConfigSpec CONFIG;
    public @Nullable ModConfigSpec.ConfigValue<Integer> DELAY;

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

    public void buildConfig(ModConfigSpec.Builder spellBuilder) {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Spell spell) {
            return registryName.equals(spell.registryName);
        }
        return false;
    }
}
