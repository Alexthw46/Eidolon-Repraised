package alexthw.eidolon_repraised.network;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.gui.WorktableContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorktableTransferPacket extends AbstractPacket {

    int containerId;
    int width;
    int height;
    List<ItemStack> coreStacks;
    List<ItemStack> outerStacks;

    public WorktableTransferPacket(int containerId, int width, int height, List<ItemStack> coreStacks, List<ItemStack> outerStacks) {
        this.containerId = containerId;
        this.width = width;
        this.height = height;
        this.coreStacks = coreStacks;
        this.outerStacks = outerStacks;
    }

    public static final Type<WorktableTransferPacket> TYPE = new Type<>(Eidolon.prefix("worktable_transfer_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WorktableTransferPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            (pkt) -> pkt.containerId,
            ByteBufCodecs.INT,
            (pkt) -> pkt.width,
            ByteBufCodecs.INT,
            (pkt) -> pkt.height,
            ItemStack.OPTIONAL_LIST_STREAM_CODEC,
            (pkt) -> pkt.coreStacks,
            ItemStack.OPTIONAL_LIST_STREAM_CODEC,
            (pkt) -> pkt.outerStacks,
            WorktableTransferPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        minecraftServer.execute(() -> {
            if (!(player.containerMenu instanceof WorktableContainer menu)) return;
            if (menu.containerId != containerId) return;

            // --- Fill core slots (1-9) ---
            // Need to respect width and height to keep the shape
            // Index of the list doesn't directly map to slot index
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int listIndex = row * width + col;
                    int slotIndex = 1 + row * 3 + col; // Slots 1-9 are the core slots
                    if (listIndex < coreStacks.size()) {
                        menu.getSlot(slotIndex).set(coreStacks.get(listIndex).copy());
                    }
                }
            }

            // --- Fill outer slots (10-13) ---
            for (int i = 0; i < outerStacks.size(); i++) {
                menu.getSlot(10 + i).set(outerStacks.get(i).copy());
            }

            // --- Remove items from player's inventory ---
            takeIngredientsFromPlayer(player, coreStacks);
            takeIngredientsFromPlayer(player, outerStacks);

            // Optional: update result
            menu.craftSlotsChanged();
        });
    }

    private static void takeIngredientsFromPlayer(ServerPlayer player, List<ItemStack> ingredients) {
        Inventory inv = player.getInventory();

        for (ItemStack required : ingredients) {
            if (required.isEmpty()) continue;

            int needed = required.getCount();

            for (int slot = 0; slot < inv.getContainerSize(); slot++) {
                ItemStack stack = inv.getItem(slot);
                if (ItemStack.isSameItem(stack, required)) {
                    int remove = Math.min(stack.getCount(), needed);
                    stack.shrink(remove);
                    needed -= remove;
                    if (stack.isEmpty()) inv.setItem(slot, ItemStack.EMPTY);
                    if (needed <= 0) break;
                }
            }
        }
    }

}
