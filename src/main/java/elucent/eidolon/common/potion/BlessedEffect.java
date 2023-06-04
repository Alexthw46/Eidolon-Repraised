package elucent.eidolon.common.potion;

import elucent.eidolon.util.ColorUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BlessedEffect extends MobEffect {
    public BlessedEffect() {
        super(MobEffectCategory.BENEFICIAL, ColorUtil.packColor(255, 255, 255, 255));
    }

}
