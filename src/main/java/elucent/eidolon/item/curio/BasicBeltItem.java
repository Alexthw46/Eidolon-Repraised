package elucent.eidolon.item.curio;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import elucent.eidolon.item.ItemBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.UUID;

public class BasicBeltItem extends ItemBase {
    final UUID ATTR_ID = new UUID(5892388994722937059L, 8235504439637777033L);

    public BasicBeltItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
        return new EidolonCurio(stack) {
            @Override
            public Multimap<Attribute, AttributeModifier> getAttributeModifiers(String identifier) {
                Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
                map.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ATTR_ID, Eidolon.MODID + ":basic_belt", 1.0f, AttributeModifier.Operation.ADDITION));
                return map;
            }

            @Override
            public boolean canRightClickEquip() {
                return true;
            }
        };
    }
}
