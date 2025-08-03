package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.altar.AltarInfo;
import alexthw.eidolon_repraised.api.deity.Deity;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.deity.DeityLocks;
import alexthw.eidolon_repraised.common.tile.EffigyTileEntity;
import alexthw.eidolon_repraised.common.tile.GobletTileEntity;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import alexthw.eidolon_repraised.registries.Registry;
import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VillagerSacrificeSpell extends PrayerSpell {
    public VillagerSacrificeSpell(ResourceLocation name, Deity deity, int baseRep, double powerMult, Sign... signs) {
        super(name, deity, baseRep, powerMult, signs);
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        if (reputationCheck(world, player, 15)) return false;
        EffigyTileEntity effigy = getEffigy(world, pos);
        GobletTileEntity goblet = getGoblet(world, pos);
        if (effigy == null || goblet == null || goblet.getEntityType() == null) {
            player.displayClientMessage(Component.translatable("eidolon_repraised.message.no_effigy"), true);
            return false;
        }
        AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
        if (info.getAltar() != Registry.STONE_ALTAR.get() || info.getIcon() != Registry.ELDER_EFFIGY.get())
            return false;
        return (goblet.getEntityType() == EntityType.PLAYER || goblet.getEntityType().create(world) instanceof AbstractVillager) && effigy.ready();
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        EffigyTileEntity effigy = getEffigy(world, pos);
        GobletTileEntity goblet = getGoblet(world, pos);
        if (effigy == null || goblet == null || goblet.getEntityType() == null) return;
        if (!world.isClientSide) {
            effigy.pray();
            goblet.setEntityType(null);
            AltarInfo info = AltarInfo.getAltarInfo(world, effigy.getBlockPos());
            var rep = player.getCapability(EidolonCapabilities.REPUTATION_CAPABILITY);
            if (rep != null) {
                rep.pray(this, world.getGameTime());
                KnowledgeUtil.grantResearchNoToast(player, DeityLocks.SACRIFICE_VILLAGER);
                rep.addReputation(deity.getId(), 6.0 + getPowerMultiplier() * info.getPower());
                updateMagic(info, player, world, rep.getReputation(deity.getId()));
            }
        } else {
            playSuccessSound(world, player, effigy, Signs.SOUL_SIGN);
        }
    }
}
