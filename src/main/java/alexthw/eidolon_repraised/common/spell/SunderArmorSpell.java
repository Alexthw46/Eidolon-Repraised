package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.registries.EidolonPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class SunderArmorSpell extends ApplyPotionSpell {
    public SunderArmorSpell(ResourceLocation name, Sign... signs) {
        super(name, 50, signs);
    }

    @Override
    protected MobEffectInstance getPotionEffect(Player player) {
        return new MobEffectInstance(EidolonPotions.VULNERABLE_EFFECT, 1200, 0);
    }

}
