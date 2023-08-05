package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static elucent.eidolon.Eidolon.prefix;

public class EidBiomeTagProvider extends BiomeTagsProvider {
    public EidBiomeTagProvider(DataGenerator pGenerator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator.getPackOutput(), provider, Eidolon.MODID, existingFileHelper);
    }

    public static final TagKey<Biome> BANANA_SLUG_TAG = TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), prefix("banana_slug"));
    public static final TagKey<Biome> BROWN_SLUG_TAG = TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), prefix("brown_slug"));
    public static final TagKey<Biome> SLIMY_SLUG_TAG = TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), prefix("slimy_slug"));

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BANANA_SLUG_TAG).addTag(BiomeTags.IS_JUNGLE).addTag(Tags.Biomes.IS_LUSH);
        tag(BROWN_SLUG_TAG).addTag(BiomeTags.IS_TAIGA).addTag(Tags.Biomes.IS_COLD_OVERWORLD);
        tag(SLIMY_SLUG_TAG).addTag(BiomeTags.IS_FOREST).addTag(Tags.Biomes.IS_WET_OVERWORLD);
    }
}
