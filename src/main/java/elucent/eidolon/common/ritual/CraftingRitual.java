package elucent.eidolon.common.ritual;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.tile.BrazierTileEntity;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CraftingRitual extends Ritual {
    final ItemStack result;
    private final boolean keepNbtOfReagent;

    public CraftingRitual(ResourceLocation symbol, int color, ItemStack result) {
        this(symbol, color, result, true);
    }

    public CraftingRitual(ResourceLocation symbol, int color, ItemStack result, boolean keepNBT) {
        super(symbol, color);
        this.result = result;
        this.keepNbtOfReagent = keepNBT;
    }

    @Override
    public Ritual cloneRitual() {
        return new CraftingRitual(getSymbol(), getColor(), result);
    }

    public static class SanguineRitual extends CraftingRitual {
        public static final ResourceLocation SYMBOL = new ResourceLocation(Eidolon.MODID, "particle/sanguine_ritual");

        public SanguineRitual(ItemStack result) {
            super(SYMBOL, ColorUtil.packColor(255, 255, 51, 85), result);
        }
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            if (world.getBlockEntity(pos) instanceof BrazierTileEntity inv) {
                ItemStack result = this.getResult().copy();
                if (keepNbtOfReagent && inv.getStack().hasTag()) {
                    result.setTag(inv.getStack().getTag());
                    result.setDamageValue(0);
                }
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5, result));
            }
        }
        return RitualResult.TERMINATE;
    }
}
