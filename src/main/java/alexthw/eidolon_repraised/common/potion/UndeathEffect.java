package alexthw.eidolon_repraised.common.potion;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.common.extensions.IMobEffectExtension;

public class UndeathEffect extends MobEffect implements IMobEffectExtension {
    public UndeathEffect() {
        super(MobEffectCategory.HARMFUL, ColorUtil.packColor(255, 51, 39, 42));
    }
    
    protected static final ResourceLocation EFFECT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"textures/mob_effect/undeath.png" );

}
