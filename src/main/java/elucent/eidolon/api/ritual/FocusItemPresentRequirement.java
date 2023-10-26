package elucent.eidolon.api.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class FocusItemPresentRequirement extends FocusItemRequirement {
    public FocusItemPresentRequirement(Ingredient ingredient) {
        super(ingredient);
    }
    public FocusItemPresentRequirement(ItemStack item) {
        super(item);
    }

    public FocusItemPresentRequirement(ItemLike item) {
        super(item);
    }

    public FocusItemPresentRequirement(TagKey<Item> item) {
        super(item);
    }

    public void whenMet(Ritual ritual, Level world, BlockPos pos, RequirementInfo info) {
        //don't consume the item
    }

    public Ingredient getMatch() {
        return match;
    }
}
