package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.Registry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
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
        return ItemTags.create(new ResourceLocation("eidolon", name));
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ItemTags.MUSIC_DISCS).add(Registry.PAROUSIA_DISC.get());
        tag(SCRIBE_ITEMS).add(Items.CHARCOAL, Items.FEATHER, Items.BOOK, Registry.CANDLE.get().asItem(), Registry.PARCHMENT.get(), Registry.MAGIC_INK.get());
        tag(PATRON_SYMBOL).add(Registry.HOLY_SYMBOL.get(), Registry.UNHOLY_SYMBOL.get());
        this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);

    }
}
