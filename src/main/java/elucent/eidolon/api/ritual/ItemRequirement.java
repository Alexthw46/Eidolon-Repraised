package elucent.eidolon.api.ritual;

import elucent.eidolon.network.Networking;
import elucent.eidolon.network.RitualConsumePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class ItemRequirement implements IRequirement {
    final Ingredient match;

    public ItemRequirement(ItemStack item) {
        this.match = Ingredient.of(item);
    }

    public ItemRequirement(Item item) {
        this.match = Ingredient.of(item);
    }

    public ItemRequirement(Block block) {
        this.match = Ingredient.of(block);
    }

    public ItemRequirement(TagKey<Item> item) {
        this.match = Ingredient.of(item);
    }

    @Override
    public RequirementInfo isMet(Ritual ritual, Level world, BlockPos pos) {
        List<IRitualItemProvider> tiles = Ritual.getTilesWithinAABB(IRitualItemProvider.class, world, ritual.getSearchBounds(pos));
        if (tiles.isEmpty()) return RequirementInfo.FALSE;
        for (IRitualItemProvider tile : tiles) {
            ItemStack stack = tile.provide();
            if (match.test(stack)) {
                return new RequirementInfo(true, ((BlockEntity) tile).getBlockPos());
            }
        }

        return RequirementInfo.FALSE;
    }

    public void whenMet(Ritual ritual, Level world, BlockPos pos, RequirementInfo info) {
        ((IRitualItemProvider)world.getBlockEntity(info.getPos())).take();
        if (!world.isClientSide) {
            Networking.sendToTracking(world, pos.above(2), new RitualConsumePacket(info.getPos(), pos.above(2), ritual.getRed(), ritual.getGreen(), ritual.getBlue()));
        }
    }

    public Ingredient getMatch() {
        return match;
    }
}
