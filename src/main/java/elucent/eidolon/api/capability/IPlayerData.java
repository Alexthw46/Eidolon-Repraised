package elucent.eidolon.api.capability;

import elucent.eidolon.common.item.IWingsItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public interface IPlayerData {

    default ItemStack getWingsItem(Player player) {
        ItemStack[] result = new ItemStack[]{ItemStack.EMPTY};
        CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent((h) -> {
            for (int i = 0; i < h.getSlots(); i ++) {
                ItemStack s = h.getStackInSlot(i);
                if (s.getItem() instanceof IWingsItem) {
                    result[0] = s;
                    break;
                }
            }
        });
        return result[0];
    }

    default int getMaxWingCharges(Player player) {
        ItemStack wings = getWingsItem(player);
        if (wings.getItem() instanceof IWingsItem i) return i.getMaxCharges(wings);
        return 0;
    }

    default boolean isDashing(Player player) {
        return getDashTicks(player) > 0;
    }

    default boolean canFlap(Player player) {
        return !player.onGround() && !player.isInPowderSnow && !player.isSwimming() && !player.isPassenger() && !player.getAbilities().flying;
    }

    int getDashTicks(Player player);

    void doDashTick(Player player);

    boolean tryDash(Player player);
    int getWingCharges(Player player);
    void rechargeWings(Player player);
    boolean tryFlapWings(Player player);
    long getFlightStartTime(Player player);
    long getLastFlapTime(Player player);
    boolean isFlying(Player player);
    void startFlying(Player player);
    void stopFlying(Player player);
    void setDashTicks(int ticks);
    void setLastFlapTime(long lastFlapTime);
}
