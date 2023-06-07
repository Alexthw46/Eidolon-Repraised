package elucent.eidolon.common.item.curio;

import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.Registry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;
import java.util.UUID;

public class ResoluteBeltItem extends EidolonCurio {
    final UUID ATTR_ID = new UUID(3701779382882225399L, 5035874982077300549L);

    public ResoluteBeltItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.addListener(ResoluteBeltItem::onHurt);
    }

    static final Random random = new Random();

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity entity && CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), Registry.RESOLUTE_BELT.get()).isPresent()) {
            Vec3 diff = event.getEntity().position().subtract(entity.position()).multiply(1, 0, 1).normalize();
            entity.knockback(0.8f, diff.x, diff.z);
            if (!entity.level.isClientSide)
                entity.level.playSound(null, entity.blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS, 1.0f, 1.9f + 0.2f * random.nextFloat());
        }
    }


    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slotContext, uuid, stack);
        map.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ATTR_ID, Eidolon.MODID + ":resolute_belt", 1.0f, AttributeModifier.Operation.ADDITION));
        return map;
    }

}
