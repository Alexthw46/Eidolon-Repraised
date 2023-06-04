package elucent.eidolon.api.ritual;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ItemSacrifice {
    public Ingredient main;

    public ItemSacrifice(Ingredient main) {
        this.main = main;
    }

    public ItemSacrifice(TagKey<Item> main) {
        this.main = Ingredient.of(main);
    }

    public ItemSacrifice(ItemStack main) {
        this.main = Ingredient.of(main);
    }

    public ItemSacrifice(ItemLike main) {
        this.main = Ingredient.of(main);
    }


}
