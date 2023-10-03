package elucent.eidolon.common.ritual;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.IRitualItemFocus;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.item.IRechargeableWand;
import elucent.eidolon.network.Networking;
import elucent.eidolon.network.RitualConsumePacket;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class RechargingRitual extends Ritual {
    public static final ResourceLocation SYMBOL = new ResourceLocation(Eidolon.MODID, "particle/recharge_ritual");

    public RechargingRitual() {
        super(SYMBOL, ColorUtil.packColor(255, 220, 180, 701));
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {
        List<IRitualItemFocus> tiles = Ritual.getTilesWithinAABB(IRitualItemFocus.class, world, getSearchBounds(pos));
        if (!tiles.isEmpty()) for (IRitualItemFocus tile : tiles) {
            ItemStack stack = tile.provide();
            if (stack.getItem() instanceof IRechargeableWand) {
                tile.replace(((IRechargeableWand) stack.getItem()).recharge(stack));
                if (!world.isClientSide && tile instanceof BlockEntity b) {
                    Networking.sendToTracking(world, b.getBlockPos(), new RitualConsumePacket(pos.above(2), b.getBlockPos(), getRed(), getGreen(), getBlue()));
                }
                break;
            }
        }
        return RitualResult.TERMINATE;
    }
}
