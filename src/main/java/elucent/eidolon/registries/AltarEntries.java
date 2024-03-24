package elucent.eidolon.registries;

import elucent.eidolon.api.altar.AltarEntry;
import elucent.eidolon.api.altar.AltarKeys;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class AltarEntries {
    static final Map<Block, AltarEntry> entries = new HashMap<>();

    public static AltarEntry find(Block state) {
        return entries.getOrDefault(state, null);
    }

    public static AltarEntry find(Item blockItem) {
        return entries.getOrDefault(Block.byItem(blockItem), null);
    }


    public static void init() {
        entries.put(Blocks.TORCH, new AltarEntry(AltarKeys.LIGHT_KEY).setPower(1));
        entries.put(Blocks.SOUL_TORCH, new AltarEntry(AltarKeys.LIGHT_KEY).setPower(1));
        entries.put(Blocks.LANTERN, new AltarEntry(AltarKeys.LIGHT_KEY).setPower(1).setCapacity(1));
        entries.put(Registry.CANDLE.get(), new AltarEntry(AltarKeys.LIGHT_KEY).setPower(2));
        entries.put(Registry.CANDLESTICK.get(), new AltarEntry(AltarKeys.LIGHT_KEY).setPower(2));
        entries.put(Registry.MAGIC_CANDLE.get(), new AltarEntry(AltarKeys.LIGHT_KEY).setPower(2));
        entries.put(Registry.MAGIC_CANDLESTICK.get(), new AltarEntry(AltarKeys.LIGHT_KEY).setPower(3));

        entries.put(Blocks.SKELETON_SKULL, new AltarEntry(AltarKeys.SKULL_KEY).setCapacity(2));
        entries.put(Blocks.ZOMBIE_HEAD, new AltarEntry(AltarKeys.SKULL_KEY).setCapacity(1).setPower(1));
        entries.put(Blocks.WITHER_SKELETON_SKULL, new AltarEntry(AltarKeys.SKULL_KEY).setCapacity(3).setPower(1));


        entries.put(Blocks.POTTED_WARPED_ROOTS, new AltarEntry(AltarKeys.PLANT_KEY).setPower(1));
        entries.put(Blocks.POTTED_CRIMSON_ROOTS, new AltarEntry(AltarKeys.PLANT_KEY).setPower(1));
        entries.put(Blocks.POTTED_WARPED_FUNGUS, new AltarEntry(AltarKeys.PLANT_KEY).setPower(2));
        entries.put(Blocks.POTTED_CRIMSON_FUNGUS, new AltarEntry(AltarKeys.PLANT_KEY).setPower(2));
        entries.put(Blocks.POTTED_WITHER_ROSE, new AltarEntry(AltarKeys.PLANT_KEY).setPower(3));
        entries.put(Registry.GOBLET.get(), new AltarEntry(AltarKeys.OFFERS_KEY).setCapacity(2));
        entries.put(Registry.CENSER.get(), new AltarEntry(AltarKeys.OFFERS_KEY).setCapacity(2));

    }
}
