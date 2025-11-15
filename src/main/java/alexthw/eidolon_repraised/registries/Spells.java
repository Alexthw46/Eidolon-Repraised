package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.spells.SignSequence;
import alexthw.eidolon_repraised.api.spells.Spell;
import alexthw.eidolon_repraised.common.deity.Deities;
import alexthw.eidolon_repraised.common.spell.*;
import alexthw.eidolon_repraised.recipe.ChantRecipe;
import alexthw.eidolon_repraised.recipe.CommandChantRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Spells {

    // local cache for faster lookup
    static final List<Spell> spells = new CopyOnWriteArrayList<>();
    static final Map<ResourceLocation, Spell> spellMap = new ConcurrentHashMap<>();

    public static Spell find(ResourceLocation loc) {
        return spellMap.getOrDefault(loc, null);
    }

    public static Spell find(SignSequence signs, Level world) {
        for (Spell spell : spells) if (spell.matches(signs)) return spell;
        for (RecipeHolder<ChantRecipe> chantRecipeH : world.getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_TYPE.get())) {
            ChantRecipe chantRecipe = chantRecipeH.value();
            if (chantRecipe.matches(signs)) {
                Spell spell = chantRecipe.getChant();
                spell.setSigns(signs);
                spells.add(spell);
                return spell;
            }
        }
        for (RecipeHolder<CommandChantRecipe> chantRecipeH : world.getRecipeManager().getAllRecipesFor(EidolonRecipes.COMMAND_CHANT_TYPE.get())) {
            ChantRecipe chantRecipe = chantRecipeH.value();
            if (chantRecipe.matches(signs)) {
                Spell spell = chantRecipe.getChant();
                spell.setSigns(signs);
                spells.add(spell);
                return spell;
            }
        }
        return null;
    }

    public static Spell registerWithFallback(Spell spell) {
        register(spell);
        spells.add(spell);
        return spell;
    }

    public static Spell register(Spell spell) {
        spellMap.put(spell.getRegistryName(), spell);
        ModConfigSpec spec;
        ModConfigSpec.Builder spellBuilder = new ModConfigSpec.Builder();
        spell.buildConfig(spellBuilder);
        spec = spellBuilder.build();
        spell.CONFIG = spec;
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, spec, spell.getRegistryName().getNamespace() + "/" + spell.getRegistryName().getPath() + ".toml");
        return spell;
    }


    public static List<Spell> getSpells() {
        return spells;
    }

    public static Map<ResourceLocation, Spell> getSpellMap() {
        return spellMap;
    }

    public static Spell DARK_PRAYER, DARKLIGHT_CHANT, DARK_ANIMAL_SACRIFICE, DARK_TOUCH, FROST_CHANT, DARK_VILLAGER_SACRIFICE, ZOMBIFY, ENTHRALL_UNDEAD, LIGHT_PRAYER, FIRE_CHANT, LIGHT_CHANT, HOLY_TOUCH, LAY_ON_HANDS, CURE_ZOMBIE_CHANT, SMITE_CHANT, SUNDER_ARMOR, BLESS_ARMOR, WATER_CHANT, UNDEAD_LURE;
    // dummy
    public static PrayerSpell CENSER;

    public static void init() {
        DARK_PRAYER = register(new PrayerSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "dark_prayer"),
                Deities.DARK_DEITY,
                Signs.WICKED_SIGN, Signs.WICKED_SIGN, Signs.WICKED_SIGN
        ));

        DARKLIGHT_CHANT = register(new LightSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "darklight_chant"), Deities.DARK_DEITY,
                Signs.WICKED_SIGN, Signs.FLAME_SIGN, Signs.WICKED_SIGN, Signs.FLAME_SIGN
        ));

        DARK_ANIMAL_SACRIFICE = register(new AnimalSacrificeSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "dark_animal_sacrifice"),
                Deities.DARK_DEITY, 3, 0.5,
                Signs.WICKED_SIGN, Signs.BLOOD_SIGN, Signs.WICKED_SIGN
        ));
        DARK_TOUCH = register(new DarkTouchSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "dark_touch"),
                Signs.WICKED_SIGN, Signs.SOUL_SIGN, Signs.WICKED_SIGN, Signs.SOUL_SIGN
        ));

        FROST_CHANT = register(new FrostSpell(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "frost_touch"),
                Signs.WICKED_SIGN, Signs.WINTER_SIGN, Signs.BLOOD_SIGN, Signs.WINTER_SIGN, Signs.WICKED_SIGN)
        );
        DARK_VILLAGER_SACRIFICE = register(new VillagerSacrificeSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "dark_villager_sacrifice"),
                Deities.DARK_DEITY, 6, 1,
                Signs.BLOOD_SIGN, Signs.WICKED_SIGN, Signs.BLOOD_SIGN, Signs.SOUL_SIGN
        ));

        ZOMBIFY = register(new ZombifySpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "zombify_villager"), 8, 1.25,
                Signs.DEATH_SIGN, Signs.BLOOD_SIGN, Signs.WICKED_SIGN, Signs.DEATH_SIGN, Signs.SOUL_SIGN, Signs.BLOOD_SIGN
        ));

        ENTHRALL_UNDEAD = register(new ThrallSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "enthrall_spell"),
                Signs.WICKED_SIGN, Signs.MIND_SIGN, Signs.MAGIC_SIGN, Signs.MAGIC_SIGN, Signs.MIND_SIGN
        ));

        LIGHT_PRAYER = register(new PrayerSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "light_prayer"),
                Deities.LIGHT_DEITY,
                Signs.SACRED_SIGN, Signs.SACRED_SIGN, Signs.SACRED_SIGN
        ));
        FIRE_CHANT = register(new FireTouchSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "fire_chant"),
                Signs.FLAME_SIGN, Signs.FLAME_SIGN, Signs.FLAME_SIGN
        ));
        LIGHT_CHANT = register(new LightSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "light_chant"), Deities.LIGHT_DEITY,
                Signs.SACRED_SIGN, Signs.FLAME_SIGN, Signs.SACRED_SIGN, Signs.FLAME_SIGN
        ));

        HOLY_TOUCH = register(new LightTouchSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "holy_touch"),
                Signs.SACRED_SIGN, Signs.SOUL_SIGN, Signs.SACRED_SIGN, Signs.SOUL_SIGN
        ));

        LAY_ON_HANDS = register(new HealSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "lay_on_hands"),
                Signs.FLAME_SIGN, Signs.SOUL_SIGN, Signs.SACRED_SIGN, Signs.SOUL_SIGN, Signs.SACRED_SIGN
        ));

        CURE_ZOMBIE_CHANT = register(new ConvertZombieSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "cure_zombie"), 8, 1.25,
                Signs.SACRED_SIGN, Signs.SOUL_SIGN, Signs.MIND_SIGN, Signs.HARMONY_SIGN, Signs.FLAME_SIGN, Signs.SOUL_SIGN
        ));

        SMITE_CHANT = register(new SmiteSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "smite_chant"),
                Signs.FLAME_SIGN, Signs.MAGIC_SIGN, Signs.SACRED_SIGN, Signs.DEATH_SIGN, Signs.MAGIC_SIGN, Signs.SACRED_SIGN
        ));

        SUNDER_ARMOR = register(new SunderArmorSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "sunder_armor"),
                Signs.FLAME_SIGN, Signs.MAGIC_SIGN, Signs.WICKED_SIGN, Signs.MAGIC_SIGN, Signs.FLAME_SIGN
        ));

        BLESS_ARMOR = register(new LightArmorSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "reinforce_armor"),
                Signs.SACRED_SIGN, Signs.WARDING_SIGN, Signs.SACRED_SIGN, Signs.WARDING_SIGN, Signs.SACRED_SIGN
        ));

        WATER_CHANT = register(new WaterSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "create_water"),
                Signs.WINTER_SIGN, Signs.WINTER_SIGN, Signs.FLAME_SIGN, Signs.FLAME_SIGN
        ));

        //TODO Undead Lure - neutral
        UNDEAD_LURE = register(new UndeadLureSpell(
                ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "undead_lure"),
                Signs.MIND_SIGN, Signs.MAGIC_SIGN, Signs.WICKED_SIGN

        ));
        CENSER = (PrayerSpell) register(new PrayerSpell(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "basic_incense"), Deities.LIGHT_DEITY));

    }
}
