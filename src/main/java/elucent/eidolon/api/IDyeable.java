package elucent.eidolon.api;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public interface IDyeable {

    default void onDye(ItemStack stack, DyeColor color) {
        stack.getOrCreateTag().putInt("color", color.getId());
    }

}
