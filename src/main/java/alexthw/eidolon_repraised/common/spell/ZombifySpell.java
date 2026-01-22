package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.altar.AltarInfo;
import alexthw.eidolon_repraised.api.capability.IMana;
import alexthw.eidolon_repraised.api.capability.IReputation;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.deity.Deities;
import alexthw.eidolon_repraised.common.deity.DeityLocks;
import alexthw.eidolon_repraised.common.tile.EffigyTileEntity;
import alexthw.eidolon_repraised.registries.AdvancementTriggers;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.Registry;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.EventHooks;

public class ZombifySpell extends PrayerSpell {
    public ZombifySpell(ResourceLocation resourceLocation, int baseRep, double powerMult, Sign... signs) {
        super(resourceLocation, Deities.DARK_DEITY, 20, baseRep, powerMult, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, true);
        boolean flag = ray instanceof EntityHitResult result && result.getEntity() instanceof Villager;
        EffigyTileEntity effigy = getEffigy(world, pos);
        if (effigy == null) {
            player.displayClientMessage(Component.translatable("eidolon_repraised.message.no_effigy"), true);
            return false;
        }
        AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
        if (info.getAltar() == null || !info.getAltar().defaultBlockState().is(Registry.BETTER_ALTAR_BLOCKS) || info.getIcon() != Registry.ELDER_EFFIGY.get())
            return false;
        return flag && super.canCast(world, pos, player);
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {

        EffigyTileEntity effigy = getEffigy(world, pos);
        if (effigy == null) return;

        HitResult ray = rayTrace(player, player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, true);
        if (!(ray instanceof EntityHitResult result && result.getEntity() instanceof Villager villager)) return;

        if (world instanceof ServerLevel level && player instanceof ServerPlayer serverPlayer) {
            effigy.pray();
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            IReputation rep = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
            if (rep != null) {
                rep.pray(this, world.getGameTime());
                AdvancementTriggers.ZOMBIFY.get().trigger(serverPlayer);
                KnowledgeUtil.grantResearchNoToast(player, DeityLocks.ZOMBIFY_VILLAGER);
                rep.addReputation(deity.getId(), getBaseRep() + getPowerMultiplier() * info.getPower());
                updateMagic(info, player, world, rep.getReputation(deity.getId()));
            }
            zombify(villager, level);
            IMana.expendMana(player, getCost());
        } else {
            playSuccessSound(world, player, effigy, Signs.DEATH_SIGN);
        }
    }

    private void zombify(Villager villager, ServerLevel level) {
        ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
        if (zombievillager == null) return;
        zombievillager.finalizeSpawn(level, level.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true));
        zombievillager.setVillagerData(villager.getVillagerData());
        zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE));
        zombievillager.setTradeOffers(villager.getOffers());
        zombievillager.setVillagerXp(villager.getVillagerXp());
        EventHooks.onLivingConvert(villager, zombievillager);
    }

}
