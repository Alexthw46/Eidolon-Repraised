package elucent.eidolon.api.ritual;

import elucent.eidolon.network.Networking;
import elucent.eidolon.network.RitualConsumePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class FocusItemRequirement implements IRequirement {
    final Ingredient match;

    public FocusItemRequirement(ItemStack item) {
        this.match = Ingredient.of(item);
    }

    public FocusItemRequirement(ItemLike item) {
        this.match = Ingredient.of(item);
    }

    public FocusItemRequirement(TagKey<Item> item) {
        this.match = Ingredient.of(item);
    }

    @Override
    public RequirementInfo isMet(Ritual ritual, Level world, BlockPos pos) {
        List<IRitualItemFocus> tiles = Ritual.getTilesWithinAABB(IRitualItemFocus.class, world, ritual.getSearchBounds(pos));
        if (tiles.isEmpty()) return RequirementInfo.FALSE;
        for (IRitualItemFocus tile : tiles) {
            ItemStack stack = tile.provide();
            if (match.test(stack)) {
                return new RequirementInfo(true, ((BlockEntity) tile).getBlockPos());
            }
        }

        return RequirementInfo.FALSE;
    }

    public void whenMet(Ritual ritual, Level world, BlockPos pos, RequirementInfo info) {
        if (world.getBlockEntity(info.getPos()) instanceof IRitualItemProvider provider) {
            provider.take();
        }
        if (!world.isClientSide) {
            Networking.sendToTracking(world, pos.above(2), new RitualConsumePacket(info.getPos(), pos.above(2), ritual.getRed(), ritual.getGreen(), ritual.getBlue()));
        }
    }
}
