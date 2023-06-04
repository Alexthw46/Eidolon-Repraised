package elucent.eidolon.common.item.curio;

import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class BasicBeltItem extends EidolonCurio {
    final UUID ATTR_ID = new UUID(5892388994722937059L, 8235504439637777033L);

    public BasicBeltItem(Properties properties) {
        super(properties);
    }


    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slotContext, uuid, stack);
        map.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ATTR_ID, Eidolon.MODID + ":basic_belt", 1.0f, AttributeModifier.Operation.ADDITION));
        return map;
    }

}
