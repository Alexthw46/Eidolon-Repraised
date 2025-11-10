package alexthw.eidolon_repraised.common.item.curio;

import alexthw.eidolon_repraised.registries.Registry;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public class GravityBeltItem extends BasicBeltItem {

    public GravityBeltItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(GravityBeltItem::onFall);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (CuriosApi.getCuriosInventory(event.getEntity()).flatMap(i -> i.findFirstCurio(Registry.GRAVITY_BELT.get())).isPresent()) {
            event.setDistance(event.getDistance() / 4);
        }
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = super.getAttributeModifiers(slotContext, id, stack);
        map.put(Attributes.GRAVITY, new AttributeModifier(id, -0.60f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return map;
    }

}
