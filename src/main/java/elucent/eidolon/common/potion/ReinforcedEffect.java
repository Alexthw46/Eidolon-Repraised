package elucent.eidolon.common.potion;

import elucent.eidolon.Eidolon;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.common.extensions.IForgeMobEffect;

public class ReinforcedEffect extends MobEffect implements IForgeMobEffect {
    public ReinforcedEffect() {
        super(MobEffectCategory.BENEFICIAL, ColorUtil.packColor(255, 250, 214, 74));
    }
    
    protected static final ResourceLocation EFFECT_TEXTURE = new ResourceLocation(Eidolon.MODID, "textures/mob_effect/reinforced.png");

}
