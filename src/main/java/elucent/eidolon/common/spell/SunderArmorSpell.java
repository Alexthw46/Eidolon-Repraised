package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class SunderArmorSpell extends ApplyPotionSpell {
    public SunderArmorSpell(ResourceLocation name, Sign... signs) {
        super(name, 50, signs);
    }

    @Override
    protected MobEffectInstance getPotionEffect(Player player) {
        return new MobEffectInstance(EidolonPotions.VULNERABLE_EFFECT.get(), 1200, 0);
    }

}
