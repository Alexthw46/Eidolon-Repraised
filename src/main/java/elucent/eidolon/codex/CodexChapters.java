package elucent.eidolon.codex;

import elucent.eidolon.capability.Facts;
import elucent.eidolon.codex.IndexPage.FactLockedEntry;
import elucent.eidolon.codex.IndexPage.IndexEntry;
import elucent.eidolon.codex.IndexPage.ResearchLockedEntry;
import elucent.eidolon.codex.IndexPage.SignLockedEntry;
import elucent.eidolon.codex.ListPage.ListEntry;
import elucent.eidolon.codex.SignIndexPage.SignEntry;
import elucent.eidolon.registries.*;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static elucent.eidolon.Eidolon.prefix;

public class CodexChapters {
    static public final List<Category> categories = new CopyOnWriteArrayList<>();
    static Category NATURE, RITUALS, ARTIFICE, THEURGY, SIGNS, SPELLS;

    static Index NATURE_INDEX, RITUALS_INDEX, ARTIFICE_INDEX, THEURGY_INDEX, SIGNS_INDEX, SPELLS_INDEX;
    static Chapter MONSTERS, CRITTERS, ORES, PEWTER, ENCHANTED_ASH, PLANTS, RESEARCHES, DECORATIONS,
            BRAZIER, ITEM_PROVIDERS, CRYSTAL_RITUAL, SUMMON_RITUAL, ALLURE_RITUAL, REPELLING_RITUAL, DECEIT_RITUAL, TIME_RITUALS, PURIFY_RITUAL, SANGUINE_RITUAL, RECHARGE_RITUAL, CAPTURE_RITUAL,
            WOODEN_STAND, TALLOW, CRUCIBLE, ARCANE_GOLD, REAGENTS, SOUL_GEMS, SHADOW_GEM, WARPED_SPROUTS, BASIC_ALCHEMY, INLAYS, BASIC_BAUBLES, MAGIC_WORKBENCH, VOID_AMULET, WARDED_MAIL, SOULFIRE_WAND, BONECHILL_WAND, REAPER_SCYTHE, CLEAVING_AXE, SOUL_ENCHANTER, REVERSAL_PICK, WARLOCK_ARMOR, GRAVITY_BELT, PRESTIGIOUS_PALM, MIND_SHIELDING_PLATE, RESOLUTE_BELT, GLASS_HAND, SOULBONE, RAVEN_CLOAK, ARROW_RING, NECROMANCER_STAFF,
            INTRO_SIGNS, EFFIGY, ALTARS, ALTAR_LIGHTS, ALTAR_SKULLS, ALTAR_HERBS, GOBLET, CENSER, DARK_PRAYER, ANIMAL_SACRIFICE, DARK_TOUCH, STONE_ALTAR, UNHOLY_EFFIGY, HOLY_EFFIGY, VILLAGER_SACRIFICE, LIGHT_PRAYER, INCENSE_BURN, HEAL, HOLY_TOUCH,
            WICKED_SIGN, SACRED_SIGN, BLOOD_SIGN, SOUL_SIGN, MIND_SIGN, FLAME_SIGN, WINTER_SIGN, HARMONY_SIGN, DEATH_SIGN, WARDING_SIGN, MAGIC_SIGN,
            MANA, LIGHT, FIRE_TOUCH, CHILL_TOUCH, ZOMBIFY, CURE_ZOMBIE, ENTHRALL, SMITE;

    public static void init() {

        //NATURE
        {
            MONSTERS = new Chapter(
                    "eidolon.codex.chapter.monsters",
                    new TitlePage("eidolon.codex.page.monsters.zombie_brute"),
                    new EntityPage(EidolonEntities.ZOMBIE_BRUTE.get()),
                    new TitlePage("eidolon.codex.page.monsters.wraith"),
                    new EntityPage(EidolonEntities.WRAITH.get()),
                    new TitlePage("eidolon.codex.page.monsters.chilled"),
                    new TextPage(""),
                    new EntityPage(EidolonEntities.GIANT_SKEL.get()),
                    new TitlePage("eidolon.codex.page.monsters.giant_skeleton"),
                    new EntityPage(EidolonEntities.NECROMANCER.get()),
                    new TitlePage("eidolon.codex.page.monsters.necromancer")
            );

            CRITTERS = new Chapter(
                    "eidolon.codex.chapter.critters",
                    new TitlePage("eidolon.codex.page.critters.raven"),
                    new EntityPage(EidolonEntities.RAVEN.get()),
                    new TitlePage("eidolon.codex.page.critters.slimy_slug"),
                    new EntityPage(EidolonEntities.SLIMY_SLUG.get())
            );

            ORES = new Chapter(
                    "eidolon.codex.chapter.ores",
                    new TitlePage("eidolon.codex.page.ores.lead_ore"),
                    new TitlePage("eidolon.codex.page.ores.silver_ore"),
                    new SmeltingPage(new ItemStack(Registry.LEAD_INGOT.get()), new ItemStack(Registry.LEAD_ORE.get()), prefix("smelt_lead_ore")),
                    new SmeltingPage(new ItemStack(Registry.SILVER_INGOT.get()), new ItemStack(Registry.SILVER_ORE.get()), prefix("smelt_silver_ore")),
                    new CraftingPage(Registry.LEAD_BLOCK.get().asItem()),
                    new CraftingPage(Registry.SILVER_BLOCK.get().asItem()),
                    new CraftingPage(new ItemStack(Registry.LEAD_NUGGET.get(), 9), prefix("decompress_lead_ingot")),
                    new CraftingPage(new ItemStack(Registry.SILVER_NUGGET.get(), 9), prefix("decompress_silver_ingot"))
            );

            PEWTER = new Chapter(
                    "eidolon.codex.chapter.pewter",
                    new TitlePage("eidolon.codex.page.pewter"),
                    new CraftingPage(new ItemStack(Registry.PEWTER_BLEND.get(), 2)),
                    new SmeltingPage(new ItemStack(Registry.PEWTER_INGOT.get()), new ItemStack(Registry.PEWTER_BLEND.get()), prefix("smelt_pewter_blend")),
                    new CraftingPage(Registry.PEWTER_BLOCK.get().asItem()),
                    new CraftingPage(new ItemStack(Registry.PEWTER_NUGGET.get(), 9), prefix("decompress_pewter_ingot"))
            );

            ENCHANTED_ASH = new Chapter(
                    "eidolon.codex.chapter.enchanted_ash",
                    new TitlePage("eidolon.codex.page.enchanted_ash"),
                    new SmeltingPage(new ItemStack(Registry.ENCHANTED_ASH.get(), 2), new ItemStack(Items.BONE))
            );

            PLANTS = new Chapter(
                    "eidolon.codex.chapter.plants",
                    new TitlePage("eidolon.codex.page.plants"),
                    new TextPage("eidolon.codex.page.plants.1"),
                    new WorktablePage(Registry.ATHAME.get()),
                    new WorktablePage(Registry.PLANTER.get().asItem()),
                    new CraftingPage(Registry.MERAMMER_RESIN.get())
            );

            RESEARCHES = new Chapter(
                    "eidolon.codex.chapter.researches",
                    new TitlePage("eidolon.codex.page.researches.0"),
                    new CraftingPage(Registry.RESEARCH_TABLE.get().asItem()),
                    new CruciblePage(new ItemStack(Registry.MAGICIANS_WAX.get(), 4)),
                    new CraftingPage(new ItemStack(Registry.ARCANE_SEAL.get(), 2)),
                    new CruciblePage(new ItemStack(Registry.MAGIC_INK.get(), 2)),
                    new CruciblePage(new ItemStack(Registry.PARCHMENT.get(), 4)),
                    new CraftingPage(Registry.NOTETAKING_TOOLS.get())
            );

            DECORATIONS = new Chapter(
                    "eidolon.codex.chapter.decorations",
                    new TitlePage("eidolon.codex.page.decorations"),
                    new CruciblePage(new ItemStack(Registry.ELDER_BRICK.get(), 16)),
                    new CraftingPage(new ItemStack(Registry.ELDER_BRICKS.getBlock(), 4)),
                    new CraftingPage(Registry.BONE_PILE.getBlock().asItem())
            );

            NATURE_INDEX = new Index(
                    "eidolon.codex.chapter.nature_index",
                    new TitledIndexPage("eidolon.codex.page.nature_index.0",
                            new IndexEntry(MONSTERS, new ItemStack(Registry.TATTERED_CLOTH.get())),
                            new IndexEntry(CRITTERS, new ItemStack(Registry.RAVEN_FEATHER.get())),
                            new IndexEntry(ORES, new ItemStack(Registry.LEAD_ORE.get())),
                            new IndexEntry(PEWTER, new ItemStack(Registry.PEWTER_INGOT.get())),
                            new IndexEntry(ENCHANTED_ASH, new ItemStack(Registry.ENCHANTED_ASH.get())),
                            new IndexEntry(PLANTS, new ItemStack(Registry.OANNA_BLOOM.get()))
                    ),
                    new IndexPage(
                            new IndexEntry(RESEARCHES, new ItemStack(Registry.RESEARCH_TABLE.get())),
                            new IndexEntry(DECORATIONS, new ItemStack(Registry.ELDER_BRICKS_EYE.get()))
                    )
            );

            categories.add(NATURE = new Category(
                    "nature",
                    new ItemStack(Registry.ZOMBIE_HEART.get()),
                    ColorUtil.packColor(255, 89, 143, 76),
                    NATURE_INDEX
            ));
        }

        //RITUALS
        {
            BRAZIER = new Chapter(
                    "eidolon.codex.chapter.brazier",
                    new TitlePage("eidolon.codex.page.brazier.0"),
                    new TextPage("eidolon.codex.page.brazier.1"),
                    new CraftingPage(Registry.BRAZIER.get().asItem())
            );

            ITEM_PROVIDERS = new Chapter(
                    "eidolon.codex.chapter.item_providers",
                    new TitlePage("eidolon.codex.page.item_providers.0"),
                    new CraftingPage(Registry.STONE_HAND.get().asItem()),
                    new TitlePage("eidolon.codex.page.item_providers.1"),
                    new CraftingPage(Registry.NECROTIC_FOCUS.get().asItem())
            );

            CRYSTAL_RITUAL = new Chapter(
                    "eidolon.codex.chapter.crystal_ritual",
                    new TitledRitualPage("eidolon.codex.page.crystal_ritual", RitualRegistry.CRYSTAL_RITUAL),
                    new TextPage("eidolon.codex.page.crystal_ritual")
            );

            SUMMON_RITUAL = new Chapter(
                    "eidolon.codex.chapter.summon_ritual",
                    new TitledRitualPage("eidolon.codex.page.summon_ritual.0", prefix("summon_zombie")),
                    new TextPage("eidolon.codex.page.summon_ritual.0"),
                    new TitledRitualPage("eidolon.codex.page.summon_ritual.1", prefix("summon_skeleton")),
                    new TitledRitualPage("eidolon.codex.page.summon_ritual.2", prefix("summon_phantom")),
                    new TitledRitualPage("eidolon.codex.page.summon_ritual.3", prefix("summon_wither_skeleton")),
                    new TitledRitualPage("eidolon.codex.page.summon_ritual.4", prefix("summon_husk")),
                    new TitledRitualPage("eidolon.codex.page.summon_ritual.5", prefix("summon_drowned")),
                    new TitledRitualPage("eidolon.codex.page.summon_ritual.6", prefix("summon_stray")),
                    new TitledRitualPage("eidolon.codex.page.summon_ritual.7", prefix("summon_wraith"))
            );

            ALLURE_RITUAL = new Chapter(
                    "eidolon.codex.chapter.allure_ritual",
                    new TitledRitualPage("eidolon.codex.page.allure_ritual", RitualRegistry.ALLURE_RITUAL),
                    new TextPage("eidolon.codex.page.allure_ritual")
            );

            REPELLING_RITUAL = new Chapter(
                    "eidolon.codex.chapter.repelling_ritual",
                    new TitledRitualPage("eidolon.codex.page.repelling_ritual", RitualRegistry.REPELLING_RITUAL),
                    new TextPage("eidolon.codex.page.repelling_ritual")
            );

            DECEIT_RITUAL = new Chapter(
                    "eidolon.codex.chapter.deceit_ritual",
                    new TitledRitualPage("eidolon.codex.page.deceit_ritual", RitualRegistry.DECEIT_RITUAL),
                    new TextPage("eidolon.codex.page.deceit_ritual")
            );

            TIME_RITUALS = new Chapter(
                    "eidolon.codex.chapter.time_rituals",
                    new TitledRitualPage("eidolon.codex.page.time_rituals.0", RitualRegistry.DAYLIGHT_RITUAL),
                    new TextPage("eidolon.codex.page.time_rituals.0"),
                    new TitledRitualPage("eidolon.codex.page.time_rituals.1", RitualRegistry.MOONLIGHT_RITUAL),
                    new TextPage("eidolon.codex.page.time_rituals.1")
            );

            PURIFY_RITUAL = new Chapter(
                    "eidolon.codex.chapter.purify_ritual",
                    new TitledRitualPage("eidolon.codex.page.purify_ritual", RitualRegistry.PURIFY_RITUAL),
                    new TextPage("eidolon.codex.page.purify_ritual")
            );

            SANGUINE_RITUAL = new Chapter(
                    "eidolon.codex.chapter.sanguine_ritual",
                    new TitledRitualPage("eidolon.codex.page.sanguine_ritual.0", Registry.SAPPING_SWORD.get().getDefaultInstance()),
                    new TextPage("eidolon.codex.page.sanguine_ritual.0"),
                    new TitledRitualPage("eidolon.codex.page.sanguine_ritual.1", Registry.SANGUINE_AMULET.get().getDefaultInstance()),
                    new TextPage("eidolon.codex.page.sanguine_ritual.1")
            );
            RECHARGE_RITUAL = new Chapter(
                    "eidolon.codex.chapter.recharge_ritual",
                    new TitledRitualPage("eidolon.codex.page.recharge_ritual.soulfire", RitualRegistry.RECHARGE_SOULFIRE_RITUAL),
                    new TitledRitualPage("eidolon.codex.page.recharge_ritual.bonechill", RitualRegistry.RECHARGE_BONECHILL_RITUAL),
                    new TextPage("eidolon.codex.page.recharge_ritual")
            );
            CAPTURE_RITUAL = new Chapter(
                    "eidolon.codex.chapter.capture_ritual",
                    new TitledRitualPage("eidolon.codex.page.capture_ritual", RitualRegistry.ABSORB_RITUAL),
                    new TextPage("eidolon.codex.page.capture_ritual")
            );

            RITUALS_INDEX = new Index(
                    "eidolon.codex.chapter.rituals",
                    new TitledIndexPage("eidolon.codex.page.rituals.0",
                            new IndexEntry(BRAZIER, new ItemStack(Registry.BRAZIER.get())),
                            new IndexEntry(ITEM_PROVIDERS, new ItemStack(Registry.STONE_HAND.get())),
                            new IndexEntry(CRYSTAL_RITUAL, new ItemStack(Registry.SOUL_SHARD.get())),
                            new IndexEntry(SUMMON_RITUAL, new ItemStack(Items.ROTTEN_FLESH)),
                            new IndexEntry(ALLURE_RITUAL, new ItemStack(Items.CARROT_ON_A_STICK)),
                            new IndexEntry(REPELLING_RITUAL, new ItemStack(Items.SHIELD))
                    ),
                    new IndexPage(
                            new IndexEntry(DECEIT_RITUAL, new ItemStack(Items.EMERALD)),
                            new IndexEntry(TIME_RITUALS, new ItemStack(Items.CLOCK)),
                            new IndexEntry(PURIFY_RITUAL, new ItemStack(Items.GOLDEN_APPLE)),
                            new IndexEntry(SANGUINE_RITUAL, new ItemStack(Registry.SANGUINE_AMULET.get())),
                            new IndexEntry(RECHARGE_RITUAL, new ItemStack(Registry.SOULFIRE_WAND.get())),
                            new IndexEntry(CAPTURE_RITUAL, new ItemStack(Registry.SUMMONING_STAFF.get()))
                    )
            );

            categories.add(RITUALS = new Category(
                    "rituals",
                    new ItemStack(Registry.LESSER_SOUL_GEM.get()),
                    ColorUtil.packColor(255, 223, 178, 43),
                    RITUALS_INDEX
            ));

        }

        //ARTIFICE
        {

            WOODEN_STAND = new Chapter(
                    "eidolon.codex.chapter.wooden_stand",
                    new TitlePage("eidolon.codex.page.wooden_stand.0"),
                    new CraftingPage(Registry.WOODEN_STAND.get().asItem()),
                    new TitlePage("eidolon.codex.page.wooden_stand.1"),
                    new CruciblePage(new ItemStack(Registry.FUNGUS_SPROUTS.get(), 2))
            );

            TALLOW = new Chapter(
                    "eidolon.codex.chapter.tallow",
                    new TitlePage("eidolon.codex.page.tallow.0"),
                    new SmeltingPage(new ItemStack(Registry.TALLOW.get()), new ItemStack(Items.ROTTEN_FLESH)),
                    new TitlePage("eidolon.codex.page.tallow.1"),
                    new CraftingPage(new ItemStack(Registry.CANDLE.get(), 4)),
                    new CraftingPage(Registry.CANDLESTICK.get().asItem())
            );

            CRUCIBLE = new Chapter(
                    "eidolon.codex.chapter.crucible",
                    new TitlePage("eidolon.codex.page.crucible.0"),
                    new TextPage("eidolon.codex.page.crucible.1"),
                    new CraftingPage(Registry.CRUCIBLE.get().asItem())
            );

            ARCANE_GOLD = new Chapter(
                    "eidolon.codex.chapter.arcane_gold",
                    new TitlePage("eidolon.codex.page.arcane_gold"),
                    new CruciblePage(new ItemStack(Registry.ARCANE_GOLD_INGOT.get(), 2), prefix("arcane_gold_ingot_alchemy")),
                    new CraftingPage(new ItemStack(Registry.ARCANE_GOLD_BLOCK.get())),
                    new CraftingPage(new ItemStack(Registry.ARCANE_GOLD_NUGGET.get(), 9), prefix("decompress_arcane_gold_ingot"))
            );

            REAGENTS = new Chapter(
                    "eidolon.codex.chapter.reagents",
                    new TitlePage("eidolon.codex.page.reagents.0"),
                    new CruciblePage(new ItemStack(Registry.SULFUR.get(), 2)),
                    new TitlePage("eidolon.codex.page.reagents.1"),
                    new CruciblePage(new ItemStack(Registry.DEATH_ESSENCE.get(), 4)),
                    new TitlePage("eidolon.codex.page.reagents.2"),
                    new CruciblePage(new ItemStack(Registry.CRIMSON_ESSENCE.get(), 4), prefix("crimson_essence_fungus")),
                    new CruciblePage(new ItemStack(Registry.CRIMSON_ESSENCE.get(), 2), prefix("crimson_essence_vines")),
                    new CruciblePage(new ItemStack(Registry.CRIMSON_ESSENCE.get(), 2), prefix("crimson_essence_roots")),
                    new TitlePage("eidolon.codex.page.reagents.3"),
                    new CruciblePage(new ItemStack(Registry.ENDER_CALX.get(), 2))
            );

            SOUL_GEMS = new Chapter(
                    "eidolon.codex.chapter.soul_gems",
                    new TitlePage("eidolon.codex.page.soul_gems"),
                    new CruciblePage(Registry.LESSER_SOUL_GEM.get())
            );

            SHADOW_GEM = new Chapter(
                    "eidolon.codex.chapter.shadow_gem",
                    new TitlePage("eidolon.codex.page.shadow_gem"),
                    new CruciblePage(Registry.SHADOW_GEM.get())
            );

            WARPED_SPROUTS = new Chapter(
                    "eidolon.codex.chapter.warped_sprouts",
                    new TitlePage("eidolon.codex.page.warped_sprouts.0"),
                    new CruciblePage(new ItemStack(Registry.WARPED_SPROUTS.get(), 2)),
                    new TitlePage("eidolon.codex.page.warped_sprouts.1")
            );

            BASIC_ALCHEMY = new Chapter(
                    "eidolon.codex.chapter.basic_alchemy",
                    new TitlePage("eidolon.codex.page.basic_alchemy.0"),
                    new CruciblePage(new ItemStack(Items.LEATHER), prefix("flesh_to_leather")),
                    new TitlePage("eidolon.codex.page.basic_alchemy.1"),
                    new CruciblePage(new ItemStack(Items.ROTTEN_FLESH), prefix("meat_to_flesh")),
                    new TitlePage("eidolon.codex.page.basic_alchemy.2"),
                    new CruciblePage(new ItemStack(Items.GUNPOWDER, 4), prefix("gunpowder_alchemy")),
                    new TitlePage("eidolon.codex.page.basic_alchemy.3"),
                    new CruciblePage(new ItemStack(Items.GOLDEN_APPLE), prefix("gilding_apple")),
                    new CruciblePage(new ItemStack(Items.GOLDEN_CARROT), prefix("gilding_carrot")),
                    new CruciblePage(new ItemStack(Items.GLISTERING_MELON_SLICE), prefix("gilding_melon"))
            );

            INLAYS = new Chapter(
                    "eidolon.codex.chapter.inlays",
                    new TitlePage("eidolon.codex.page.inlays"),
                    new CraftingPage(new ItemStack(Registry.PEWTER_INLAY.get(), 2)),
                    new CraftingPage(new ItemStack(Registry.GOLD_INLAY.get(), 2))
            );

            BASIC_BAUBLES = new Chapter(
                    "eidolon.codex.chapter.basic_baubles",
                    new TitlePage("eidolon.codex.page.basic_baubles"),
                    new CraftingPage(Registry.BASIC_AMULET.get()),
                    new CraftingPage(Registry.BASIC_RING.get()),
                    new CraftingPage(Registry.BASIC_BELT.get())
            );

            MAGIC_WORKBENCH = new Chapter(
                    "eidolon.codex.chapter.magic_workbench",
                    new TitlePage("eidolon.codex.page.magic_workbench"),
                    new CraftingPage(Registry.WORKTABLE.get().asItem())
            );

            VOID_AMULET = new Chapter(
                    "eidolon.codex.chapter.void_amulet",
                    new TitlePage("eidolon.codex.page.void_amulet"),
                    new WorktablePage(Registry.VOID_AMULET.get())
            );

            WARDED_MAIL = new Chapter(
                    "eidolon.codex.chapter.warded_mail",
                    new TitlePage("eidolon.codex.page.warded_mail"),
                    new WorktablePage(Registry.WARDED_MAIL.get())
            );

            SOULFIRE_WAND = new Chapter(
                    "eidolon.codex.chapter.soulfire_wand",
                    new TitlePage("eidolon.codex.page.soulfire_wand"),
                    new WorktablePage(Registry.SOULFIRE_WAND.get())
            );

            BONECHILL_WAND = new Chapter(
                    "eidolon.codex.chapter.bonechill_wand",
                    new TitlePage("eidolon.codex.page.bonechill_wand"),
                    new WorktablePage(Registry.BONECHILL_WAND.get())
            );

            REAPER_SCYTHE = new Chapter(
                    "eidolon.codex.chapter.reaper_scythe",
                    new TitlePage("eidolon.codex.page.reaper_scythe"),
                    new WorktablePage(Registry.REAPER_SCYTHE.get()),
                    new TitlePage("eidolon.codex.page.death_scythe"),
                    new WorktablePage(Registry.DEATHBRINGER_SCYTHE.get())
            );

            CLEAVING_AXE = new Chapter(
                    "eidolon.codex.chapter.cleaving_axe",
                    new TitlePage("eidolon.codex.page.cleaving_axe"),
                    new WorktablePage(Registry.CLEAVING_AXE.get())
            );

            SOUL_ENCHANTER = new Chapter(
                    "eidolon.codex.chapter.soul_enchanter",
                    new TitlePage("eidolon.codex.page.soul_enchanter.0"),
                    new TextPage("eidolon.codex.page.soul_enchanter.1"),
                    new WorktablePage(Registry.SOUL_ENCHANTER.get().asItem())
            );

            REVERSAL_PICK = new Chapter(
                    "eidolon.codex.chapter.reversal_pick",
                    new TitlePage("eidolon.codex.page.reversal_pick"),
                    new WorktablePage(Registry.REVERSAL_PICK.get())
            );

            WARLOCK_ARMOR = new Chapter(
                    "eidolon.codex.chapter.warlock_armor",
                    new TitlePage("eidolon.codex.page.warlock_armor.0"),
                    new WorktablePage(new ItemStack(Registry.WICKED_WEAVE.get(), 8)),
                    new TitlePage("eidolon.codex.page.warlock_armor.1"),
                    new WorktablePage(Registry.WARLOCK_HAT.get()),
                    new TitlePage("eidolon.codex.page.warlock_armor.2"),
                    new WorktablePage(Registry.WARLOCK_CLOAK.get()),
                    new TitlePage("eidolon.codex.page.warlock_armor.3"),
                    new WorktablePage(Registry.WARLOCK_BOOTS.get())
            );

            GRAVITY_BELT = new Chapter(
                    "eidolon.codex.chapter.gravity_belt",
                    new TitlePage("eidolon.codex.page.gravity_belt"),
                    new WorktablePage(Registry.GRAVITY_BELT.get())
            );

            PRESTIGIOUS_PALM = new Chapter(
                    "eidolon.codex.chapter.prestigious_palm",
                    new TitlePage("eidolon.codex.page.prestigious_palm"),
                    new WorktablePage(Registry.PRESTIGIOUS_PALM.get())
            );

            MIND_SHIELDING_PLATE = new Chapter(
                    "eidolon.codex.chapter.mind_shielding_plate",
                    new TitlePage("eidolon.codex.page.mind_shielding_plate"),
                    new WorktablePage(Registry.MIND_SHIELDING_PLATE.get())
            );

            RESOLUTE_BELT = new Chapter(
                    "eidolon.codex.chapter.resolute_belt",
                    new TitlePage("eidolon.codex.page.resolute_belt"),
                    new WorktablePage(Registry.RESOLUTE_BELT.get())
            );

            GLASS_HAND = new Chapter(
                    "eidolon.codex.chapter.glass_hand",
                    new TitlePage("eidolon.codex.page.glass_hand"),
                    new WorktablePage(Registry.GLASS_HAND.get())
            );

            SOULBONE = new Chapter(
                    "eidolon.codex.chapter.soulbone_amulet",
                    new TitlePage("eidolon.codex.page.soulbone_amulet"),
                    new TitlePage("eidolon.codex.page.soulbone_amulet.1"),
                    new WorktablePage(Registry.SOULBONE_AMULET.get())
            );

            //WIPS

            RAVEN_CLOAK = new Chapter("eidolon.codex.chapter.raven_cloak",
                    new TitlePage("eidolon.codex.page.raven_cloak"),
                    new WorktablePage(Registry.RAVEN_CLOAK.get())
            );

            NECROMANCER_STAFF = new Chapter("eidolon.codex.chapter.summoning_staff",
                    new TitlePage("eidolon.codex.page.summoning_staff")
            );

            ARROW_RING = new Chapter("eidolon.codex.chapter.angel_sight",
                    new TitlePage("eidolon.codex.page.angel_sight"),
                    new WorktablePage(Registry.ANGELS_SIGHT.get())
            );


            ARTIFICE_INDEX = new Index(
                    "eidolon.codex.chapter.artifice",
                    new TitledIndexPage("eidolon.codex.page.artifice",
                            new IndexEntry(WOODEN_STAND, new ItemStack(Registry.WOODEN_STAND.get())),
                            new IndexEntry(TALLOW, new ItemStack(Registry.TALLOW.get())),
                            new IndexEntry(CRUCIBLE, new ItemStack(Registry.CRUCIBLE.get())),
                            new IndexEntry(ARCANE_GOLD, new ItemStack(Registry.ARCANE_GOLD_INGOT.get())),
                            new IndexEntry(REAGENTS, new ItemStack(Registry.DEATH_ESSENCE.get())),
                            new IndexEntry(SOUL_GEMS, new ItemStack(Registry.LESSER_SOUL_GEM.get()))
                    ),
                    new IndexPage(
                            new IndexEntry(SHADOW_GEM, new ItemStack(Registry.SHADOW_GEM.get())),
                            new IndexEntry(BASIC_ALCHEMY, new ItemStack(Items.GUNPOWDER)),
                            new IndexEntry(WARPED_SPROUTS, new ItemStack(Registry.WARPED_SPROUTS.get())),
                            new IndexEntry(INLAYS, new ItemStack(Registry.GOLD_INLAY.get())),
                            new IndexEntry(BASIC_BAUBLES, new ItemStack(Registry.BASIC_RING.get())),
                            new IndexEntry(MAGIC_WORKBENCH, new ItemStack(Registry.WORKTABLE.get())),
                            new IndexEntry(VOID_AMULET, new ItemStack(Registry.VOID_AMULET.get()))
                    ),
                    new IndexPage(
                            new IndexEntry(WARDED_MAIL, new ItemStack(Registry.WARDED_MAIL.get())),
                            new IndexEntry(SOULFIRE_WAND, new ItemStack(Registry.SOULFIRE_WAND.get())),
                            new IndexEntry(BONECHILL_WAND, new ItemStack(Registry.BONECHILL_WAND.get())),
                            new IndexEntry(REAPER_SCYTHE, new ItemStack(Registry.REAPER_SCYTHE.get())),
                            new IndexEntry(CLEAVING_AXE, new ItemStack(Registry.CLEAVING_AXE.get())),
                            new IndexEntry(SOUL_ENCHANTER, new ItemStack(Registry.SOUL_ENCHANTER.get())),
                            new IndexEntry(REVERSAL_PICK, new ItemStack(Registry.REVERSAL_PICK.get()))
                    ),
                    new IndexPage(
                            new IndexEntry(WARLOCK_ARMOR, new ItemStack(Registry.WARLOCK_HAT.get())),
                            new IndexEntry(GRAVITY_BELT, new ItemStack(Registry.GRAVITY_BELT.get())),
                            new IndexEntry(PRESTIGIOUS_PALM, new ItemStack(Registry.PRESTIGIOUS_PALM.get())),
                            new IndexEntry(MIND_SHIELDING_PLATE, new ItemStack(Registry.MIND_SHIELDING_PLATE.get())),
                            new IndexEntry(RESOLUTE_BELT, new ItemStack(Registry.RESOLUTE_BELT.get())),
                            new IndexEntry(GLASS_HAND, new ItemStack(Registry.GLASS_HAND.get())),
                            new IndexEntry(SOULBONE, new ItemStack(Registry.SOULBONE_AMULET.get()))
                    ),
                    new IndexPage(
                            new IndexEntry(RAVEN_CLOAK, new ItemStack(Registry.RAVEN_CLOAK.get())),
                            new IndexEntry(NECROMANCER_STAFF, new ItemStack(Registry.SUMMONING_STAFF.get())),
                            new IndexEntry(ARROW_RING, new ItemStack(Registry.ANGELS_SIGHT.get()))
                    )
            );

            categories.add(ARTIFICE = new Category(
                    "artifice",
                    new ItemStack(Registry.GOLD_INLAY.get()),
                    ColorUtil.packColor(255, 204, 57, 72),
                    ARTIFICE_INDEX
            ));
        }

        //THEURGY
        {
            INTRO_SIGNS = new Chapter(
                    "eidolon.codex.chapter.intro_signs",
                    new TitlePage("eidolon.codex.page.intro_signs.0"),
                    new TextPage("eidolon.codex.page.intro_signs.1")
            );

            EFFIGY = new Chapter(
                    "eidolon.codex.chapter.effigy",
                    new TitlePage("eidolon.codex.page.effigy"),
                    new CraftingPage(Registry.STRAW_EFFIGY.get().asItem())
            );

            ALTARS = new Chapter(
                    "eidolon.codex.chapter.altars",
                    new TitlePage("eidolon.codex.page.altars.0"),
                    new TextPage("eidolon.codex.page.altars.1"),
                    new CraftingPage(new ItemStack(Registry.WOODEN_ALTAR.get(), 3))
            );

            ALTAR_LIGHTS = new Chapter(
                    "eidolon.codex.chapter.altar_lights",
                    new TitlePage("eidolon.codex.page.altar_lights.0"),
                    new ListPage("eidolon.codex.page.altar_lights.1",
                            new ListEntry("torch", new ItemStack(Items.TORCH)),
                            new ListEntry("lantern", new ItemStack(Items.LANTERN)),
                            new ListEntry("candle", new ItemStack(Registry.CANDLE.get())),
                            new ListEntry("candlestick", new ItemStack(Registry.CANDLESTICK.get())))
            );

            ALTAR_SKULLS = new Chapter(
                    "eidolon.codex.chapter.altar_skulls",
                    new TitlePage("eidolon.codex.page.altar_skulls.0"),
                    new ListPage("eidolon.codex.page.altar_skulls.1",
                            new ListEntry("skull", new ItemStack(Items.SKELETON_SKULL)),
                            new ListEntry("zombie", new ItemStack(Items.ZOMBIE_HEAD)),
                            new ListEntry("wither_skull", new ItemStack(Items.WITHER_SKELETON_SKULL)))
            );

            ALTAR_HERBS = new Chapter(
                    "eidolon.codex.chapter.altar_herbs",
                    new TitlePage("eidolon.codex.page.altar_herbs.0"),
                    new ListPage("eidolon.codex.page.altar_herbs.1",
                            new ListEntry("crimson_fungus", new ItemStack(Items.CRIMSON_FUNGUS)),
                            new ListEntry("warped_fungus", new ItemStack(Items.WARPED_FUNGUS)),
                            new ListEntry("wither_rose", new ItemStack(Items.WITHER_ROSE)))
            );

            GOBLET = new Chapter(
                    "eidolon.codex.chapter.goblet",
                    new TitlePage("eidolon.codex.page.goblet"),
                    new CraftingPage(Registry.GOBLET.get().asItem())
            );
            CENSER = new Chapter(
                    "eidolon.codex.chapter.censer",
                    new TitlePage("eidolon.codex.page.censer"),
                    new CraftingPage(Registry.CENSER.get().asItem())
            );

            DARK_PRAYER = new Chapter(
                    "eidolon.codex.chapter.dark_prayer",
                    new ChantPage("eidolon.codex.page.dark_prayer.0", Spells.DARK_PRAYER.signs()),
                    new TextPage("eidolon.codex.page.dark_prayer.1")
            );
            LIGHT_PRAYER = new Chapter(
                    "eidolon.codex.chapter.light_prayer",
                    new ChantPage("eidolon.codex.page.light_prayer.0", Spells.LIGHT_PRAYER.signs()),
                    new TextPage("eidolon.codex.page.light_prayer.1")
            );


            ANIMAL_SACRIFICE = new Chapter(
                    "eidolon.codex.chapter.animal_sacrifice",
                    new ChantPage("eidolon.codex.page.animal_sacrifice", Spells.DARK_ANIMAL_SACRIFICE.signs())
            );

            INCENSE_BURN = new Chapter(
                    "eidolon.codex.chapter.censer_offering",
                    new TitlePage("eidolon.codex.page.censer_offering"),
                    new CruciblePage(new ItemStack(Registry.OFFERING_INCENSE.get(), 2))
            );

            DARK_TOUCH = new Chapter(
                    "eidolon.codex.chapter.dark_touch",
                    new ChantPage("eidolon.codex.page.dark_touch.0", Spells.DARK_TOUCH.signs()),
                    new TextPage("eidolon.codex.page.dark_touch.1")
            );

            HOLY_TOUCH = new Chapter(
                    "eidolon.codex.chapter.holy_touch",
                    new ChantPage("eidolon.codex.page.holy_touch.0", Spells.HOLY_TOUCH.signs()),
                    new TextPage("eidolon.codex.page.holy_touch.1")
            );

            STONE_ALTAR = new Chapter(
                    "eidolon.codex.chapter.stone_altar",
                    new TitlePage("eidolon.codex.page.stone_altar"),
                    new WorktablePage(new ItemStack(Registry.STONE_ALTAR.get(), 3)
                    )
            );

            UNHOLY_EFFIGY = new Chapter(
                    "eidolon.codex.chapter.unholy_effigy",
                    new TitlePage("eidolon.codex.page.unholy_effigy"),
                    new WorktablePage(Registry.ELDER_EFFIGY.get().asItem())
            );

            HOLY_EFFIGY = new Chapter(
                    "eidolon.codex.chapter.holy_effigy",
                    new TitlePage("eidolon.codex.page.holy_effigy"),
                    new WorktablePage(Registry.ELDER_EFFIGY.get().asItem())
            );

            VILLAGER_SACRIFICE = new Chapter(
                    "eidolon.codex.chapter.villager_sacrifice",
                    new ChantPage("eidolon.codex.page.villager_sacrifice", Spells.DARK_VILLAGER_SACRIFICE.signs())
            );

            HEAL = new Chapter(
                    "eidolon.codex.chapter.lay_on_hands",
                    new ChantPage("eidolon.codex.page.lay_on_hands", Spells.LAY_ON_HANDS.signs())
            );

            THEURGY_INDEX = new Index(
                    "eidolon.codex.chapter.theurgy",
                    new TitledIndexPage(
                            "eidolon.codex.page.theurgy",
                            new IndexEntry(INTRO_SIGNS, new ItemStack(Items.PAPER)),
                            new IndexEntry(EFFIGY, new ItemStack(Registry.STRAW_EFFIGY.get())),
                            new IndexEntry(ALTARS, new ItemStack(Registry.WOODEN_ALTAR.get())),
                            new IndexEntry(ALTAR_LIGHTS, new ItemStack(Registry.CANDLE.get())),
                            new IndexEntry(ALTAR_SKULLS, new ItemStack(Items.SKELETON_SKULL)),
                            new IndexEntry(ALTAR_HERBS, new ItemStack(Items.WITHER_ROSE))
                    ),
                    new IndexPage(
                            new IndexEntry(GOBLET, new ItemStack(Registry.GOBLET.get())),
                            new SignLockedEntry(DARK_PRAYER, new ItemStack(Registry.SHADOW_GEM.get()), Signs.WICKED_SIGN),
                            new SignLockedEntry(ANIMAL_SACRIFICE, new ItemStack(Items.PORKCHOP), Signs.BLOOD_SIGN),
                            new SignLockedEntry(DARK_TOUCH, new ItemStack(Registry.UNHOLY_SYMBOL.get()), Signs.SOUL_SIGN, Signs.WICKED_SIGN),
                            new SignLockedEntry(STONE_ALTAR, new ItemStack(Registry.STONE_ALTAR.get()), Signs.SOUL_SIGN),
                            new SignLockedEntry(UNHOLY_EFFIGY, new ItemStack(Registry.ELDER_EFFIGY.get()), Signs.WICKED_SIGN, Signs.SOUL_SIGN),
                            new FactLockedEntry(VILLAGER_SACRIFICE, new ItemStack(Items.IRON_SWORD), Facts.VILLAGER_SACRIFICE)
                    ),
                    //TODO light path - EFFIGY
                    new IndexPage(
                            new IndexEntry(CENSER, new ItemStack(Registry.CENSER.get().asItem())),
                            new SignLockedEntry(LIGHT_PRAYER, new ItemStack(Registry.ENCHANTED_ASH.get()), Signs.SACRED_SIGN),
                            new SignLockedEntry(INCENSE_BURN, new ItemStack(Registry.OFFERING_INCENSE.get()), Signs.FLAME_SIGN),
                            new SignLockedEntry(HOLY_TOUCH, new ItemStack(Registry.HOLY_SYMBOL.get()), Signs.SOUL_SIGN, Signs.SACRED_SIGN),
                            new SignLockedEntry(STONE_ALTAR, new ItemStack(Registry.STONE_ALTAR.get()), Signs.SOUL_SIGN),
                            new SignLockedEntry(HOLY_EFFIGY, new ItemStack(Registry.ELDER_EFFIGY.get()), Signs.SACRED_SIGN, Signs.SOUL_SIGN),
                            new FactLockedEntry(HEAL, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING), Facts.VILLAGER_HEALING)
                    )
            );


            categories.add(THEURGY = new Category(
                    "theurgy",
                    new ItemStack(Registry.GOBLET.get()),
                    ColorUtil.packColor(255, 94, 90, 219),
                    THEURGY_INDEX
            ));

        }

        //SIGNS
        {
            WICKED_SIGN = new Chapter(
                    "eidolon.codex.chapter.wicked_sign",
                    new TitlePage("eidolon.codex.page.wicked_sign"),
                    new SignPage(Signs.WICKED_SIGN)
            );

            SACRED_SIGN = new Chapter(
                    "eidolon.codex.chapter.sacred_sign",
                    new TitlePage("eidolon.codex.page.sacred_sign"),
                    new SignPage(Signs.SACRED_SIGN)
            );

            BLOOD_SIGN = new Chapter(
                    "eidolon.codex.chapter.blood_sign",
                    new TitlePage("eidolon.codex.page.blood_sign"),
                    new SignPage(Signs.BLOOD_SIGN)
            );

            SOUL_SIGN = new Chapter(
                    "eidolon.codex.chapter.soul_sign",
                    new TitlePage("eidolon.codex.page.soul_sign"),
                    new SignPage(Signs.SOUL_SIGN)
            );

            MIND_SIGN = new Chapter(
                    "eidolon.codex.chapter.mind_sign",
                    new TitlePage("eidolon.codex.page.mind_sign"),
                    new SignPage(Signs.MIND_SIGN)
            );

            FLAME_SIGN = new Chapter(
                    "eidolon.codex.chapter.flame_sign",
                    new TitlePage("eidolon.codex.page.flame_sign"),
                    new SignPage(Signs.FLAME_SIGN)
            );

            WINTER_SIGN = new Chapter(
                    "eidolon.codex.chapter.winter_sign",
                    new TitlePage("eidolon.codex.page.winter_sign"),
                    new SignPage(Signs.WINTER_SIGN)
            );

            HARMONY_SIGN = new Chapter(
                    "eidolon.codex.chapter.harmony_sign",
                    new TitlePage("eidolon.codex.page.harmony_sign"),
                    new SignPage(Signs.HARMONY_SIGN)
            );

            DEATH_SIGN = new Chapter(
                    "eidolon.codex.chapter.death_sign",
                    new TitlePage("eidolon.codex.page.death_sign"),
                    new SignPage(Signs.DEATH_SIGN)
            );

            WARDING_SIGN = new Chapter(
                    "eidolon.codex.chapter.warding_sign",
                    new TitlePage("eidolon.codex.page.warding_sign"),
                    new SignPage(Signs.WARDING_SIGN)
            );

            MAGIC_SIGN = new Chapter(
                    "eidolon.codex.chapter.magic_sign",
                    new TitlePage("eidolon.codex.page.magic_sign"),
                    new SignPage(Signs.MAGIC_SIGN)
            );

            SIGNS_INDEX = new Index(
                    "eidolon.codex.chapter.signs_index",
                    new SignIndexPage(
                            new SignEntry(WICKED_SIGN, Signs.WICKED_SIGN),
                            new SignEntry(SACRED_SIGN, Signs.SACRED_SIGN),
                            new SignEntry(BLOOD_SIGN, Signs.BLOOD_SIGN),
                            new SignEntry(SOUL_SIGN, Signs.SOUL_SIGN),
                            new SignEntry(MIND_SIGN, Signs.MIND_SIGN),
                            new SignEntry(FLAME_SIGN, Signs.FLAME_SIGN)
                    ),
                    new SignIndexPage(
                            new SignEntry(WINTER_SIGN, Signs.WINTER_SIGN),
                            new SignEntry(MAGIC_SIGN, Signs.MAGIC_SIGN),
                            new SignEntry(HARMONY_SIGN, Signs.HARMONY_SIGN),
                            new SignEntry(DEATH_SIGN, Signs.DEATH_SIGN),
                            new SignEntry(WARDING_SIGN, Signs.WARDING_SIGN)
                    )
            );

            categories.add(SIGNS = new Category(
                    "signs",
                    new ItemStack(Registry.UNHOLY_SYMBOL.get()),
                    ColorUtil.packColor(255, 163, 74, 207),
                    SIGNS_INDEX
            ));
        }

        //SPELLS
        {
            MANA = new Chapter("eidolon.codex.chapter.mana",
                    new TitlePage("eidolon.codex.page.mana"));

            LIGHT = new Chapter(
                    "eidolon.codex.chapter.light",
                    new ChantPage("eidolon.codex.page.light", Spells.LIGHT_CHANT.signs())
            );

            FIRE_TOUCH = new Chapter(
                    "eidolon.codex.chapter.fire_touch",
                    new ChantPage("eidolon.codex.page.fire_touch", Spells.FIRE_CHANT.signs())
            );

            CHILL_TOUCH = new Chapter(
                    "eidolon.codex.chapter.chill_touch",
                    new ChantPage("eidolon.codex.page.chill_touch", Spells.FROST_CHANT.signs())
            );

            ZOMBIFY = new Chapter(
                    "eidolon.codex.chapter.villager_zombie",
                    new ChantPage("eidolon.codex.page.villager_zombie", Spells.ZOMBIFY.signs())
            );

            CURE_ZOMBIE = new Chapter(
                    "eidolon.codex.chapter.villager_cure",
                    new ChantPage("eidolon.codex.page.villager_cure", Spells.CURE_ZOMBIE_CHANT.signs())
            );

            ENTHRALL = new Chapter(
                    "eidolon.codex.chapter.enthrall",
                    new ChantPage("eidolon.codex.page.enthrall", Spells.ENTHRALL_UNDEAD.signs()),
                    new TextPage("eidolon.codex.page.enthrall.1")
            );

            SMITE = new Chapter(
                    "eidolon.codex.chapter.smite",
                    new ChantPage("eidolon.codex.page.smite", Spells.SMITE_CHANT.signs())
            );

            SPELLS_INDEX = new Index(
                    "eidolon.codex.chapter.spells",
                    new TitledIndexPage(
                            "eidolon.codex.page.spells",
                            new SignLockedEntry(LIGHT, new ItemStack(Items.LANTERN), Signs.FLAME_SIGN),
                            new ResearchLockedEntry(FIRE_TOUCH, new ItemStack(Items.FLINT_AND_STEEL), Researches.FIRE_SPELL),
                            new ResearchLockedEntry(CHILL_TOUCH, new ItemStack(Items.ICE), Researches.FROST_SPELL),
                            new FactLockedEntry(ZOMBIFY, new ItemStack(Registry.ZOMBIE_HEART.get()), Facts.ZOMBIFY),
                            new FactLockedEntry(CURE_ZOMBIE, new ItemStack(Items.GOLDEN_APPLE), Facts.ZOMBIE_CURE),
                            new FactLockedEntry(ENTHRALL, new ItemStack(Registry.SUMMONING_STAFF.get()), Facts.ENTHRALL),
                            new FactLockedEntry(SMITE, new ItemStack(Registry.SILVER_SWORD.get()), Facts.SMITE)
                    ), new IndexPage(

            )
            );

            categories.add(SPELLS = new Category(
                    "spells",
                    new ItemStack(Registry.PARCHMENT.get()),
                    ColorUtil.packColor(255, 70, 70, 194),
                    SPELLS_INDEX
            ));
        }
    }
}
