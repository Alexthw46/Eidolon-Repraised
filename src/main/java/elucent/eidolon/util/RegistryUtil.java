package elucent.eidolon.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryUtil {


    public static ResourceLocation getRegistryName(ItemLike item) {
        if (item instanceof Item) {
            return ForgeRegistries.ITEMS.getKey((Item) item);
        } else if (item instanceof Block) {
            return ForgeRegistries.BLOCKS.getKey((Block) item);
        }
        return null; // Return null if the item is neither an Item nor a Block
    }

    public static ResourceLocation getRegistryName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation getRegistryName(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public static ResourceLocation getRegistryName(EntityType<?> entity) {
        return ForgeRegistries.ENTITY_TYPES.getKey(entity);
    }

}
