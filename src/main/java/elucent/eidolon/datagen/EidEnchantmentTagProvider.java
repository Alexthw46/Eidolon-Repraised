package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class EidEnchantmentTagProvider extends ForgeRegistryTagsProvider<Enchantment> {
    public static TagKey<Enchantment> SOUL_ENCHANTER_BLACKLIST = TagKey.create(Registry.ENCHANTMENT_REGISTRY, new ResourceLocation(Eidolon.MODID, "soul_enchanter_blacklist"));

    public EidEnchantmentTagProvider(final DataGenerator generator, @Nullable final ExistingFileHelper helper) {
        super(generator, ForgeRegistries.ENCHANTMENTS, Eidolon.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(SOUL_ENCHANTER_BLACKLIST);
    }
}
