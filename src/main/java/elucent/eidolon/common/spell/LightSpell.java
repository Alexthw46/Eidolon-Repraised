package elucent.eidolon.common.spell;

import elucent.eidolon.api.deity.Deity;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.common.block.GhostLight;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.registries.EidolonAttributes;
import elucent.eidolon.registries.Registry;
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

    final Deity deity;

    public LightSpell(ResourceLocation name, Deity deity, Sign... signs) {
        super(name, 3, signs);
        this.deity = deity;
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        AtomicReference<Boolean> favor = new AtomicReference<>(Boolean.FALSE);
        world.getCapability(IReputation.INSTANCE).ifPresent(reputation -> favor.set(reputation.getReputation(player, deity.getId()) >= 3));
        return favor.get();
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getReachDistance(), 0, true);
        if (ray instanceof BlockHitResult blockHitResult) {
            BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
            if (world.getBlockState(blockPos).getMaterial().isReplaceable() && world.isUnobstructed(Registry.GHOST_LIGHT.get().defaultBlockState(), blockPos, CollisionContext.of(player))) {
                BlockState lightBlockState = Registry.GHOST_LIGHT.get().defaultBlockState();
                if (deity.getId().equals(Deities.DARK_DEITY_ID)) {
                    lightBlockState = lightBlockState.setValue(GhostLight.DEITY, false);
                }
                world.setBlockAndUpdate(blockPos, lightBlockState);
                world.sendBlockUpdated(blockPos, world.getBlockState(blockPos), world.getBlockState(blockPos), 2);
                ISoul.expendMana(player, getCost());
            }
        } else if (ray instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING, EidolonAttributes.getSpellEffectDuration(player, 200)));
            ISoul.expendMana(player, getCost());
        }
    }

}
