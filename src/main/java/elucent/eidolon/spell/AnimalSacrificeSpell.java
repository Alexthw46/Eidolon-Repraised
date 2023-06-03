package elucent.eidolon.spell;

import elucent.eidolon.capability.IReputation;
import elucent.eidolon.deity.Deity;
import elucent.eidolon.deity.DeityLocks;
import elucent.eidolon.ritual.Ritual;
import elucent.eidolon.tile.EffigyTileEntity;
import elucent.eidolon.tile.GobletTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;

public class AnimalSacrificeSpell extends PrayerSpell {

    public AnimalSacrificeSpell(ResourceLocation name, Deity deity, Sign... signs) {
        super(name, deity, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        if (!world.getCapability(IReputation.INSTANCE).isPresent()) return false;
        if (!world.getCapability(IReputation.INSTANCE).resolve().get().canPray(player, getRegistryName(), world.getGameTime()))
            return false;
        if (world.getCapability(IReputation.INSTANCE).resolve().get().getReputation(player.getUUID(), deity.getId()) < 3.0)
            return false;
        List<GobletTileEntity> goblets = Ritual.getTilesWithinAABB(GobletTileEntity.class, world, new AABB(pos.offset(-4, -4, -4), pos.offset(5, 5, 5)));
        List<EffigyTileEntity> effigies = Ritual.getTilesWithinAABB(EffigyTileEntity.class, world, new AABB(pos.offset(-4, -4, -4), pos.offset(5, 5, 5)));
        if (effigies.size() == 0 || goblets.size() == 0) return false;
        EffigyTileEntity effigy = effigies.stream().min(Comparator.comparingDouble((e) -> e.getBlockPos().distSqr(pos))).get();
        GobletTileEntity goblet = goblets.stream().min(Comparator.comparingDouble((e) -> e.getBlockPos().distSqr(pos))).get();
        if (goblet.getEntityType() == null) return false;
        Entity test = goblet.getEntityType().create(world);
        return test instanceof Animal && effigy.ready();
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        List<GobletTileEntity> goblets = Ritual.getTilesWithinAABB(GobletTileEntity.class, world, new AABB(pos.offset(-4, -4, -4), pos.offset(5, 5, 5)));
        List<EffigyTileEntity> effigies = Ritual.getTilesWithinAABB(EffigyTileEntity.class, world, new AABB(pos.offset(-4, -4, -4), pos.offset(5, 5, 5)));
        if (effigies.size() == 0 || goblets.size() == 0) return;
        EffigyTileEntity effigy = effigies.stream().min(Comparator.comparingDouble((e) -> e.getBlockPos().distSqr(pos))).get();
        GobletTileEntity goblet = goblets.stream().min(Comparator.comparingDouble((e) -> e.getBlockPos().distSqr(pos))).get();
        if (!world.isClientSide) {
            effigy.pray();
            goblet.setEntityType(null);
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            world.getCapability(IReputation.INSTANCE, null).ifPresent((rep) -> {
                rep.pray(player, getRegistryName(), world.getGameTime());
                rep.unlock(player, deity.getId(), DeityLocks.SACRIFICE_MOB);
                rep.addReputation(player, deity.getId(), 3.0 + 0.5 * info.getPower());
                updateMagic(info, player, world, rep.getReputation(player, deity.getId()));
            });
        } else playSuccessSound(world, player, effigy, Signs.BLOOD_SIGN);
    }
}
