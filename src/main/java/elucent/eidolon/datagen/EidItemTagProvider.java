package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EidItemTagProvider extends ItemTagsProvider {
    public EidItemTagProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, Eidolon.MODID, existingFileHelper);
    }

    public static final TagKey<Item> SCRIBE_ITEMS = eidolonItemTag("scribe_items");
    public static final TagKey<Item> PATRON_SYMBOL = eidolonItemTag("patron_symbol");

    @NotNull
    private static TagKey<Item> eidolonItemTag(String name) {
        return ItemTags.create(new ResourceLocation("eidolon", name));
    }

    @Override
    protected void addTags() {
        tag(ItemTags.MUSIC_DISCS).add(Registry.PAROUSIA_DISC.get());
        tag(SCRIBE_ITEMS).add(Items.CHARCOAL, Items.FEATHER, Items.BOOK, Registry.CANDLE.get().asItem(), Registry.PARCHMENT.get(), Registry.MAGIC_INK.get());
        tag(PATRON_SYMBOL).add(Registry.HOLY_SYMBOL.get(), Registry.UNHOLY_SYMBOL.get());
        this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
        this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        this.copy(BlockTags.CANDLES, ItemTags.CANDLES);

        //this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        tag(ItemTags.SIGNS).add(Registry.ILLWOOD_PLANKS.getStandingSign().asItem(), Registry.POLISHED_PLANKS.getStandingSign().asItem());
        this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
    }
}
