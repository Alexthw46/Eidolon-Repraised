package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.api.altar.AltarEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.core.registries.BuiltInRegistries.BLOCK;

public class AltarEntries {
    static final Map<Block, AltarEntry> entries = new HashMap<>();

    public static AltarEntry find(Block block) {
        Optional<ResourceKey<Block>> resourceKey = BLOCK.getResourceKey(block);
        return resourceKey.map(blockResourceKey -> BLOCK.getData(EidolonDataMaps.ALTAR_ENTRY_MAP, blockResourceKey)).orElse(null);

    }

    public static AltarEntry find(Item blockItem) {
        return find(Block.byItem(blockItem));
    }


//    public static void init() {
//        entries.put(Blocks.TORCH, new AltarEntry(AltarKeys.LIGHT_KEY, 1));
//        entries.put(Blocks.SOUL_TORCH, new AltarEntry(AltarKeys.LIGHT_KEY, 1));
//        entries.put(Blocks.LANTERN, new AltarEntry(AltarKeys.LIGHT_KEY, 1, 1));
//        entries.put(Registry.CANDLE.get(), new AltarEntry(AltarKeys.LIGHT_KEY, 2));
//        entries.put(Registry.CANDLESTICK.get(), new AltarEntry(AltarKeys.LIGHT_KEY, 2));
//        entries.put(Registry.MAGIC_CANDLE.get(), new AltarEntry(AltarKeys.LIGHT_KEY, 2));
//        entries.put(Registry.MAGIC_CANDLESTICK.get(), new AltarEntry(AltarKeys.LIGHT_KEY, 3));
//
//        entries.put(Blocks.SKELETON_SKULL, new AltarEntry(AltarKeys.SKULL_KEY, 2));
//        entries.put(Blocks.ZOMBIE_HEAD, new AltarEntry(AltarKeys.SKULL_KEY, 1, 1));
//        entries.put(Blocks.WITHER_SKELETON_SKULL, new AltarEntry(AltarKeys.SKULL_KEY, 3, 1));
//
//        entries.put(Blocks.POTTED_WARPED_ROOTS, new AltarEntry(AltarKeys.PLANT_KEY, 1));
//        entries.put(Blocks.POTTED_CRIMSON_ROOTS, new AltarEntry(AltarKeys.PLANT_KEY, 1));
//        entries.put(Blocks.POTTED_WARPED_FUNGUS, new AltarEntry(AltarKeys.PLANT_KEY, 2));
//        entries.put(Blocks.POTTED_CRIMSON_FUNGUS, new AltarEntry(AltarKeys.PLANT_KEY, 2));
//        entries.put(Blocks.POTTED_WITHER_ROSE, new AltarEntry(AltarKeys.PLANT_KEY, 3));
//        //entries.put(Registry.POTTED_MIRECAP.get(), new AltarEntry(AltarKeys.PLANT_KEY,2));
//        entries.put(Registry.GOBLET.get(), new AltarEntry(AltarKeys.OFFERS_KEY, 2, 0));
//        entries.put(Registry.CENSER.get(), new AltarEntry(AltarKeys.OFFERS_KEY, 2, 0));
//
//    }

}
