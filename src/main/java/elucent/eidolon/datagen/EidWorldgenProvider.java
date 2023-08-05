package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.Worldgen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EidWorldgenProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, Worldgen::bootstrapConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, Worldgen::bootstrapPlacedFeatures)
            //.add(ForgeRegistries.Keys.BIOME_MODIFIERS, EidWorldgenProvider::generateBiomeModifiers)
            //.add(ForgeRegistries.Keys.BIOMES, Worldgen.Biomes::registerBiomes)
            ;

    public EidWorldgenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Eidolon.MODID));
    }

}
