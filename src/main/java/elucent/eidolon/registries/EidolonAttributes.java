package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EidolonAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Eidolon.MODID);

    public static final RegistryObject<Attribute> MAGICAL_KNOWLEDGE = ATTRIBUTES.register("magical_knowledge", () -> new RangedAttribute("attribute.eidolon.magical_knowledge", 1, 0, 10).setSyncable(true));

    public static float getMagicalKnowledge(final LivingEntity livingEntity) {
        if (livingEntity == null) {
            return 1;
        }

        return (float) livingEntity.getAttributeValue(MAGICAL_KNOWLEDGE.get());
    }

    public static float getMagicalDamage(final LivingEntity livingEntity, float baseValue) {
        return baseValue * getMagicalKnowledge(livingEntity);
    }

    @SubscribeEvent
    public static void setAttributes(final EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type -> event.add(type, MAGICAL_KNOWLEDGE.get()));
    }
}
