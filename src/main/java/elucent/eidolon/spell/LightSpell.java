package elucent.eidolon.spell;

import elucent.eidolon.Registry;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.deity.Deities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.concurrent.atomic.AtomicReference;

public class LightSpell extends StaticSpell {
    public LightSpell(ResourceLocation name, Sign... signs) {
        super(name, 5, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        AtomicReference<Boolean> favor = new AtomicReference<>(Boolean.FALSE);
        world.getCapability(IReputation.INSTANCE).ifPresent(reputation -> favor.set(reputation.getReputation(player, Deities.LIGHT_DEITY.getId()) > 10));
        return favor.get();
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getReachDistance(), 0, true);
        if (ray instanceof BlockHitResult blockHitResult) {
            BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
            if (world.getBlockState(blockPos).getMaterial().isReplaceable() && world.isUnobstructed(Registry.GHOST_LIGHT.get().defaultBlockState(), blockPos, CollisionContext.of(player))) {
                BlockState lightBlockState = Registry.GHOST_LIGHT.get().defaultBlockState();
                world.setBlockAndUpdate(blockPos, lightBlockState);
                world.sendBlockUpdated(blockPos, world.getBlockState(blockPos), world.getBlockState(blockPos), 2);
                ISoul.expendMana(player, getCost());
            }
        } else if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
            ISoul.expendMana(player, getCost());
        }
    }

}
