package alexthw.eidolon_repraised.datagen;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EidItemTagProvider extends ItemTagsProvider {
    public EidItemTagProvider(DataGenerator pGenerator, CompletableFuture<HolderLookup.Provider> provider, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator.getPackOutput(), provider, pBlockTagsProvider.contentsGetter(), Eidolon.MODID, existingFileHelper);
    }

    public static final TagKey<Item> SCRIBE_ITEMS = eidolonItemTag("scribe_items");
    public static final TagKey<Item> PATRON_SYMBOL = eidolonItemTag("patron_symbol");

    @NotNull
    private static TagKey<Item> eidolonItemTag(String name) {
        return ItemTags.create(Eidolon.prefix(name));
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(Tags.Items.MUSIC_DISCS).add(Registry.PAROUSIA_DISC.get());
        tag(SCRIBE_ITEMS).add(Items.CHARCOAL, Items.FEATHER, Items.BOOK, Registry.CANDLE.get().asItem(), Registry.PARCHMENT.get(), Registry.MAGIC_INK.get());
        tag(PATRON_SYMBOL).add(Registry.HOLY_SYMBOL.get(), Registry.UNHOLY_SYMBOL.get());
        this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
        this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        this.copy(BlockTags.CANDLES, ItemTags.CANDLES);
        tag(ItemTags.BOOKSHELF_BOOKS).add(Registry.CODEX.get());
        tag(ItemTags.LECTERN_BOOKS).add(Registry.CODEX.get());
        //this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        tag(ItemTags.SIGNS).add(Registry.ILLWOOD_PLANKS.getStandingSign().asItem(), Registry.POLISHED_PLANKS.getStandingSign().asItem());
        this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);

        Item[] armors = new Item[]{
                Registry.SILVER_HELMET.get(),
                Registry.SILVER_CHESTPLATE.get(),
                Registry.SILVER_LEGGINGS.get(),
                Registry.SILVER_BOOTS.get(),
                Registry.WARLOCK_HAT.get(),
                Registry.WARLOCK_CLOAK.get(),
                Registry.WARLOCK_BOOTS.get(),
                Registry.BONELORD_HELM.get(),
                Registry.BONELORD_CHESTPLATE.get(),
                Registry.BONELORD_GREAVES.get()
        };

        tag(ItemTags.ARMOR_ENCHANTABLE).add(armors);
        tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(armors);
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(armors);

        tag(ItemTags.HEAD_ARMOR).add(Registry.SILVER_HELMET.get(),
                Registry.WARLOCK_HAT.get(),
                Registry.BONELORD_HELM.get()
        );
        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(Registry.SILVER_HELMET.get(),
                Registry.WARLOCK_HAT.get(),
                Registry.BONELORD_HELM.get()
        );

        tag(ItemTags.CHEST_ARMOR).add(Registry.SILVER_CHESTPLATE.get(),
                Registry.WARLOCK_CLOAK.get(),
                Registry.BONELORD_CHESTPLATE.get()
        );
        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(Registry.SILVER_CHESTPLATE.get(),
                Registry.WARLOCK_CLOAK.get(),
                Registry.BONELORD_CHESTPLATE.get()
        );

        tag(ItemTags.LEG_ARMOR).add(Registry.SILVER_LEGGINGS.get(),
                Registry.BONELORD_GREAVES.get()
        );
        tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(Registry.SILVER_LEGGINGS.get(),
                Registry.BONELORD_GREAVES.get()
        );


        tag(ItemTags.FOOT_ARMOR).add(Registry.SILVER_BOOTS.get(),
                Registry.WARLOCK_BOOTS.get()
        );
        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(Registry.SILVER_BOOTS.get(),
                Registry.WARLOCK_BOOTS.get()
        );

    }
}
