package alexthw.eidolon_repraised.api.capability;

import alexthw.eidolon_repraised.network.ManaUpdatePacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface IMana {

    static void expendMana(Player player, int amount) {
        if (player.isCreative()) return;
        var mana = player.getCapability(EidolonCapabilities.MANA_CAPABILITY);

        if (mana == null) return;
        if (mana.getMagic() >= amount) {
            mana.takeMagic(amount);
            if (player instanceof ServerPlayer serverPlayer)
                Networking.sendToPlayerClient(new ManaUpdatePacket(player), serverPlayer);
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
