package elucent.eidolon.ritual;

import elucent.eidolon.api.ritual.ItemSacrifice;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class MultiItemSacrifice extends ItemSacrifice {

    public List<Ingredient> items = new ArrayList<>();

    public MultiItemSacrifice(ItemLike main, Object... items) {
        super(main);
        this.items.addAll(fromGenericToIngredient(items));
    }

    public MultiItemSacrifice(TagKey<Item> main, Object... items) {
        super(main);
        this.items.addAll(fromGenericToIngredient(items));
    }

    public MultiItemSacrifice(ItemStack main, Object... items) {
        super(main);
        this.items.addAll(fromGenericToIngredient(items));
    }

    public List<Ingredient> fromGenericToIngredient(Object... items) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (Object item : items) {
            if (item instanceof ItemLike itemLike) {
                ingredients.add(Ingredient.of(itemLike));
            } else if (item instanceof TagKey) {
                ingredients.add(Ingredient.of((TagKey<Item>) item));
            } else if (item instanceof ItemStack stack) {
                ingredients.add(Ingredient.of(stack));
            }
        }
        return ingredients;
    }

}
