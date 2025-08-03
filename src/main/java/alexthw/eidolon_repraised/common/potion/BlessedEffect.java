package alexthw.eidolon_repraised.common.potion;

import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BlessedEffect extends MobEffect {
    public BlessedEffect() {
        super(MobEffectCategory.BENEFICIAL, ColorUtil.packColor(255, 255, 255, 255));
    }

}
