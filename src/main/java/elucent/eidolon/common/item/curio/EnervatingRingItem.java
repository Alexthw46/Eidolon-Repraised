package elucent.eidolon.common.item.curio;

import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.EidolonAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class EnervatingRingItem extends BasicRingItem {
    public EnervatingRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slotContext, uuid, stack);
        map.put(EidolonAttributes.CHANTING_SPEED.get(), new AttributeModifier(uuid, Eidolon.MODID + ":enervating_ring", 0.5f, AttributeModifier.Operation.MULTIPLY_BASE));
        return map;
    }

}
