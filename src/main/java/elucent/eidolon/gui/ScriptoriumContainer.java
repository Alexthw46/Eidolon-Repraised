package elucent.eidolon.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ScriptoriumContainer extends AbstractContainerMenu {
    protected ScriptoriumContainer(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     *
     * @param pPlayer
     * @param pIndex
     */
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    /**
     * Determines whether supplied player can use this container
     *
     * @param pPlayer
     */
    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }
}
