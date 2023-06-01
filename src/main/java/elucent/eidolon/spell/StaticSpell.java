package elucent.eidolon.spell;

import elucent.eidolon.capability.ISoul;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class StaticSpell extends Spell {
    final SignSequence signs;
    private int cost;

    public StaticSpell(ResourceLocation name, Sign... signs) {
        super(name);
        this.signs = new SignSequence(signs);
    }

    public StaticSpell(ResourceLocation name, int cost, Sign... signs) {
        this(name, signs);
        this.cost = cost;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @NotNull
    protected static Vec3 getVector(Level world, Player player) {
        HitResult ray = world.clip(new ClipContext(player.getEyePosition(0), player.getEyePosition(0).add(player.getLookAngle().scale(4)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        return ray.getType() == HitResult.Type.BLOCK ? ray.getLocation() : player.getEyePosition(0).add(player.getLookAngle().scale(4));
    }

    public static HitResult rayTrace(Entity entity, double length, float lookOffset, boolean hitLiquids) {
        HitResult result = entity.pick(length, lookOffset, hitLiquids);
        EntityHitResult entityLookedAt = MathUtil.getLookedAtEntity(entity, 25);
        return entityLookedAt == null ? result : entityLookedAt;
    }

    @Override
    public boolean matches(SignSequence signs) {
        return this.signs.equals(signs);
    }

    public abstract boolean canCast(Level world, BlockPos pos, Player player);

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player, SignSequence signs) {
        if (getCost() > 0) {
            if (player.getCapability(ISoul.INSTANCE).isPresent()) {
                ISoul soul = player.getCapability(ISoul.INSTANCE).resolve().get();
                if (soul.getMagic() < getCost()) {
                    return false;
                }
            }
        }
        return canCast(world, pos, player);
    }

    public abstract void cast(Level world, BlockPos pos, Player player);

    @Override
    public void cast(Level world, BlockPos pos, Player player, SignSequence signs) {
        cast(world, pos, player);
    }

}
