package elucent.eidolon.common.item.curio;

import com.google.common.collect.Multimap;
import elucent.eidolon.registries.Registry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;
import java.util.UUID;

public class ResoluteBeltItem extends EidolonCurio {
    final UUID ATTR_ID = new UUID(3701779382882225399L, 5035874982077300549L);

    public ResoluteBeltItem(Properties properties) {
        super(properties);
        NeoForge.EVENT_BUS.addListener(ResoluteBeltItem::onHurt);
    }

    static final Random random = new Random();

    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof LivingEntity entity && CuriosApi.getCuriosInventory(event.getEntity()).flatMap(i -> i.findFirstCurio(Registry.RESOLUTE_BELT.get())).isPresent()) {
            Vec3 diff = event.getEntity().position().subtract(entity.position()).multiply(1, 0, 1).normalize();
            entity.knockback(0.8f, diff.x, diff.z);
            if (!entity.level().isClientSide)
                entity.level().playSound(null, entity.blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS, 1.0f, 1.9f + 0.2f * random.nextFloat());
        }
    }


    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation uuid, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = super.getAttributeModifiers(slotContext, uuid, stack);
        map.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, 1.0f, AttributeModifier.Operation.ADD_VALUE));
        return map;
    }

}
