package alexthw.eidolon_repraised.common.ritual;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.ritual.IRitualItemFocus;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.common.item.SummoningStaffItem;
import alexthw.eidolon_repraised.common.spell.ThrallSpell;
import alexthw.eidolon_repraised.network.MagicBurstEffectPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.RitualConsumePacket;
import alexthw.eidolon_repraised.util.ColorUtil;
import alexthw.eidolon_repraised.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class AbsorptionRitual extends Ritual {
    public static final ResourceLocation SYMBOL = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "particle/absorption_ritual");

    public AbsorptionRitual() {
        super(SYMBOL, ColorUtil.packColor(255, 123, 140, 70));
    }

    @Override
    public Ritual cloneRitual() {
        return new AbsorptionRitual();
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {
        if (world.isClientSide) return RitualResult.TERMINATE;
        List<IRitualItemFocus> tiles = Ritual.getTilesWithinAABB(IRitualItemFocus.class, world, getSearchBounds(pos));
        BlockPos toRecharge = null;
        if (!tiles.isEmpty()) for (IRitualItemFocus tile : tiles) {
            ItemStack stack = tile.provide();
            if (stack.getItem() instanceof SummoningStaffItem) {
                toRecharge = ((BlockEntity) tile).getBlockPos();
                break;
            }
        }

        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, getSearchBounds(pos), (e) -> ((e.getType().is(EntityTypeTags.UNDEAD)) || e.getType().is(ThrallSpell.ENTHRALL_WHITELIST)) && !e.getType().is(ThrallSpell.ENTHRALL_BLACKLIST) && !(e instanceof Player) && !EntityUtil.isEnthralled(e) && e.getHealth() <= e.getMaxHealth() / 3);
        ListTag entityTags = new ListTag();
        for (LivingEntity e : entities) {
            e.setHealth(e.getMaxHealth());
            Networking.sendToNearbyClient(world, e.blockPosition(), new MagicBurstEffectPacket(e.getX(), e.getY() + 0.1, e.getZ(),
                    ColorUtil.packColor(255, 61, 70, 35), ColorUtil.packColor(255, 36, 24, 41)));
            if (toRecharge != null) {
                Networking.sendToNearbyClient(world, toRecharge, new RitualConsumePacket(e.blockPosition().above(), toRecharge, getRed(), getGreen(), getBlue()));
            }
            CompoundTag eTag = e.serializeNBT(world.registryAccess());
            entityTags.add(eTag);
            e.remove(RemovalReason.KILLED);
        }
        if (!tiles.isEmpty()) for (IRitualItemFocus tile : tiles) {
            ItemStack stack = tile.provide();
            if (stack.getItem() instanceof SummoningStaffItem s) {
                tile.replace(s.addCharges(stack, entityTags));
                break;
            }
        }
        return RitualResult.TERMINATE;
    }
}
