package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EidEnchantmentTagProvider extends TagsProvider<Enchantment> {
    public static TagKey<Enchantment> SOUL_ENCHANTER_BLACKLIST = TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "soul_enchanter_blacklist"));

    public EidEnchantmentTagProvider(final DataGenerator generator, CompletableFuture<HolderLookup.Provider> provider, @Nullable final ExistingFileHelper helper) {
        super(generator.getPackOutput(), Registries.ENCHANTMENT, provider, Eidolon.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        tag(SOUL_ENCHANTER_BLACKLIST);
    }
}
