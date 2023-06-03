package elucent.eidolon.spell;

import elucent.eidolon.capability.IReputation;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.deity.Deities;
import elucent.eidolon.deity.DeityLocks;
import elucent.eidolon.ritual.Ritual;
import elucent.eidolon.tile.EffigyTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Comparator;
import java.util.List;

public class ConvertZombieSpell extends PrayerSpell {
    public ConvertZombieSpell(ResourceLocation resourceLocation, Sign... signs) {
        super(resourceLocation, Deities.LIGHT_DEITY, 20, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getReachDistance(), 0, true);
        boolean flag = ray instanceof EntityHitResult result && result.getEntity() instanceof ZombieVillager;
        return flag && super.canCast(world, pos, player);
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        List<EffigyTileEntity> effigies = Ritual.getTilesWithinAABB(EffigyTileEntity.class, world, new AABB(pos.offset(-4, -4, -4), pos.offset(5, 5, 5)));
        if (effigies.size() == 0) return;
        EffigyTileEntity effigy = effigies.stream().min(Comparator.comparingDouble((e) -> e.getBlockPos().distSqr(pos))).get();

        HitResult ray = rayTrace(player, player.getReachDistance(), 0, true);
        if (!(ray instanceof EntityHitResult result && result.getEntity() instanceof ZombieVillager villager)) return;

        if (world instanceof ServerLevel level) {
            effigy.pray();
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            world.getCapability(IReputation.INSTANCE, null).ifPresent((rep) -> {
                rep.pray(player, getRegistryName(), world.getGameTime());
                rep.unlock(player, deity.getId(), DeityLocks.HEAL_VILLAGER);
                rep.addReputation(player, deity.getId(), 1.0 + info.getPower());
                updateMagic(info, player, world, rep.getReputation(player, deity.getId()));
            });
            villager.finishConversion(level);
            ISoul.expendMana(player, getCost());
        } else {
            playSuccessSound(world, player, effigy, Signs.SOUL_SIGN);
        }
    }
}
