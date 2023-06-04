package elucent.eidolon.common.item.curio;

import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class BasicRingItem extends EidolonCurio {
    final UUID ATTR_ID = new UUID(7207179027447911419L, 1628308750126455317L);

    public BasicRingItem(Properties properties) {
        super(properties);
    }


    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slotContext, uuid, stack);
        map.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ATTR_ID, Eidolon.MODID + ":basic_ring", 0.5f, AttributeModifier.Operation.ADDITION));
        return map;
    }

}
