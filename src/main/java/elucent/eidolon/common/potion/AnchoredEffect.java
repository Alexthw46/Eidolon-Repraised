package elucent.eidolon.common.potion;

import elucent.eidolon.Eidolon;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMobEffect;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AnchoredEffect extends MobEffect implements IForgeMobEffect {
    public AnchoredEffect() {
        super(MobEffectCategory.BENEFICIAL, ColorUtil.packColor(255, 154, 58, 232));
        MinecraftForge.EVENT_BUS.addListener(this::anchor);
    }

    @SubscribeEvent
    public void anchor(EntityTeleportEvent event) {
        if (event instanceof EntityTeleportEvent.TeleportCommand) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity living && living.hasEffect(this)) {
            event.setCanceled(true);
        }
    }

    protected static final ResourceLocation EFFECT_TEXTURE = new ResourceLocation(Eidolon.MODID, "textures/mob_effect/anchored.png");

}
