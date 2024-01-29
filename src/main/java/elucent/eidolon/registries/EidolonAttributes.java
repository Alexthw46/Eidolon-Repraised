package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.common.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EidolonAttributes {
    static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Eidolon.MODID);
    public static final RegistryObject<Attribute> CHANTING_SPEED = ATTRIBUTES.register("chanting_speed", () -> new RangedAttribute(Eidolon.MODID + ".chanting_speed", 1, 0, 100).setSyncable(true));
    public static final RegistryObject<Attribute> PERSISTENT_SOUL_HEARTS = ATTRIBUTES.register("persistent_soul_hearts", () -> new RangedAttribute(Eidolon.MODID + ".persistent_soul_hearts", 0, 0, 2000).setSyncable(true));
    @Deprecated(forRemoval = true)
    public static final RegistryObject<Attribute> MAX_SOUL_HEARTS = ATTRIBUTES.register("max_soul_hearts", () -> new RangedAttribute(Eidolon.MODID + ".max_soul_hearts", 60, 0, 2000).setSyncable(true));
    public static final RegistryObject<Attribute> MAGIC_POWER = ATTRIBUTES.register("magic_power", () -> new RangedAttribute("attribute.eidolon.magic_power", 1, 0, 10).setSyncable(true));

    @SubscribeEvent
    public static void addCustomAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> t : event.getTypes()) {
            if (event.has(t, Attributes.MAX_HEALTH)) {
                event.add(t, PERSISTENT_SOUL_HEARTS.get());
                event.add(t, MAX_SOUL_HEARTS.get());
            }
            if (t == EntityType.PLAYER) {
                event.add(t, MAGIC_POWER.get());
                event.add(t, CHANTING_SPEED.get());
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
