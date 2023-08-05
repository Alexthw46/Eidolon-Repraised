package elucent.eidolon.common.potion;

import elucent.eidolon.Eidolon;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.common.extensions.IForgeMobEffect;

public class UndeathEffect extends MobEffect implements IForgeMobEffect {
    public UndeathEffect() {
        super(MobEffectCategory.HARMFUL, ColorUtil.packColor(255, 51, 39, 42));
    }
    
    protected static final ResourceLocation EFFECT_TEXTURE = new ResourceLocation(Eidolon.MODID, "textures/mob_effect/undeath.png");

}
