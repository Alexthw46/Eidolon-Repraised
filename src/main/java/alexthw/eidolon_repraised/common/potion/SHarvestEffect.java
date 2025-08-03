package alexthw.eidolon_repraised.common.potion;

import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SHarvestEffect extends MobEffect {

    public SHarvestEffect() {
        super(MobEffectCategory.BENEFICIAL, ColorUtil.packColor(255, 154, 58, 232));
    }

}
