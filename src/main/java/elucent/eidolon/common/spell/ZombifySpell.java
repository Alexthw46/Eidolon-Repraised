package elucent.eidolon.common.spell;

import elucent.eidolon.api.altar.AltarInfo;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.capability.ISoul;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.common.deity.DeityLocks;
import elucent.eidolon.common.tile.EffigyTileEntity;
import elucent.eidolon.registries.Registry;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;

public class ZombifySpell extends PrayerSpell {
    public ZombifySpell(ResourceLocation resourceLocation, Sign... signs) {
        super(resourceLocation, Deities.DARK_DEITY, 20, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getBlockReach(), 0, true);
        boolean flag = ray instanceof EntityHitResult result && result.getEntity() instanceof Villager;
        EffigyTileEntity effigy = getEffigy(world, pos);
        if (effigy == null) return false;
        AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
        if (info.getAltar() != Registry.STONE_ALTAR.get() || info.getIcon() != Registry.ELDER_EFFIGY.get())
            return false;
        return flag && super.canCast(world, pos, player);
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

        EffigyTileEntity effigy = getEffigy(world, pos);
        if (effigy == null) return;

        HitResult ray = rayTrace(player, player.getBlockReach(), 0, true);
        if (!(ray instanceof EntityHitResult result && result.getEntity() instanceof Villager villager)) return;

        if (world instanceof ServerLevel level) {
            effigy.pray();
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            world.getCapability(IReputation.INSTANCE, null).ifPresent((rep) -> {
                rep.pray(player, this, world.getGameTime());
                KnowledgeUtil.grantResearchNoToast(player, DeityLocks.ZOMBIFY_VILLAGER);
                rep.addReputation(player, deity.getId(), 6.0 + info.getPower());
                updateMagic(info, player, world, rep.getReputation(player, deity.getId()));
            });
            zombify(villager, level);
            ISoul.expendMana(player, getCost());
        } else {
            playSuccessSound(world, player, effigy, Signs.DEATH_SIGN);
        }
    }

    private void zombify(Villager villager, ServerLevel level) {
        ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
        if (zombievillager == null) return;
        zombievillager.finalizeSpawn(level, level.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), null);
        zombievillager.setVillagerData(villager.getVillagerData());
        zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE));
        zombievillager.setTradeOffers(villager.getOffers().createTag());
        zombievillager.setVillagerXp(villager.getVillagerXp());
        ForgeEventFactory.onLivingConvert(villager, zombievillager);
    }

}
