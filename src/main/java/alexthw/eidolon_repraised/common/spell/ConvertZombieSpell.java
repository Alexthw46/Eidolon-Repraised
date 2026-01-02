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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import static alexthw.eidolon_repraised.registries.Registry.BETTER_ALTAR_BLOCKS;

public class ConvertZombieSpell extends PrayerSpell {
    public ConvertZombieSpell(ResourceLocation resourceLocation, int baseRep, double powerMult, Sign... signs) {
        super(resourceLocation, Deities.LIGHT_DEITY, 20, baseRep, powerMult, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        HitResult ray = rayTrace(player, player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, true);
        boolean flag = ray instanceof EntityHitResult result && result.getEntity() instanceof ZombieVillager;
        EffigyTileEntity effigy = getEffigy(world, pos);
        if (effigy == null) {
            player.displayClientMessage(Component.translatable("eidolon_repraised.message.no_effigy"), true);
            return false;
        }
        AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
        if (!info.getAltar().defaultBlockState().is(BETTER_ALTAR_BLOCKS) || info.getIcon() != Registry.ELDER_EFFIGY.get())
            return false;
        return flag && super.canCast(world, pos, player);
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        EffigyTileEntity effigy = getEffigy(world, pos);
        if (effigy == null) return;

        HitResult ray = rayTrace(player, player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, true);
        if (!(ray instanceof EntityHitResult result && result.getEntity() instanceof ZombieVillager villager)) return;

        if (world instanceof ServerLevel && player instanceof ServerPlayer serverPlayer) {
            effigy.pray();
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
            if (reputation != null) {
                reputation.pray(this, world.getGameTime());
                AdvancementTriggers.CURE_ZOMBIE.get().trigger(serverPlayer);
                KnowledgeUtil.grantResearchNoToast(player, DeityLocks.CURE_ZOMBIE);
                reputation.addReputation(deity.getId(), getBaseRep() + getPowerMultiplier() * info.getPower());
                updateMagic(info, player, world, reputation.getReputation(deity.getId()));
            }
            villager.startConverting(player.getUUID(), 20);
            IMana.expendMana(player, getCost());
        } else {
            playSuccessSound(world, player, effigy, Signs.HARMONY_SIGN);
        }
    }
}
