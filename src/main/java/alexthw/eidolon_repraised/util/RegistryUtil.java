package alexthw.eidolon_repraised.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import static alexthw.eidolon_repraised.Eidolon.prefix;

public class RegistryUtil {

    public static ResourceLocation getRegistryName(ItemLike itemlike){
        if (itemlike instanceof Item item) {
            return getRegistryName(item);
        } else if (itemlike instanceof Block block) {
            return getRegistryName(block);
        }
        return prefix("unknown");
    }

    public static ResourceLocation getRegistryName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation getRegistryName(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourceLocation getRegistryName(EntityType<?> entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity);
    }

}
