package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import static elucent.eidolon.Eidolon.prefix;

public class EidBiomeTagProvider extends BiomeTagsProvider {
    public EidBiomeTagProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, Eidolon.MODID, existingFileHelper);
    }

    public static final TagKey<Biome> BANANA_SLUG_TAG = TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), prefix("banana_slug"));
    public static final TagKey<Biome> BROWN_SLUG_TAG = TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), prefix("brown_slug"));
    public static final TagKey<Biome> SLIMY_SLUG_TAG = TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), prefix("slimy_slug"));

    @Override
    protected void addTags() {
        tag(BANANA_SLUG_TAG).addTag(BiomeTags.IS_JUNGLE).addTag(Tags.Biomes.IS_LUSH);
        tag(BROWN_SLUG_TAG).addTag(BiomeTags.IS_TAIGA).addTag(Tags.Biomes.IS_COLD_OVERWORLD);
        tag(SLIMY_SLUG_TAG).addTag(BiomeTags.IS_FOREST).addTag(Tags.Biomes.IS_WET_OVERWORLD);
    }
}
