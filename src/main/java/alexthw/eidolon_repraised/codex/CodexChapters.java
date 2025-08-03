package alexthw.eidolon_repraised.codex;

import alexthw.eidolon_repraised.capability.Facts;
import alexthw.eidolon_repraised.codex.IndexPage.*;
import alexthw.eidolon_repraised.codex.ListPage.ListEntry;
import alexthw.eidolon_repraised.codex.SignIndexPage.SignEntry;
import alexthw.eidolon_repraised.common.deity.Deities;
import alexthw.eidolon_repraised.common.item.CodexItem;
import alexthw.eidolon_repraised.registries.*;
import alexthw.eidolon_repraised.util.ClientInfo;
import alexthw.eidolon_repraised.util.ColorUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static alexthw.eidolon_repraised.Eidolon.prefix;

public class CodexChapters {

    static public final List<Category> categories = new CopyOnWriteArrayList<>();
    static public final Map<Item, IndexEntry> itemToEntryMap = new ConcurrentHashMap<>();

    public static Category NATURE, RITUALS, ARTIFICE, THEURGY, SIGNS, SPELLS;

    public static Index NATURE_INDEX, RITUALS_INDEX, ARTIFICE_INDEX, THEURGY_INDEX, SIGNS_INDEX, SPELLS_INDEX;
    public static Chapter MONSTERS, CRITTERS, ORES, PEWTER, ENCHANTED_ASH, PLANTS, RESEARCHES, DECORATIONS,
            BRAZIER, ITEM_PROVIDERS, CRYSTAL_RITUAL, SUMMON_RITUAL, ALLURE_RITUAL, REPELLING_RITUAL, DECEIT_RITUAL, TIME_RITUALS, PURIFY_RITUAL, SANGUINE_RITUAL, RECHARGE_RITUAL, CAPTURE_RITUAL, LOCATE_RITUAL,
            WOODEN_STAND, TALLOW, CRUCIBLE, ARCANE_GOLD, REAGENTS, SOUL_GEMS, SHADOW_GEM, WARPED_SPROUTS, BASIC_ALCHEMY, INLAYS, BASIC_BAUBLES, MAGIC_WORKBENCH, VOID_AMULET, WARDED_MAIL, SOULFIRE_WAND, BONECHILL_WAND, REAPER_SCYTHE, CLEAVING_AXE, SOUL_ENCHANTER, REVERSAL_PICK, WARLOCK_ARMOR, GRAVITY_BELT, PRESTIGIOUS_PALM, MIND_SHIELDING_PLATE, RESOLUTE_BELT, GLASS_HAND, SOULBONE, RAVEN_CLOAK, ARROW_RING, NECROMANCER_STAFF,
            INTRO_SIGNS, EFFIGY, ALTARS, ALTAR_LIGHTS, ALTAR_SKULLS, ALTAR_HERBS, GOBLET, CENSER, DARK_PRAYER, ANIMAL_SACRIFICE, DARK_TOUCH, STONE_ALTAR, UNHOLY_EFFIGY, HOLY_EFFIGY, VILLAGER_SACRIFICE, LIGHT_PRAYER, INCENSE_BURN, HEAL, HOLY_TOUCH,
            WICKED_SIGN, SACRED_SIGN, BLOOD_SIGN, SOUL_SIGN, MIND_SIGN, FLAME_SIGN, WINTER_SIGN, HARMONY_SIGN, DEATH_SIGN, WARDING_SIGN, MAGIC_SIGN,
            MANA, LIGHT, FIRE_TOUCH, CHILL_TOUCH, WATER, ZOMBIFY, CURE_ZOMBIE, ENTHRALL, SMITE, SUNDER_ARMOR, REINFORCE_ARMOR;

    public static void init() {

        // purge old categories
        if (!categories.isEmpty()) {
            CodexGui.INSTANCE = null;
            categories.clear();
            itemToEntryMap.clear();
        }
        //NATURE
        {
            MONSTERS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.monsters")
                    .titlePage("eidolon_repraised.codex.page.monsters.zombie_brute")
                    .entityPage(EidolonEntities.ZOMBIE_BRUTE.get())
                    .titlePage("eidolon_repraised.codex.page.monsters.wraith")
                    .entityPage(EidolonEntities.WRAITH.get())
                    .titlePage("eidolon_repraised.codex.page.monsters.chilled")
                    .textPage("")
                    .entityPage(EidolonEntities.GIANT_SKEL.get())
                    .titlePage("eidolon_repraised.codex.page.monsters.giant_skeleton")
                    .entityPage(EidolonEntities.NECROMANCER.get())
                    .titlePage("eidolon_repraised.codex.page.monsters.necromancer")
                    .build();

            CRITTERS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.critters")
                    .titlePage("eidolon_repraised.codex.page.critters.raven")
                    .entityPage(EidolonEntities.RAVEN.get())
                    .titlePage("eidolon_repraised.codex.page.critters.slimy_slug")
                    .entityPage(EidolonEntities.SLIMY_SLUG.get())
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual_c.1", prefix("summon_ravens"))
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual_c.2", prefix("summon_slugs"))
                    .build();

            ORES = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.ores")
                    .titlePage("eidolon_repraised.codex.page.ores.lead_ore")
                    .titlePage("eidolon_repraised.codex.page.ores.silver_ore")
                    .smeltingPage(new ItemStack(Registry.LEAD_INGOT.get()), new ItemStack(Registry.LEAD_ORE.get()), prefix("smelt_lead_ore"))
                    .smeltingPage(new ItemStack(Registry.SILVER_INGOT.get()), new ItemStack(Registry.SILVER_ORE.get()), prefix("smelt_silver_ore"))
                    .craftingPage(Registry.LEAD_BLOCK.get())
                    .craftingPage(new ItemStack(Registry.LEAD_NUGGET.get(), 9), prefix("decompress_lead_ingot"))
                    .craftingPage(Registry.SILVER_BLOCK.get())
                    .craftingPage(new ItemStack(Registry.SILVER_NUGGET.get(), 9), prefix("decompress_silver_ingot"))
                    .build();

            PEWTER = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.pewter")
                    .titlePage("eidolon_repraised.codex.page.pewter")
                    .craftingPage(new ItemStack(Registry.PEWTER_BLEND.get(), 2))
                    .smeltingPage(new ItemStack(Registry.PEWTER_INGOT.get()), new ItemStack(Registry.PEWTER_BLEND.get()), prefix("smelt_pewter_blend"))
                    .craftingPage(Registry.PEWTER_BLOCK.get())
                    .craftingPage(new ItemStack(Registry.PEWTER_NUGGET.get(), 9), prefix("decompress_pewter_ingot"))
                    .build();

            ENCHANTED_ASH = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.enchanted_ash")
                    .titlePage("eidolon_repraised.codex.page.enchanted_ash")
                    .smeltingPage(new ItemStack(Registry.ENCHANTED_ASH.get(), 2), new ItemStack(Items.BONE))
                    .build();

            PLANTS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.plants")
                    .titlePage("eidolon_repraised.codex.page.plants")
                    .worktablePage(Registry.ATHAME.get())
                    .titlePage("eidolon_repraised.codex.page.plants.planter", new ItemStack(Registry.PLANTER.get()))
                    .worktablePage(Registry.PLANTER.get())
                    .titlePage("eidolon_repraised.codex.page.plants.oanna", new ItemStack(Registry.OANNA_BLOOM.get()))
                    .titlePage("eidolon_repraised.codex.page.plants.mirecap", new ItemStack(Registry.MIRECAP.get()))
                    .titlePage("eidolon_repraised.codex.page.plants.sildran", new ItemStack(Registry.SILDRIAN_SEED.get()))
                    .titlePage("eidolon_repraised.codex.page.plants.avenna", new ItemStack(Registry.AVENNIAN_SPRIG.get()))
                    .titlePage("eidolon_repraised.codex.page.plants.merammer", new ItemStack(Registry.MERAMMER_ROOT.get()))
                    .craftingPage(Registry.MERAMMER_RESIN.get())
                    .build();

            RESEARCHES = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.researches")
                    .textPage("eidolon_repraised.codex.page.researches.0")
                    .craftingPage(Registry.RESEARCH_TABLE.get())
                    .cruciblePage(new ItemStack(Registry.MAGICIANS_WAX.get(), 4))
                    .craftingPage(new ItemStack(Registry.ARCANE_SEAL.get(), 2))
                    .cruciblePage(new ItemStack(Registry.MAGIC_INK.get(), 2))
                    .cruciblePage(new ItemStack(Registry.PARCHMENT.get(), 4))
                    .craftingPage(Registry.NOTETAKING_TOOLS.get())
                    .build();

            DECORATIONS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.decorations")
                    .titlePage("eidolon_repraised.codex.page.decorations")
                    .cruciblePage(new ItemStack(Registry.ELDER_BRICK.get(), 16))
                    .craftingPage(new ItemStack(Registry.ELDER_BRICKS.getBlock(), 4))
                    .craftingPage(Registry.BONE_PILE.getBlock())
                    .build();

            NATURE_INDEX = new Index(
                    "eidolon_repraised.codex.chapter.nature_index",
                    new TitledIndexPage("eidolon_repraised.codex.page.nature_index.0",
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
            BRAZIER = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.brazier")
                    .titlePage("eidolon_repraised.codex.page.brazier.0")
                    .textPage("eidolon_repraised.codex.page.brazier.1")
                    .craftingPage(Registry.BRAZIER.get().asItem())
                    .build();

            ITEM_PROVIDERS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.item_providers")
                    .titlePage("eidolon_repraised.codex.page.item_providers.0")
                    .craftingPage(Registry.STONE_HAND.get().asItem())
                    .titlePage("eidolon_repraised.codex.page.item_providers.1")
                    .craftingPage(Registry.NECROTIC_FOCUS.get().asItem())
                    .build();

            CRYSTAL_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.crystal_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.crystal_ritual", RitualRegistry.CRYSTAL_RITUAL)
                    .textPage("eidolon_repraised.codex.page.crystal_ritual")
                    .build();

            SUMMON_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.summon_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual.0", prefix("summon_zombie"))
                    .textPage("eidolon_repraised.codex.page.summon_ritual.0")
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual.1", prefix("summon_skeleton"))
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual.2", prefix("summon_phantom"))
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual.3", prefix("summon_wither_skeleton"))
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual.4", prefix("summon_husk"))
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual.5", prefix("summon_drowned"))
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual.6", prefix("summon_stray"))
                    .titledRitualPage("eidolon_repraised.codex.page.summon_ritual.7", prefix("summon_wraith"))
                    .build();

            ALLURE_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.allure_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.allure_ritual", RitualRegistry.ALLURE_RITUAL)
                    .textPage("eidolon_repraised.codex.page.allure_ritual")
                    .build();

            REPELLING_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.repelling_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.repelling_ritual", RitualRegistry.REPELLING_RITUAL)
                    .textPage("eidolon_repraised.codex.page.repelling_ritual")
                    .build();

            DECEIT_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.deceit_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.deceit_ritual", RitualRegistry.DECEIT_RITUAL)
                    .textPage("eidolon_repraised.codex.page.deceit_ritual")
                    .build();

            TIME_RITUALS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.time_rituals")
                    .titledRitualPage("eidolon_repraised.codex.page.time_rituals.0", RitualRegistry.DAYLIGHT_RITUAL)
                    .textPage("eidolon_repraised.codex.page.time_rituals.0")
                    .titledRitualPage("eidolon_repraised.codex.page.time_rituals.1", RitualRegistry.MOONLIGHT_RITUAL)
                    .textPage("eidolon_repraised.codex.page.time_rituals.1")
                    .build();

            PURIFY_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.purify_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.purify_ritual", RitualRegistry.PURIFY_RITUAL)
                    .textPage("eidolon_repraised.codex.page.purify_ritual")
                    .build();

            SANGUINE_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.sanguine_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.sanguine_ritual.0", Registry.SAPPING_SWORD.get().getDefaultInstance())
                    .textPage("eidolon_repraised.codex.page.sanguine_ritual.0")
                    .titledRitualPage("eidolon_repraised.codex.page.sanguine_ritual.1", Registry.SANGUINE_AMULET.get().getDefaultInstance())
                    .textPage("eidolon_repraised.codex.page.sanguine_ritual.1")
                    .build();

            RECHARGE_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.recharge_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.recharge_ritual.soulfire", RitualRegistry.RECHARGE_SOULFIRE_RITUAL)
                    .titledRitualPage("eidolon_repraised.codex.page.recharge_ritual.bonechill", RitualRegistry.RECHARGE_BONECHILL_RITUAL)
                    .textPage("eidolon_repraised.codex.page.recharge_ritual")
                    .build();

            CAPTURE_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.capture_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.capture_ritual", RitualRegistry.ABSORB_RITUAL)
                    .textPage("eidolon_repraised.codex.page.capture_ritual")
                    .build();

            LOCATE_RITUAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.locate_ritual")
                    .titledRitualPage("eidolon_repraised.codex.page.locate_ritual", prefix("ritual_catacomb_locator"))
                    .textPage("eidolon_repraised.codex.page.locate_ritual")
                    .build();

            RITUALS_INDEX = new Index(
                    "eidolon_repraised.codex.chapter.rituals",
                    new TitledIndexPage("eidolon_repraised.codex.page.rituals.0",
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
                            new IndexEntry(CAPTURE_RITUAL, new ItemStack(Registry.SUMMONING_STAFF.get())),
                            new IndexEntry(LOCATE_RITUAL, new ItemStack(Items.COMPASS))
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

            WOODEN_STAND = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.wooden_stand")
                    .titlePage("eidolon_repraised.codex.page.wooden_stand.0")
                    .craftingPage(Registry.WOODEN_STAND.get().asItem())
                    .titlePage("eidolon_repraised.codex.page.wooden_stand.1")
                    .cruciblePage(new ItemStack(Registry.FUNGUS_SPROUTS.get(), 2))
                    .build();

            TALLOW = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.tallow")
                    .titlePage("eidolon_repraised.codex.page.tallow.0")
                    .smeltingPage(new ItemStack(Registry.TALLOW.get()), new ItemStack(Items.ROTTEN_FLESH))
                    .titlePage("eidolon_repraised.codex.page.tallow.1")
                    .craftingPage(new ItemStack(Registry.CANDLE.get(), 4))
                    .craftingPage(Registry.CANDLESTICK.get().asItem())
                    .build();

            CRUCIBLE = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.crucible")
                    .titlePage("eidolon_repraised.codex.page.crucible.0")
                    .textPage("eidolon_repraised.codex.page.crucible.1")
                    .craftingPage(Registry.CRUCIBLE.get().asItem())
                    .build();

            ARCANE_GOLD = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.arcane_gold")
                    .titlePage("eidolon_repraised.codex.page.arcane_gold")
                    .cruciblePage(new ItemStack(Registry.ARCANE_GOLD_INGOT.get(), 2), prefix("arcane_gold_ingot_alchemy"))
                    .craftingPage(new ItemStack(Registry.ARCANE_GOLD_BLOCK.get()))
                    .craftingPage(new ItemStack(Registry.ARCANE_GOLD_NUGGET.get(), 9), prefix("decompress_arcane_gold_ingot"))
                    .build();

            REAGENTS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.reagents")
                    .titlePage("eidolon_repraised.codex.page.reagents.0")
                    .cruciblePage(new ItemStack(Registry.SULFUR.get(), 2))
                    .titlePage("eidolon_repraised.codex.page.reagents.1")
                    .cruciblePage(new ItemStack(Registry.DEATH_ESSENCE.get(), 4))
                    .titlePage("eidolon_repraised.codex.page.reagents.2")
                    .cruciblePage(new ItemStack(Registry.CRIMSON_ESSENCE.get(), 4), prefix("crimson_essence_fungus"))
                    .cruciblePage(new ItemStack(Registry.CRIMSON_ESSENCE.get(), 2), prefix("crimson_essence_vines"))
                    .cruciblePage(new ItemStack(Registry.CRIMSON_ESSENCE.get(), 2), prefix("crimson_essence_roots"))
                    .titlePage("eidolon_repraised.codex.page.reagents.3")
                    .cruciblePage(new ItemStack(Registry.ENDER_CALX.get(), 2))
                    .build();

            SOUL_GEMS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.soul_gems")
                    .titlePage("eidolon_repraised.codex.page.soul_gems")
                    .cruciblePage(Registry.LESSER_SOUL_GEM.get())
                    .build();

            SHADOW_GEM = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.shadow_gem")
                    .titlePage("eidolon_repraised.codex.page.shadow_gem")
                    .cruciblePage(Registry.SHADOW_GEM.get())
                    .build();

            WARPED_SPROUTS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.warped_sprouts")
                    .titlePage("eidolon_repraised.codex.page.warped_sprouts.0")
                    .cruciblePage(new ItemStack(Registry.WARPED_SPROUTS.get(), 2))
                    .titlePage("eidolon_repraised.codex.page.warped_sprouts.1")
                    .build();

            BASIC_ALCHEMY = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.basic_alchemy")
                    .titlePage("eidolon_repraised.codex.page.basic_alchemy.0")
                    .cruciblePage(new ItemStack(Items.LEATHER), prefix("flesh_to_leather"))
                    .titlePage("eidolon_repraised.codex.page.basic_alchemy.1")
                    .cruciblePage(new ItemStack(Items.ROTTEN_FLESH), prefix("meat_to_flesh"))
                    .titlePage("eidolon_repraised.codex.page.basic_alchemy.2")
                    .cruciblePage(new ItemStack(Items.GUNPOWDER, 4), prefix("gunpowder_alchemy"))
                    .titlePage("eidolon_repraised.codex.page.basic_alchemy.3")
                    .cruciblePage(new ItemStack(Items.GOLDEN_APPLE), prefix("gilding_apple"))
                    .cruciblePage(new ItemStack(Items.GOLDEN_CARROT), prefix("gilding_carrot"))
                    .cruciblePage(new ItemStack(Items.GLISTERING_MELON_SLICE), prefix("gilding_melon"))
                    .build();

            INLAYS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.inlays")
                    .titlePage("eidolon_repraised.codex.page.inlays")
                    .craftingPage(new ItemStack(Registry.PEWTER_INLAY.get(), 2))
                    .craftingPage(new ItemStack(Registry.GOLD_INLAY.get(), 2))
                    .build();

            BASIC_BAUBLES = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.basic_baubles")
                    .titlePage("eidolon_repraised.codex.page.basic_baubles")
                    .craftingPage(Registry.BASIC_AMULET.get())
                    .craftingPage(Registry.BASIC_RING.get())
                    .craftingPage(Registry.BASIC_BELT.get())
                    .build();

            MAGIC_WORKBENCH = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.magic_workbench")
                    .titlePage("eidolon_repraised.codex.page.magic_workbench")
                    .craftingPage(Registry.WORKTABLE.get().asItem())
                    .build();

            VOID_AMULET = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.void_amulet")
                    .titlePage("eidolon_repraised.codex.page.void_amulet")
                    .worktablePage(Registry.VOID_AMULET.get())
                    .build();

            WARDED_MAIL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.warded_mail")
                    .titlePage("eidolon_repraised.codex.page.warded_mail")
                    .worktablePage(Registry.WARDED_MAIL.get())
                    .build();

            SOULFIRE_WAND = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.soulfire_wand")
                    .titlePage("eidolon_repraised.codex.page.soulfire_wand")
                    .worktablePage(Registry.SOULFIRE_WAND.get())
                    .build();

            BONECHILL_WAND = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.bonechill_wand")
                    .titlePage("eidolon_repraised.codex.page.bonechill_wand")
                    .worktablePage(Registry.BONECHILL_WAND.get())
                    .build();

            REAPER_SCYTHE = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.reaper_scythe")
                    .titlePage("eidolon_repraised.codex.page.reaper_scythe")
                    .worktablePage(Registry.REAPER_SCYTHE.get())
                    .titlePage("eidolon_repraised.codex.page.death_scythe")
                    .worktablePage(Registry.DEATHBRINGER_SCYTHE.get())
                    .build();

            CLEAVING_AXE = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.cleaving_axe")
                    .titlePage("eidolon_repraised.codex.page.cleaving_axe")
                    .worktablePage(Registry.CLEAVING_AXE.get())
                    .build();

            SOUL_ENCHANTER = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.soul_enchanter")
                    .titlePage("eidolon_repraised.codex.page.soul_enchanter.0")
                    .textPage("eidolon_repraised.codex.page.soul_enchanter.1")
                    .worktablePage(Registry.SOUL_ENCHANTER.get().asItem())
                    .build();

            REVERSAL_PICK = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.reversal_pick")
                    .titlePage("eidolon_repraised.codex.page.reversal_pick")
                    .worktablePage(Registry.REVERSAL_PICK.get())
                    .build();

            WARLOCK_ARMOR = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.warlock_armor")
                    .titlePage("eidolon_repraised.codex.page.warlock_armor.0")
                    .worktablePage(new ItemStack(Registry.WICKED_WEAVE.get(), 8))
                    .titlePage("eidolon_repraised.codex.page.warlock_armor.1")
                    .worktablePage(Registry.WARLOCK_HAT.get())
                    .titlePage("eidolon_repraised.codex.page.warlock_armor.2")
                    .worktablePage(Registry.WARLOCK_CLOAK.get())
                    .titlePage("eidolon_repraised.codex.page.warlock_armor.3")
                    .worktablePage(Registry.WARLOCK_BOOTS.get())
                    .build();

            GRAVITY_BELT = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.gravity_belt")
                    .titlePage("eidolon_repraised.codex.page.gravity_belt")
                    .worktablePage(Registry.GRAVITY_BELT.get())
                    .build();

            PRESTIGIOUS_PALM = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.prestigious_palm")
                    .titlePage("eidolon_repraised.codex.page.prestigious_palm")
                    .worktablePage(Registry.PRESTIGIOUS_PALM.get())
                    .build();

            MIND_SHIELDING_PLATE = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.mind_shielding_plate")
                    .titlePage("eidolon_repraised.codex.page.mind_shielding_plate")
                    .worktablePage(Registry.MIND_SHIELDING_PLATE.get())
                    .build();

            RESOLUTE_BELT = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.resolute_belt")
                    .titlePage("eidolon_repraised.codex.page.resolute_belt")
                    .worktablePage(Registry.RESOLUTE_BELT.get())
                    .build();

            GLASS_HAND = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.glass_hand")
                    .titlePage("eidolon_repraised.codex.page.glass_hand")
                    .worktablePage(Registry.GLASS_HAND.get())
                    .build();

            SOULBONE = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.soulbone_amulet")
                    .titlePage("eidolon_repraised.codex.page.soulbone_amulet")
                    .worktablePage(Registry.SOULBONE_AMULET.get())
                    .titlePage("eidolon_repraised.codex.page.soulbone_amulet.1")
                    .titlePage("eidolon_repraised.codex.page.bonelord_armor")
                    .textPage("eidolon_repraised.codex.page.bonelord_armor.1")
                    .worktablePage(Registry.BONELORD_HELM.get())
                    .worktablePage(Registry.BONELORD_CHESTPLATE.get())
                    .worktablePage(Registry.BONELORD_GREAVES.get())
                    .build();

            RAVEN_CLOAK = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.raven_cloak")
                    .titlePage("eidolon_repraised.codex.page.raven_cloak")
                    .worktablePage(Registry.RAVEN_CLOAK.get())
                    .build();

            NECROMANCER_STAFF = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.summoning_staff")
                    .titlePage("eidolon_repraised.codex.page.summoning_staff")
                    .textPage("eidolon_repraised.codex.page.summoning_staff.1")
                    .build();

            ARROW_RING = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.angel_sight")
                    .titlePage("eidolon_repraised.codex.page.angel_sight")
                    .worktablePage(Registry.ANGELS_SIGHT.get())
                    .build();


            ARTIFICE_INDEX = new Index(
                    "eidolon_repraised.codex.chapter.artifice",
                    new TitledIndexPage("eidolon_repraised.codex.page.artifice",
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
                            new IndexEntry(MAGIC_WORKBENCH, new ItemStack(Registry.WORKTABLE.get()))
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
                            new IndexEntry(VOID_AMULET, new ItemStack(Registry.VOID_AMULET.get())),
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
            INTRO_SIGNS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.intro_signs")
                    .titlePage("eidolon_repraised.codex.page.intro_signs.0")
                    .textPage("eidolon_repraised.codex.page.intro_signs.1")
                    .build();

            EFFIGY = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.effigy")
                    .titlePage("eidolon_repraised.codex.page.effigy")
                    .craftingPage(Registry.STRAW_EFFIGY.get().asItem())
                    .build();

            ALTARS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.altars")
                    .titlePage("eidolon_repraised.codex.page.altars.0")
                    .textPage("eidolon_repraised.codex.page.altars.1")
                    .craftingPage(new ItemStack(Registry.WOODEN_ALTAR.get(), 3))
                    .titlePage("eidolon_repraised.codex.page.stone_altar")
                    .worktablePage(new ItemStack(Registry.STONE_ALTAR.get(), 3))
                    .build();

            ALTAR_LIGHTS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.altar_lights")
                    .titlePage("eidolon_repraised.codex.page.altar_lights.0")
                    .listPage("eidolon_repraised.codex.page.altar_lights.1",
                            new ListEntry("torch", new ItemStack(Items.TORCH), Blocks.TORCH),
                            new ListEntry("soultorch", new ItemStack(Items.SOUL_TORCH), Blocks.SOUL_TORCH),
                            new ListEntry("lantern", new ItemStack(Items.LANTERN), Blocks.LANTERN),
                            new ListEntry("candle", new ItemStack(Registry.CANDLE.get())),
                            new ListEntry("candlestick", new ItemStack(Registry.CANDLESTICK.get())),
                            new ListEntry("magic_candle", new ItemStack(Registry.MAGIC_CANDLE.get())),
                            new ListEntry("magic_candlestick", new ItemStack(Registry.MAGIC_CANDLESTICK.get()))
                    )
                    .build();

            ALTAR_SKULLS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.altar_skulls")
                    .titlePage("eidolon_repraised.codex.page.altar_skulls.0")
                    .listPage("eidolon_repraised.codex.page.altar_skulls.1",
                            new ListEntry("skull", new ItemStack(Items.SKELETON_SKULL)),
                            new ListEntry("zombie", new ItemStack(Items.ZOMBIE_HEAD)),
                            new ListEntry("wither_skull", new ItemStack(Items.WITHER_SKELETON_SKULL))
                    )
                    .build();

            ALTAR_HERBS = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.altar_herbs")
                    .titlePage("eidolon_repraised.codex.page.altar_herbs.0")
                    .listPage("eidolon_repraised.codex.page.altar_herbs.1",
                            new ListEntry("crimson_fungus", new ItemStack(Items.CRIMSON_FUNGUS), Blocks.POTTED_CRIMSON_FUNGUS),
                            new ListEntry("warped_fungus", new ItemStack(Items.WARPED_FUNGUS), Blocks.POTTED_WARPED_FUNGUS),
                            new ListEntry("wither_rose", new ItemStack(Items.WITHER_ROSE), Blocks.POTTED_WITHER_ROSE)
                    )
                    .build();

            GOBLET = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.goblet")
                    .titlePage("eidolon_repraised.codex.page.goblet")
                    .craftingPage(Registry.GOBLET.get().asItem())
                    .build();

            CENSER = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.censer")
                    .titlePage("eidolon_repraised.codex.page.censer")
                    .craftingPage(Registry.CENSER.get().asItem())
                    .build();

            DARK_PRAYER = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.dark_prayer")
                    .chantPage("eidolon_repraised.codex.page.dark_prayer.0", Spells.DARK_PRAYER)
                    .textPage("eidolon_repraised.codex.page.dark_prayer.1")
                    .build();

            LIGHT_PRAYER = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.light_prayer")
                    .chantPage("eidolon_repraised.codex.page.light_prayer.0", Spells.LIGHT_PRAYER)
                    .textPage("eidolon_repraised.codex.page.light_prayer.1")
                    .build();

            ANIMAL_SACRIFICE = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.animal_sacrifice")
                    .chantPage("eidolon_repraised.codex.page.animal_sacrifice", Spells.DARK_ANIMAL_SACRIFICE)
                    .build();

            INCENSE_BURN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.censer_offering")
                    .titlePage("eidolon_repraised.codex.page.censer_offering")
                    .cruciblePage(new ItemStack(Registry.OFFERING_INCENSE.get(), 2))
                    .build();

            DARK_TOUCH = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.dark_touch")
                    .chantPage("eidolon_repraised.codex.page.dark_touch.0", Spells.DARK_TOUCH)
                    .textPage("eidolon_repraised.codex.page.dark_touch.1")
                    .build();

            HOLY_TOUCH = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.holy_touch")
                    .chantPage("eidolon_repraised.codex.page.holy_touch.0", Spells.HOLY_TOUCH)
                    .textPage("eidolon_repraised.codex.page.holy_touch.1")
                    .build();

            UNHOLY_EFFIGY = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.unholy_effigy")
                    .titlePage("eidolon_repraised.codex.page.unholy_effigy")
                    .worktablePage(Registry.ELDER_EFFIGY.get().asItem())
                    .build();

            HOLY_EFFIGY = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.holy_effigy")
                    .titlePage("eidolon_repraised.codex.page.holy_effigy")
                    .worktablePage(Registry.ELDER_EFFIGY.get().asItem())
                    .build();

            VILLAGER_SACRIFICE = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.villager_sacrifice")
                    .chantPage("eidolon_repraised.codex.page.villager_sacrifice", Spells.DARK_VILLAGER_SACRIFICE)
                    .build();

            HEAL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.lay_on_hands")
                    .chantPage("eidolon_repraised.codex.page.lay_on_hands", Spells.LAY_ON_HANDS)
                    .build();

            ZOMBIFY = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.villager_zombie")
                    .chantPage("eidolon_repraised.codex.page.villager_zombie", Spells.ZOMBIFY)
                    .textPage("eidolon_repraised.codex.page.villager_zombie.1")
                    .build();

            CURE_ZOMBIE = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.villager_cure")
                    .chantPage("eidolon_repraised.codex.page.villager_cure", Spells.CURE_ZOMBIE_CHANT)
                    .textPage("eidolon_repraised.codex.page.villager_cure.1")
                    .build();


            THEURGY_INDEX = new Index(
                    "eidolon_repraised.codex.chapter.theurgy",
                    new TitledIndexPage(
                            "eidolon_repraised.codex.page.theurgy",
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
                            new ReputationLockedEntry(DARK_TOUCH, new ItemStack(Registry.UNHOLY_SYMBOL.get()), 10, Deities.DARK_DEITY),
                            new SignLockedEntry(UNHOLY_EFFIGY, new ItemStack(Registry.ELDER_EFFIGY.get()), Signs.WICKED_SIGN, Signs.SOUL_SIGN),
                            new FactLockedEntry(VILLAGER_SACRIFICE, new ItemStack(Items.IRON_SWORD), Facts.VILLAGER_SACRIFICE),
                            new FactLockedEntry(ZOMBIFY, new ItemStack(Registry.ZOMBIE_HEART.get()), Facts.ZOMBIFY)
                    ),
                    //TODO light path - EFFIGY
                    new IndexPage(
                            new IndexEntry(CENSER, new ItemStack(Registry.CENSER.get().asItem())),
                            new SignLockedEntry(LIGHT_PRAYER, new ItemStack(Registry.ENCHANTED_ASH.get()), Signs.SACRED_SIGN),
                            new SignLockedEntry(INCENSE_BURN, new ItemStack(Registry.OFFERING_INCENSE.get()), Signs.FLAME_SIGN),
                            new ReputationLockedEntry(HOLY_TOUCH, new ItemStack(Registry.HOLY_SYMBOL.get()), 10, Deities.LIGHT_DEITY),
                            new SignLockedEntry(HOLY_EFFIGY, new ItemStack(Registry.ELDER_EFFIGY.get()), Signs.SACRED_SIGN, Signs.SOUL_SIGN),
                            new FactLockedEntry(HEAL, PotionContents.createItemStack(Items.POTION, Potions.HEALING), Facts.VILLAGER_HEALING),
                            new FactLockedEntry(CURE_ZOMBIE, new ItemStack(Items.GOLDEN_APPLE), Facts.ZOMBIE_CURE)
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
            WICKED_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.wicked_sign")
                    .titlePage("eidolon_repraised.codex.page.wicked_sign")
                    .signPage(Signs.WICKED_SIGN)
                    .build();

            SACRED_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.sacred_sign")
                    .titlePage("eidolon_repraised.codex.page.sacred_sign")
                    .signPage(Signs.SACRED_SIGN)
                    .build();

            BLOOD_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.blood_sign")
                    .titlePage("eidolon_repraised.codex.page.blood_sign")
                    .signPage(Signs.BLOOD_SIGN)
                    .build();

            SOUL_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.soul_sign")
                    .titlePage("eidolon_repraised.codex.page.soul_sign")
                    .signPage(Signs.SOUL_SIGN)
                    .build();

            MIND_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.mind_sign")
                    .titlePage("eidolon_repraised.codex.page.mind_sign")
                    .signPage(Signs.MIND_SIGN)
                    .build();

            FLAME_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.flame_sign")
                    .titlePage("eidolon_repraised.codex.page.flame_sign")
                    .signPage(Signs.FLAME_SIGN)
                    .build();

            WINTER_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.winter_sign")
                    .titlePage("eidolon_repraised.codex.page.winter_sign")
                    .signPage(Signs.WINTER_SIGN)
                    .build();

            HARMONY_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.harmony_sign")
                    .titlePage("eidolon_repraised.codex.page.harmony_sign")
                    .signPage(Signs.HARMONY_SIGN)
                    .build();

            DEATH_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.death_sign")
                    .titlePage("eidolon_repraised.codex.page.death_sign")
                    .signPage(Signs.DEATH_SIGN)
                    .build();

            WARDING_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.warding_sign")
                    .titlePage("eidolon_repraised.codex.page.warding_sign")
                    .signPage(Signs.WARDING_SIGN)
                    .build();

            MAGIC_SIGN = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.magic_sign")
                    .titlePage("eidolon_repraised.codex.page.magic_sign")
                    .signPage(Signs.MAGIC_SIGN)
                    .build();

            SIGNS_INDEX = new Index(
                    "eidolon_repraised.codex.chapter.signs_index",
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
            MANA = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.mana")
                    .titlePage("eidolon_repraised.codex.page.mana")
                    .textPage("eidolon_repraised.codex.page.mana.1")
                    .build();

            LIGHT = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.light")
                    .chantPage("eidolon_repraised.codex.page.light", Spells.LIGHT_CHANT)
                    .build();

            FIRE_TOUCH = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.fire_touch")
                    .chantPage("eidolon_repraised.codex.page.fire_touch", Spells.FIRE_CHANT)
                    .build();

            CHILL_TOUCH = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.chill_touch")
                    .chantPage("eidolon_repraised.codex.page.chill_touch", Spells.FROST_CHANT)
                    .build();

            WATER = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.water")
                    .chantPage("eidolon_repraised.codex.page.water", Spells.WATER_CHANT)
                    .textPage("eidolon_repraised.codex.page.water.1")
                    .build();

            ENTHRALL = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.enthrall")
                    .chantPage("eidolon_repraised.codex.page.enthrall", Spells.ENTHRALL_UNDEAD)
                    .textPage("eidolon_repraised.codex.page.enthrall.1")
                    .build();

            SMITE = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.smite")
                    .chantPage("eidolon_repraised.codex.page.smite", Spells.SMITE_CHANT)
                    .build();

            SUNDER_ARMOR = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.sunder_armor")
                    .chantPage("eidolon_repraised.codex.page.sunder_armor", Spells.SUNDER_ARMOR)
                    .build();

            REINFORCE_ARMOR = new CodexBuilder()
                    .title("eidolon_repraised.codex.chapter.reinforce_armor")
                    .chantPage("eidolon_repraised.codex.page.reinforce_armor", Spells.BLESS_ARMOR)
                    .build();

            SPELLS_INDEX = new Index(
                    "eidolon_repraised.codex.chapter.spells",
                    new TitledIndexPage(
                            "eidolon_repraised.codex.page.spells",
                            new IndexEntry(MANA, new ItemStack(Registry.CODEX.get())),
                            new SignLockedEntry(LIGHT, new ItemStack(Items.LANTERN), Signs.FLAME_SIGN),
                            new ResearchLockedEntry(FIRE_TOUCH, new ItemStack(Items.FLINT_AND_STEEL), Researches.FIRE_SPELL),
                            new ResearchLockedEntry(CHILL_TOUCH, new ItemStack(Items.ICE), Researches.FROST_SPELL),
                            new SignLockedEntry(WATER, new ItemStack(Items.WATER_BUCKET), Signs.WINTER_SIGN, Signs.FLAME_SIGN)
                    ), new IndexPage(
                    new FactLockedEntry(ENTHRALL, new ItemStack(Registry.SUMMONING_STAFF.get()), Facts.ENTHRALL),
                    new FactLockedEntry(SMITE, new ItemStack(Registry.SILVER_SWORD.get()), Facts.SMITE),
                    new SignLockedEntry(SUNDER_ARMOR, new ItemStack(Items.FERMENTED_SPIDER_EYE), Signs.WARDING_SIGN),
                    new SignLockedEntry(REINFORCE_ARMOR, new ItemStack(Items.GOLDEN_CHESTPLATE), Signs.WARDING_SIGN)
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

    private static float lexiconLookupTime = 0;
    private static int oldMouseX = -1;
    private static int oldMouseY = -1;

    public static void onTooltip(GuiGraphics graphics, ItemStack stack, int mouseX, int mouseY) {
        PoseStack ms = graphics.pose();
        Minecraft mc = Minecraft.getInstance();
        int tooltipX = mouseX;
        int tooltipY = mouseY - 4;
        if (mc.player == null) {
            return;
        }

        var docEntry = itemToEntryMap.get(stack.getItem());

        if (docEntry == null || docEntry.chapter == null || !docEntry.isUnlocked()) {
            return;
        }
        boolean hasSpellBook = false;
        for (int i = 0; i < Inventory.getSelectionSize(); i++) {
            ItemStack stackAt = mc.player.getInventory().getItem(i);
            if (!stackAt.isEmpty()) {
                if (stackAt.getItem() instanceof CodexItem) {
                    hasSpellBook = true;
                }
            }
        }
        if (!hasSpellBook) {
            lexiconLookupTime = 0F;
            return;
        }
        if (lexiconLookupTime > 0) {
            if (oldMouseX != mouseX || oldMouseY != mouseY) {
                lexiconLookupTime = 0F;
            }
        }

        if (mc.screen instanceof CodexGui pageHolderScreen && pageHolderScreen.currentChapter == docEntry.chapter) {
            return;
        }

        int x = tooltipX - 34;
        RenderSystem.disableDepthTest();

        graphics.fill(x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
        graphics.fill(x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);

        boolean boundToControl = EidolonKeybindings.OPEN_BOOK.getKey().getValue() == 341;
        if (boundToControl ? Screen.hasControlDown() :
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), EidolonKeybindings.OPEN_BOOK.getKey().getValue())) {
            lexiconLookupTime += ClientInfo.deltaTicks;
            oldMouseX = mouseX;
            oldMouseY = mouseY;

            int cx = x + 8;
            int cy = tooltipY + 8;
            float r = 12;
            float time = 20F;
            float angles = lexiconLookupTime / time * 360F;
            float a = 0.5F + 0.2F * ((float) Math.cos(ClientInfo.totalTicks / 10.0) * 0.5F + 0.5F);
            RenderSystem.enableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            buf.addVertex(cx, cy, 0).setColor(0.5F, 0.0F, 0.6F, a);

            for (float i = angles; i > 0; i--) {
                double rad = Math.toRadians(i - 90);
                buf.addVertex((float) (cx + Math.cos(rad) * r), (float) (cy + Math.sin(rad) * r), 0).setColor(0.75F, 0F, 1.0F, 1F);
            }

            buf.addVertex(cx, cy, 0).setColor(.75F, 0F, 1.0F, 0F);

            BufferUploader.drawWithShader(buf.build());
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();

            if (lexiconLookupTime >= time) {
                CodexGui.openToEntry(docEntry.chapter, 0);
                lexiconLookupTime = 0F;
            }
        } else {
            lexiconLookupTime = 0f;
        }

        ms.pushPose();
        ms.translate(0, 0, 300);

        RenderSystem.enableDepthTest();

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        graphics.renderFakeItem(stack, x, tooltipY);
        graphics.renderItemDecorations(font, stack, x, tooltipY);
        RenderSystem.disableBlend();

        ms.popPose();

        ms.pushPose();
        ms.translate(0, 0, 500);
        graphics.drawString(mc.font, "?", x + 10, tooltipY + 8, 0xFFFFFFFF);

        ms.scale(0.5F, 0.5F, 1F);
        boolean mac = Minecraft.ON_OSX;
        Component key = (boundToControl ? (mac ? Component.literal("Cmd") : Component.literal("Ctrl")) : EidolonKeybindings.OPEN_BOOK.getTranslatedKeyMessage().copy())
                .withStyle(ChatFormatting.BOLD);
        graphics.drawString(mc.font, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF);
        ms.popPose();

        RenderSystem.enableDepthTest();
    }
}
