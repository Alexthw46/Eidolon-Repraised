package elucent.eidolon.api.capability;

import com.hollingsworth.arsnouveau.common.capability.ManaData;
import elucent.eidolon.network.Networking;
import elucent.eidolon.network.SoulUpdatePacket;
import elucent.eidolon.registries.EidolonCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IMana {

    static void expendMana(Player player, int amount) {
        if (player.isCreative()) return;
        var mana = player.getCapability(EidolonCapabilities.MANA_CAPABILITY);

        if (mana == null) return;
        if (mana.getMagic() >= amount) {
            mana.takeMagic(amount);
            if (!player.level().isClientSide)
                Networking.sendToTracking(player.level(), player.getOnPos(), new SoulUpdatePacket(player));
        }
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
}
