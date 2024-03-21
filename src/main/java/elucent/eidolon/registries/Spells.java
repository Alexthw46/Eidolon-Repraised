package elucent.eidolon.registries;

import elucent.eidolon.Config;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.SignSequence;
import elucent.eidolon.api.spells.Spell;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.common.spell.*;
import elucent.eidolon.recipe.ChantRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

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
        for (ChantRecipe chantRecipe : world.getRecipeManager().getAllRecipesFor(EidolonRecipes.CHANT_TYPE.get()))
            if (chantRecipe.matches(signs)) {
                Spell spell = chantRecipe.getChant();
                spell.setSigns(signs);
                spells.add(spell);
                return spell;
            }
        return null;
    }

    public static Spell register(Spell spell) {
        spellMap.put(spell.getRegistryName(), spell);
        ForgeConfigSpec spec;
        ForgeConfigSpec.Builder spellBuilder = new ForgeConfigSpec.Builder();
        spell.buildConfig(spellBuilder);
        spec = spellBuilder.build();
        spell.CONFIG = spec;
        Config.SpellConfig anModConfig = new Config.SpellConfig(ModConfig.Type.SERVER, spell.CONFIG, ModLoadingContext.get().getActiveContainer(), spell.getRegistryName().getNamespace() + "/" + spell.getRegistryName().getPath());
        ModLoadingContext.get().getActiveContainer().addConfig(anModConfig);
        return spell;
    }


    public static List<Spell> getSpells() {
        return spells;
    }

    public static final Spell DARK_PRAYER = register(new PrayerSpell(
            new ResourceLocation(Eidolon.MODID, "dark_prayer"),
            Deities.DARK_DEITY,
            Signs.WICKED_SIGN, Signs.WICKED_SIGN, Signs.WICKED_SIGN
    ));

    public static final Spell DARKLIGHT_CHANT = register(new LightSpell(
            new ResourceLocation(Eidolon.MODID, "darklight_chant"), Deities.DARK_DEITY,
            Signs.WICKED_SIGN, Signs.FLAME_SIGN, Signs.WICKED_SIGN, Signs.FLAME_SIGN
    ));

    public static final Spell DARK_ANIMAL_SACRIFICE = register(new AnimalSacrificeSpell(
            new ResourceLocation(Eidolon.MODID, "dark_animal_sacrifice"),
            Deities.DARK_DEITY,
            Signs.WICKED_SIGN, Signs.BLOOD_SIGN, Signs.WICKED_SIGN
    ));
    public static final Spell DARK_TOUCH = register(new DarkTouchSpell(
            new ResourceLocation(Eidolon.MODID, "dark_touch"),
            Signs.WICKED_SIGN, Signs.SOUL_SIGN, Signs.WICKED_SIGN, Signs.SOUL_SIGN
    ));

    public static final Spell FROST_CHANT = register(new FrostSpell(new ResourceLocation(Eidolon.MODID, "frost_touch"),
            Signs.WICKED_SIGN, Signs.WINTER_SIGN, Signs.BLOOD_SIGN, Signs.WINTER_SIGN, Signs.WICKED_SIGN)
    );
    public static final Spell DARK_VILLAGER_SACRIFICE = register(new VillagerSacrificeSpell(
            new ResourceLocation(Eidolon.MODID, "dark_villager_sacrifice"),
            Deities.DARK_DEITY,
            Signs.BLOOD_SIGN, Signs.WICKED_SIGN, Signs.BLOOD_SIGN, Signs.SOUL_SIGN
    ));

    public static final Spell ZOMBIFY = register(new ZombifySpell(
            new ResourceLocation(Eidolon.MODID, "zombify_villager"),
            Signs.DEATH_SIGN, Signs.BLOOD_SIGN, Signs.WICKED_SIGN, Signs.DEATH_SIGN, Signs.SOUL_SIGN, Signs.BLOOD_SIGN
    ));

    public static final Spell ENTHRALL_UNDEAD = register(new ThrallSpell(
            new ResourceLocation(Eidolon.MODID, "enthrall_spell"),
            Signs.WICKED_SIGN, Signs.MIND_SIGN, Signs.MAGIC_SIGN, Signs.MAGIC_SIGN, Signs.MIND_SIGN
    ));

    public static final Spell LIGHT_PRAYER = register(new PrayerSpell(
            new ResourceLocation(Eidolon.MODID, "light_prayer"),
            Deities.LIGHT_DEITY,
            Signs.SACRED_SIGN, Signs.SACRED_SIGN, Signs.SACRED_SIGN
    ));
    public static final Spell FIRE_CHANT = register(new FireTouchSpell(
            new ResourceLocation(Eidolon.MODID, "fire_chant"),
            Signs.FLAME_SIGN, Signs.FLAME_SIGN, Signs.FLAME_SIGN
    ));
    public static final Spell LIGHT_CHANT = register(new LightSpell(
            new ResourceLocation(Eidolon.MODID, "light_chant"), Deities.LIGHT_DEITY,
            Signs.SACRED_SIGN, Signs.FLAME_SIGN, Signs.SACRED_SIGN, Signs.FLAME_SIGN
    ));

    public static final Spell HOLY_TOUCH = register(new LightTouchSpell(
            new ResourceLocation(Eidolon.MODID, "holy_touch"),
            Signs.SACRED_SIGN, Signs.SOUL_SIGN, Signs.SACRED_SIGN, Signs.SOUL_SIGN
    ));

    public static final Spell LAY_ON_HANDS = register(new HealSpell(
            new ResourceLocation(Eidolon.MODID, "lay_on_hands"),
            Signs.FLAME_SIGN, Signs.SOUL_SIGN, Signs.SACRED_SIGN, Signs.SOUL_SIGN, Signs.SACRED_SIGN
    ));

    public static final Spell CURE_ZOMBIE_CHANT = register(new ConvertZombieSpell(
            new ResourceLocation(Eidolon.MODID, "cure_zombie"),
            Signs.SACRED_SIGN, Signs.SOUL_SIGN, Signs.MIND_SIGN, Signs.HARMONY_SIGN, Signs.FLAME_SIGN, Signs.SOUL_SIGN
    ));

    public static final Spell SMITE_CHANT = register(new SmiteSpell(
            new ResourceLocation(Eidolon.MODID, "smite_chant"),
            Signs.FLAME_SIGN, Signs.MAGIC_SIGN, Signs.SACRED_SIGN, Signs.DEATH_SIGN, Signs.MAGIC_SIGN, Signs.SACRED_SIGN
    ));

    public static final Spell SUNDER_ARMOR = register(new SunderArmorSpell(
            new ResourceLocation(Eidolon.MODID, "sunder_armor"),
            Signs.FLAME_SIGN, Signs.MAGIC_SIGN, Signs.WICKED_SIGN, Signs.MAGIC_SIGN, Signs.FLAME_SIGN
    ));

    public static final Spell BLESS_ARMOR = register(new LightArmorSpell(
            new ResourceLocation(Eidolon.MODID, "reinforce_armor"),
            Signs.SACRED_SIGN, Signs.WARDING_SIGN, Signs.SACRED_SIGN, Signs.WARDING_SIGN, Signs.SACRED_SIGN
    ));

    public static final Spell WATER_CHANT = register(new WaterSpell(
            new ResourceLocation(Eidolon.MODID, "create_water"),
            Signs.WINTER_SIGN, Signs.WINTER_SIGN, Signs.FLAME_SIGN, Signs.FLAME_SIGN
    ));

    //TODO Undead Lure - neutral
    public static final Spell UNDEAD_LURE = register(new UndeadLureSpell(
            new ResourceLocation(Eidolon.MODID, "undead_lure"),
            Signs.MIND_SIGN, Signs.MAGIC_SIGN, Signs.WICKED_SIGN
    ));

    // dummy
    public static final PrayerSpell CENSER = new PrayerSpell(new ResourceLocation(Eidolon.MODID, "censer"), Deities.LIGHT_DEITY);
}
