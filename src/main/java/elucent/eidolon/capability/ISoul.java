package elucent.eidolon.capability;

import elucent.eidolon.Registry;
import elucent.eidolon.network.Networking;
import elucent.eidolon.network.SoulUpdatePacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public interface ISoul {
    Capability<ISoul> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        final SoulImpl impl = new SoulImpl();

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            if (cap == INSTANCE) return (LazyOptional<T>) LazyOptional.of(() -> impl);
            else return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return impl.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            impl.deserializeNBT(nbt);
        }
    }

    boolean hasEtherealHealth();

    float getMaxEtherealHealth();

    float getEtherealHealth();

    void setEtherealHealth(float health);

    void setMaxEtherealHealth(float max);

    default float hurtEtherealHealth(float amount, float persistentHealth) {
        amount = Math.max(0, amount);
        float oldHealth = getEtherealHealth();
        setMaxEtherealHealth(Math.max(getMaxEtherealHealth() - amount, Math.min(persistentHealth, getMaxEtherealHealth())));
        setEtherealHealth(oldHealth - amount);
        return Math.max(0, amount - oldHealth);
    }

    default void healEtherealHealth(float amount, float persistentHealth) {
        amount = Math.max(0, amount);
        setEtherealHealth(Math.min(Math.max(getEtherealHealth(), persistentHealth), getEtherealHealth() + amount));
    }

    boolean hasMagic();

    float getMaxMagic();

    float getMagic();

    void setMagic(float magic);

    void setMaxMagic(float max);

    default void takeMagic(float amount) {
        amount = Math.max(0, amount);
        setMagic(getMagic() - amount);
    }

    default void giveMagic(float amount) {
        amount = Math.max(0, amount);
        setMagic(getMagic() + amount);
    }

    static float getPersistentHealth(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Registry.PERSISTENT_SOUL_HEARTS.get());
        if (attr != null) return (float) attr.getValue();
        else return 0;
    }

    static void expendMana(Player player, int amount) {
        if (player.isCreative()) return;
        player.getCapability(INSTANCE).ifPresent(soul -> {
            if (soul.getMagic() >= amount) {
                soul.takeMagic(amount);
            }
            if (!player.level.isClientSide)
                Networking.sendToTracking(player.level, player.getOnPos(), new SoulUpdatePacket(player));
        });
    }
}
