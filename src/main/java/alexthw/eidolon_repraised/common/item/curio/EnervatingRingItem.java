package alexthw.eidolon_repraised.common.item.curio;

import alexthw.eidolon_repraised.registries.EidolonAttributes;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class EnervatingRingItem extends BasicRingItem {
    public EnervatingRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = super.getAttributeModifiers(slotContext, id, stack);
        map.put(EidolonAttributes.CHANTING_SPEED, new AttributeModifier(id, 0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return map;
    }

}
