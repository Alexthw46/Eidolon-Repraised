package elucent.eidolon.common.item.curio;

import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class PrestigiousPalmItem extends EidolonCurio {
    final UUID ATTR_ID = new UUID(297661999713141389L, 6434109711109552363L);

    public PrestigiousPalmItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slotContext, uuid, stack);
        map.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ATTR_ID, Eidolon.MODID + ":prestigious_palm", 4.0f, AttributeModifier.Operation.ADDITION));
        return map;
    }

}
