package elucent.eidolon.common.item.curio;

import elucent.eidolon.common.item.ItemBase;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EidolonCurio extends ItemBase implements ICurioItem {

    public EidolonCurio(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
