package alexthw.eidolon_repraised.common.ritual;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.ritual.IRitualItemFocus;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.common.item.IRechargeableWand;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.network.RitualConsumePacket;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class RechargingRitual extends Ritual {
    public static final ResourceLocation SYMBOL = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"particle/recharge_ritual" );

    public RechargingRitual() {
        super(SYMBOL, ColorUtil.packColor(255, 220, 180, 701));
    }

    @Override
    public Ritual cloneRitual() {
        return new RechargingRitual();
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {
        List<IRitualItemFocus> tiles = Ritual.getTilesWithinAABB(IRitualItemFocus.class, world, getSearchBounds(pos));
        if (!tiles.isEmpty()) for (IRitualItemFocus tile : tiles) {
            ItemStack stack = tile.provide();
            if (stack.getItem() instanceof IRechargeableWand) {
                tile.replace(((IRechargeableWand) stack.getItem()).recharge(stack));
                if (!world.isClientSide && tile instanceof BlockEntity b) {
                    Networking.sendToNearbyClient(world, b.getBlockPos(), new RitualConsumePacket(pos.above(2), b.getBlockPos(), getRed(), getGreen(), getBlue()));
                }
                break;
            }
        }
        return RitualResult.TERMINATE;
    }
}
