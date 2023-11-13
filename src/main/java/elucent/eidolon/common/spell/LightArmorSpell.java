package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class LightArmorSpell extends ApplyPotionSpell {
    public LightArmorSpell(ResourceLocation name, Sign... signs) {
        super(name, 50, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    protected boolean isSelf(HitResult raytrace) {
        return !(raytrace instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity);
    }

    @Override
    protected MobEffectInstance getPotionEffect(Player player) {
        return new MobEffectInstance(EidolonPotions.REINFORCED_EFFECT.get(), 1800, 0);
    }
}
