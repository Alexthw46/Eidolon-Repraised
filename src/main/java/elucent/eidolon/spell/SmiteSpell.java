package elucent.eidolon.spell;

import elucent.eidolon.capability.IReputation;
import elucent.eidolon.deity.Deities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import java.util.concurrent.atomic.AtomicReference;

public class SmiteSpell extends StaticSpell {
    public SmiteSpell(ResourceLocation name, Sign... signs) {
        super(name, 40, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        AtomicReference<Boolean> favor = new AtomicReference<>(Boolean.FALSE);
        world.getCapability(IReputation.INSTANCE).ifPresent(reputation -> favor.set(reputation.getReputation(player, Deities.LIGHT_DEITY.getId()) > 40));
        return favor.get();
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

        var ray = rayTrace(player, player.getReachDistance(), 0, true);

        if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity.getMobType() == MobType.UNDEAD) {
                livingEntity.hurt(DamageSource.MAGIC, 10);
            }
        }

    }
}
