package elucent.eidolon.api;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public interface IDyeable {

    default void onDye(ItemStack stack, DyeColor color) {
        stack.set(DataComponents.BASE_COLOR, color);
    }

}
