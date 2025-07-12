package elucent.eidolon.common.potion;

import elucent.eidolon.Eidolon;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.common.extensions.IMobEffectExtension;
import net.minecraft.resources.ResourceLocation;

public class VulnerableEffect extends MobEffect implements IMobEffectExtension {
    public VulnerableEffect() {
        super(MobEffectCategory.HARMFUL, ColorUtil.packColor(255, 90, 102, 161));
    }

    protected static final ResourceLocation EFFECT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/mob_effect/vulnerable.png" );

}
