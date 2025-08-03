package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.altar.AltarInfo;
import alexthw.eidolon_repraised.api.capability.IReputation;
import alexthw.eidolon_repraised.api.deity.Deity;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.deity.DeityLocks;
import alexthw.eidolon_repraised.common.tile.EffigyTileEntity;
import alexthw.eidolon_repraised.common.tile.GobletTileEntity;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AnimalSacrificeSpell extends PrayerSpell {
    public AnimalSacrificeSpell(ResourceLocation name, Deity deity, int rep, double powerMult, Sign... signs) {
        super(name, deity, rep, powerMult, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        if (reputationCheck(world, player, 3.0)) return false;
        EffigyTileEntity effigy = getEffigy(world, pos);
        GobletTileEntity goblet = getGoblet(world, pos);
        if (effigy == null || goblet == null || goblet.getEntityType() == null) {
            player.displayClientMessage(Component.translatable("eidolon.message.no_effigy"), true);
            return false;
        }
        Entity test = goblet.getEntityType().create(world);
        return test instanceof Animal && effigy.ready();
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        EffigyTileEntity effigy = getEffigy(world, pos);
        GobletTileEntity goblet = getGoblet(world, pos);
        if (effigy == null || goblet == null) return;
        if (!world.isClientSide) {
            effigy.pray();
            goblet.setEntityType(null);
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            IReputation reputation = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
            if (reputation != null) {
                reputation.pray(this, world.getGameTime());
                KnowledgeUtil.grantResearchNoToast(player, DeityLocks.SACRIFICE_MOB);
                reputation.addReputation(deity.getId(), getBaseRep() + getPowerMultiplier() * info.getPower());
                updateMagic(info, player, world, reputation.getReputation(deity.getId()));
            }
        } else playSuccessSound(world, player, effigy, Signs.BLOOD_SIGN);
    }
}
