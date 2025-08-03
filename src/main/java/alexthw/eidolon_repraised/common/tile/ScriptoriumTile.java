package alexthw.eidolon_repraised.common.tile;

import alexthw.eidolon_repraised.gui.ScriptoriumContainer;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScriptoriumTile extends TileEntityBase implements MenuProvider {

    public ScriptoriumTile(BlockPos pos, BlockState state) {
        super(Registry.SCRIPTORIUM_TILE.get(), pos, state);
    }


    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new ScriptoriumContainer(pContainerId, pPlayerInventory, ContainerLevelAccess.create(pPlayer.level(), worldPosition));
    }

}
