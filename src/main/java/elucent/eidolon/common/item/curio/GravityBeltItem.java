package elucent.eidolon.common.item.curio;

import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.Registry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class GravityBeltItem extends BasicBeltItem {
    final UUID ATTR_ID = new UUID(6937061617091731127L, 7120126291930051139L);

    public GravityBeltItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.addListener(GravityBeltItem::onFall);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), Registry.GRAVITY_BELT.get()).isPresent()) {
            event.setDistance(event.getDistance() / 4);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slotContext, uuid, stack);
        map.put(ForgeMod.ENTITY_GRAVITY.get(), new AttributeModifier(ATTR_ID, Eidolon.MODID + ":gravity_belt", -0.60f, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return map;
    }

}
