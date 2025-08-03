package alexthw.eidolon_repraised.common.item.curio;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class BasicBeltItem extends EidolonCurio {
    public BasicBeltItem(Properties properties) {
        super(properties);
    }


    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = super.getAttributeModifiers(slotContext, id, stack);
        map.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, 1.0f, AttributeModifier.Operation.ADD_VALUE));
        return map;
    }

}
