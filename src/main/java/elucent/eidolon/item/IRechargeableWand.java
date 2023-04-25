package elucent.eidolon.item;

import net.minecraft.world.item.ItemStack;

public interface IRechargeableWand {
    ItemStack recharge(ItemStack stack);
}
