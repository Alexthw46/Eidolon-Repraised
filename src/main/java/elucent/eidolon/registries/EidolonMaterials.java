package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;


public class EidolonMaterials {
    public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, Eidolon.MODID);

    // Warlock Robes
    public static final Holder<ArmorMaterial> WARLOCK_ROBES = MATERIALS.register("warlock_robes", () -> register(
            "warlock_robes",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 7);
                map.put(ArmorItem.Type.LEGGINGS, 0);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            25,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0f,
            0.0f,
            () -> Ingredient.of(new ItemStack(Registry.WICKED_WEAVE.get()))
    ));

    // Silver Armor
    public static final Holder<ArmorMaterial> SILVER_ARMOR = MATERIALS.register("silver_armor", () -> register(
            "silver_armor",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 4);
                map.put(ArmorItem.Type.BOOTS, 2);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_GOLD,
            0.0f,
            0.0f,
            () -> Ingredient.of(Registry.INGOTS_SILVER)
    ));

    // Top Hat
    public static final Holder<ArmorMaterial> TOP_HAT = MATERIALS.register("top_hat", () -> register(
            "top_hat",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 0);
                map.put(ArmorItem.Type.LEGGINGS, 0);
                map.put(ArmorItem.Type.BOOTS, 0);
            }),
            12,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0f,
            0.0f,
            () -> Ingredient.of(new ItemStack(Items.BLACK_WOOL))
    ));

    // Bonelord Armor
    public static final Holder<ArmorMaterial> BONELORD = MATERIALS.register("bonelord", () -> register(
            "bonelord",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 4);
                map.put(ArmorItem.Type.CHESTPLATE, 9);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 0);
            }),
            25,
            SoundEvents.ARMOR_EQUIP_TURTLE,
            2.0f,
            0.0f,
            () -> Ingredient.of(new ItemStack(Registry.IMBUED_BONES.get()))
    ));

    private static ArmorMaterial register(
            String pName,
            EnumMap<ArmorItem.Type, Integer> pDefense,
            int pEnchantmentValue,
            Holder<SoundEvent> pEquipSound,
            float pToughness,
            float pKnockbackResistance,
            Supplier<Ingredient> pRepairIngredient
    ) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(Eidolon.prefix(pName)));
        return register(pDefense, pEnchantmentValue, pEquipSound, pToughness, pKnockbackResistance, pRepairIngredient, list);
    }

    private static ArmorMaterial register(
            EnumMap<ArmorItem.Type, Integer> pDefense,
            int pEnchantmentValue,
            Holder<SoundEvent> pEquipSound,
            float pToughness,
            float pKnockbackResistance,
            Supplier<Ingredient> pRepairIngredient,
            List<ArmorMaterial.Layer> pLayers
    ) {
        EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

        for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
            enummap.put(armoritem$type, pDefense.get(armoritem$type));
        }

        return new ArmorMaterial(enummap, pEnchantmentValue, pEquipSound, pRepairIngredient, pLayers, pToughness, pKnockbackResistance);
    }


}
