package alexthw.eidolon_repraised.datagen;

import alexthw.eidolon_repraised.api.altar.AltarEntry;
import alexthw.eidolon_repraised.api.altar.AltarKeys;
import alexthw.eidolon_repraised.registries.EidolonDataMaps;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EidDataMapProvider extends DataMapProvider {
    public EidDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        this.builder(EidolonDataMaps.ALTAR_ENTRY_MAP)
                .add(Blocks.TORCH.builtInRegistryHolder(), new AltarEntry(AltarKeys.LIGHT_KEY, 0.0, 1.0), false)
                .add(Blocks.SOUL_TORCH.builtInRegistryHolder(), new AltarEntry(AltarKeys.LIGHT_KEY, 0.0, 1.0), false)
                .add(Blocks.LANTERN.builtInRegistryHolder(), new AltarEntry(AltarKeys.LIGHT_KEY, 1.0, 1.0), false)
                .add(Registry.CANDLE.get().builtInRegistryHolder(), new AltarEntry(AltarKeys.LIGHT_KEY, 0.0, 2.0), false)
                .add(Registry.CANDLESTICK.get().builtInRegistryHolder(), new AltarEntry(AltarKeys.LIGHT_KEY, 0.0, 2.0), false)
                .add(Registry.MAGIC_CANDLE.get().builtInRegistryHolder(), new AltarEntry(AltarKeys.LIGHT_KEY, 0.0, 2.0), false)
                .add(Registry.MAGIC_CANDLESTICK.get().builtInRegistryHolder(), new AltarEntry(AltarKeys.LIGHT_KEY, 0.0, 3.0), false)

                .add(Blocks.SKELETON_SKULL.builtInRegistryHolder(), new AltarEntry(AltarKeys.SKULL_KEY, 2.0, 0.0), false)
                .add(Blocks.ZOMBIE_HEAD.builtInRegistryHolder(), new AltarEntry(AltarKeys.SKULL_KEY, 1.0, 1.0), false)
                .add(Blocks.WITHER_SKELETON_SKULL.builtInRegistryHolder(), new AltarEntry(AltarKeys.SKULL_KEY, 3.0, 1.0), false)

                .add(Blocks.POTTED_WARPED_ROOTS.builtInRegistryHolder(), new AltarEntry(AltarKeys.PLANT_KEY, 0.0, 1.0), false)
                .add(Blocks.POTTED_CRIMSON_ROOTS.builtInRegistryHolder(), new AltarEntry(AltarKeys.PLANT_KEY, 0.0, 1.0), false)
                .add(Blocks.POTTED_WARPED_FUNGUS.builtInRegistryHolder(), new AltarEntry(AltarKeys.PLANT_KEY, 0.0, 2.0), false)
                .add(Blocks.POTTED_CRIMSON_FUNGUS.builtInRegistryHolder(), new AltarEntry(AltarKeys.PLANT_KEY, 0.0, 2.0), false)
                .add(Blocks.POTTED_WITHER_ROSE.builtInRegistryHolder(), new AltarEntry(AltarKeys.PLANT_KEY, 0.0, 3.0), false)
                //.add(Registry.POTTED_MIRECAP.get().builtInRegistryHolder(), new AltarEntry(AltarKeys.PLANT_KEY, 0.0, 2.0), false)

                .add(Registry.GOBLET.get().builtInRegistryHolder(), new AltarEntry(AltarKeys.OFFERS_KEY, 2.0, 0.0), false)
                .add(Registry.CENSER.get().builtInRegistryHolder(), new AltarEntry(AltarKeys.OFFERS_KEY, 2.0, 0.0), false);
    }

}