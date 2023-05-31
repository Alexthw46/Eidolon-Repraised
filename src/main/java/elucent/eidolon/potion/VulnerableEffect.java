package elucent.eidolon.potion;

import elucent.eidolon.Eidolon;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeMobEffect;

public class VulnerableEffect extends MobEffect implements IForgeMobEffect {
    public VulnerableEffect() {
        super(MobEffectCategory.HARMFUL, ColorUtil.packColor(255, 90, 102, 161));
    }

    protected static final ResourceLocation EFFECT_TEXTURE = new ResourceLocation(Eidolon.MODID, "textures/mob_effect/vulnerable.png");

}
