package elucent.eidolon.common.spell;

import elucent.eidolon.api.spells.Sign;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public abstract class ApplyPotionSpell extends StaticSpell {
    public ApplyPotionSpell(ResourceLocation name, int cost, Sign... signs) {
        super(name, cost, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        return rayTrace(player, player.getReachDistance(), 0, true) instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        HitResult raytrace = rayTrace(player, player.getReachDistance(), 0, true);
        if (isSelf(raytrace)) {
            player.addEffect(getPotionEffect(player));
        } else if (raytrace instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity entity) {
            entity.addEffect(getPotionEffect(player));
        }
    }

    protected boolean isSelf(HitResult raytrace) {
        return false;
    }

    protected abstract MobEffectInstance getPotionEffect(Player player);

}
