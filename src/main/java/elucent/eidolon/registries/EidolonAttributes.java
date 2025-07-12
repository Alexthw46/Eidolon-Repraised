package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.common.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class EidolonAttributes {
    static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Eidolon.MODID);
    public static final DeferredHolder<Attribute, Attribute> CHANTING_SPEED = ATTRIBUTES.register("chanting_speed", () -> new RangedAttribute(Eidolon.MODID + ".chanting_speed", 1, 0, 100).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> PERSISTENT_SOUL_HEARTS = ATTRIBUTES.register("persistent_soul_hearts", () -> new RangedAttribute(Eidolon.MODID + ".persistent_soul_hearts", 0, 0, 2000).setSyncable(true));
    @Deprecated(forRemoval = true)
    public static final DeferredHolder<Attribute, Attribute> MAX_SOUL_HEARTS = ATTRIBUTES.register("max_soul_hearts", () -> new RangedAttribute(Eidolon.MODID + ".max_soul_hearts", 60, 0, 2000).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MAGIC_POWER = ATTRIBUTES.register("magic_power", () -> new RangedAttribute("attribute.eidolon.magic_power", 1, 0, 10).setSyncable(true));

    @SubscribeEvent
    public static void addCustomAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> t : event.getTypes()) {
            if (event.has(t, Attributes.MAX_HEALTH)) {
                event.add(t, PERSISTENT_SOUL_HEARTS);
                event.add(t, MAX_SOUL_HEARTS);
            }
            if (t == EntityType.PLAYER) {
                event.add(t, MAGIC_POWER);
                event.add(t, CHANTING_SPEED);
            }
        }
    }

    @SubscribeEvent
    public static void defineAttributes(EntityAttributeCreationEvent event) {
        event.put(EidolonEntities.ZOMBIE_BRUTE.get(), ZombieBruteEntity.createAttributes());
        event.put(EidolonEntities.WRAITH.get(), WraithEntity.createAttributes());
        event.put(EidolonEntities.NECROMANCER.get(), NecromancerEntity.createAttributes());
        event.put(EidolonEntities.RAVEN.get(), RavenEntity.createAttributes());
        event.put(EidolonEntities.SLIMY_SLUG.get(), SlimySlugEntity.createAttributes());
        event.put(EidolonEntities.GIANT_SKEL.get(), GiantSkeletonEntity.createAttributes().build());
    }

}
