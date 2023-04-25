package elucent.eidolon.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class FocusItemPresentRequirement extends FocusItemRequirement {
    public FocusItemPresentRequirement(ItemStack item) {
        super(item);
    }

    public FocusItemPresentRequirement(Item item) {
        super(item);
    }

    public FocusItemPresentRequirement(Block block) {
        super(block);
    }

    public FocusItemPresentRequirement(TagKey<Item> item) {
        super(item);
    }

    public FocusItemPresentRequirement(Function<ItemStack, Boolean> item) {
        super(item);
    }

    public void whenMet(Ritual ritual, Level world, BlockPos pos, RequirementInfo info) {
        //
    }
}
