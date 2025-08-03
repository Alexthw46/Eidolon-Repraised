package elucent.eidolon.common.spell;

import elucent.eidolon.api.altar.AltarInfo;
import elucent.eidolon.api.capability.IReputation;
import elucent.eidolon.api.deity.Deity;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.common.deity.DeityLocks;
import elucent.eidolon.common.tile.EffigyTileEntity;
import elucent.eidolon.common.tile.GobletTileEntity;
import elucent.eidolon.registries.EidolonCapabilities;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.util.KnowledgeUtil;
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
