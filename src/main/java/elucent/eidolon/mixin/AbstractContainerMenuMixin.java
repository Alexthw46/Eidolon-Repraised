package elucent.eidolon.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface AbstractContainerMenuMixin {
    @Accessor
    NonNullList<ItemStack> getLastSlots();

    @Accessor
    NonNullList<ItemStack> getRemoteSlots();

    @Invoker
    Slot callAddSlot(Slot slot);
}
