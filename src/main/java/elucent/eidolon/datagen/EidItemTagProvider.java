package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import elucent.eidolon.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
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

    @NotNull
    private static TagKey<Item> eidolonItemTag(String name) {
        return ItemTags.create(new ResourceLocation("eidolon", name));
    }

    @Override
    protected void addTags() {

        tag(SCRIBE_ITEMS).add(Items.CHARCOAL, Items.FEATHER, Items.BOOK, Registry.CANDLE.get().asItem(), Registry.PARCHMENT.get(), Registry.MAGIC_INK.get());

    }
}
