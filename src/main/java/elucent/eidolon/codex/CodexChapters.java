package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import elucent.eidolon.capability.Facts;
import elucent.eidolon.codex.IndexPage.FactLockedEntry;
import elucent.eidolon.codex.IndexPage.IndexEntry;
import elucent.eidolon.codex.IndexPage.ReputationLockedEntry;
import elucent.eidolon.codex.IndexPage.ResearchLockedEntry;
import elucent.eidolon.codex.IndexPage.SignLockedEntry;
import elucent.eidolon.codex.ListPage.ListEntry;
import elucent.eidolon.codex.SignIndexPage.SignEntry;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.common.item.CodexItem;
import elucent.eidolon.registries.*;
import elucent.eidolon.util.ClientInfo;
import elucent.eidolon.util.ColorUtil;
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
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static elucent.eidolon.Eidolon.prefix;

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
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            // If the level is null, we can't initialize the codex properly.
            // This can happen if the game is not fully loaded or if we're in a non-game context.
            return;
        }
        MinecraftForge.EVENT_BUS.post(new CodexEvents.PreInit(categories, itemToEntryMap));

        //NATURE
        {
            MONSTERS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.monsters")
                    .titlePage("eidolon.codex.page.monsters.zombie_brute")
                    .entityPage(EidolonEntities.ZOMBIE_BRUTE.get())
                    .titlePage("eidolon.codex.page.monsters.wraith")
                    .entityPage(EidolonEntities.WRAITH.get())
                    .titlePage("eidolon.codex.page.monsters.chilled")
                    .textPage("")
                    .entityPage(EidolonEntities.GIANT_SKEL.get())
                    .titlePage("eidolon.codex.page.monsters.giant_skeleton")
                    .entityPage(EidolonEntities.NECROMANCER.get())
                    .titlePage("eidolon.codex.page.monsters.necromancer")
                    .build();

            CRITTERS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.critters")
                    .titlePage("eidolon.codex.page.critters.raven")
                    .entityPage(EidolonEntities.RAVEN.get())
                    .titlePage("eidolon.codex.page.critters.slimy_slug")
                    .entityPage(EidolonEntities.SLIMY_SLUG.get())
                    .titledRitualPage("eidolon.codex.page.summon_ritual_c.1", prefix("summon_ravens"))
                    .titledRitualPage("eidolon.codex.page.summon_ritual_c.2", prefix("summon_slugs"))
                    .build();

            ORES = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.ores")
                    .titlePage("eidolon.codex.page.ores.lead_ore")
                    .titlePage("eidolon.codex.page.ores.silver_ore")
                    .addSupportedRecipePages(prefix("smelt_lead_ore"))
                    .addSupportedRecipePages(prefix("smelt_silver_ore"))
                    .addSupportedRecipePages(Registry.LEAD_BLOCK.get())
                    .addSupportedRecipePages(prefix("decompress_lead_ingot"))
                    .addSupportedRecipePages(Registry.SILVER_BLOCK.get())
                    .addSupportedRecipePages(prefix("decompress_silver_ingot"))
                    .build();

            PEWTER = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.pewter")
                    .titlePage("eidolon.codex.page.pewter")
                    .addSupportedRecipePages(Registry.PEWTER_BLEND.get())
                    .addSupportedRecipePages(prefix("smelt_pewter_blend"))
                    .addSupportedRecipePages(Registry.PEWTER_BLOCK.get())
                    .addSupportedRecipePages(prefix("decompress_pewter_ingot"))
                    .build();

            ENCHANTED_ASH = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.enchanted_ash")
                    .titlePage("eidolon.codex.page.enchanted_ash")
                    .smeltingPage(new ItemStack(Registry.ENCHANTED_ASH.get(), 2), new ItemStack(Items.BONE))
                    .build();

            PLANTS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.plants")
                    .titlePage("eidolon.codex.page.plants")
                    .addSupportedRecipePages(Registry.ATHAME.get())
                    .titlePage("eidolon.codex.page.plants.planter", new ItemStack(Registry.PLANTER.get()))
                    .addSupportedRecipePages(Registry.PLANTER.get())
                    .titlePage("eidolon.codex.page.plants.oanna", new ItemStack(Registry.OANNA_BLOOM.get()))
                    .titlePage("eidolon.codex.page.plants.mirecap", new ItemStack(Registry.MIRECAP.get()))
                    .titlePage("eidolon.codex.page.plants.sildran", new ItemStack(Registry.SILDRIAN_SEED.get()))
                    .titlePage("eidolon.codex.page.plants.avenna", new ItemStack(Registry.AVENNIAN_SPRIG.get()))
                    .titlePage("eidolon.codex.page.plants.merammer", new ItemStack(Registry.MERAMMER_ROOT.get()))
                    .addSupportedRecipePages(Registry.MERAMMER_RESIN.get())
                    .build();

            RESEARCHES = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.researches")
                    .textPage("eidolon.codex.page.researches.0")
                    .addSupportedRecipePages(Registry.RESEARCH_TABLE.get())
                    .addSupportedRecipePages(Registry.MAGICIANS_WAX.get())
                    .addSupportedRecipePages(Registry.ARCANE_SEAL.get())
                    .addSupportedRecipePages(Registry.MAGIC_INK.get())
                    .addSupportedRecipePages(Registry.PARCHMENT.get())
                    .addSupportedRecipePages(Registry.NOTETAKING_TOOLS.get())
                    .build();

            DECORATIONS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.decorations")
                    .titlePage("eidolon.codex.page.decorations")
                    .addSupportedRecipePages(Registry.ELDER_BRICK.get())
                    .addSupportedRecipePages(Registry.ELDER_BRICKS.getBlock())
                    .addSupportedRecipePages(Registry.BONE_PILE.getBlock())
                    .build();

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
            BRAZIER = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.brazier")
                    .titlePage("eidolon.codex.page.brazier.0")
                    .textPage("eidolon.codex.page.brazier.1")
                    .addSupportedRecipePages(Registry.BRAZIER.get())
                    .build();

            ITEM_PROVIDERS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.item_providers")
                    .titlePage("eidolon.codex.page.item_providers.0")
                    .addSupportedRecipePages(Registry.STONE_HAND.get())
                    .titlePage("eidolon.codex.page.item_providers.1")
                    .addSupportedRecipePages(Registry.NECROTIC_FOCUS.get())
                    .build();

            CRYSTAL_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.crystal_ritual")
                    .titledRitualPage("eidolon.codex.page.crystal_ritual", RitualRegistry.CRYSTAL_RITUAL)
                    .textPage("eidolon.codex.page.crystal_ritual")
                    .build();

            SUMMON_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.summon_ritual")
                    .titledRitualPage("eidolon.codex.page.summon_ritual.0", prefix("summon_zombie"))
                    .textPage("eidolon.codex.page.summon_ritual.0")
                    .titledRitualPage("eidolon.codex.page.summon_ritual.1", prefix("summon_skeleton"))
                    .titledRitualPage("eidolon.codex.page.summon_ritual.2", prefix("summon_phantom"))
                    .titledRitualPage("eidolon.codex.page.summon_ritual.3", prefix("summon_wither_skeleton"))
                    .titledRitualPage("eidolon.codex.page.summon_ritual.4", prefix("summon_husk"))
                    .titledRitualPage("eidolon.codex.page.summon_ritual.5", prefix("summon_drowned"))
                    .titledRitualPage("eidolon.codex.page.summon_ritual.6", prefix("summon_stray"))
                    .titledRitualPage("eidolon.codex.page.summon_ritual.7", prefix("summon_wraith"))
                    .build();

            ALLURE_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.allure_ritual")
                    .titledRitualPage("eidolon.codex.page.allure_ritual", RitualRegistry.ALLURE_RITUAL)
                    .textPage("eidolon.codex.page.allure_ritual")
                    .build();

            REPELLING_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.repelling_ritual")
                    .titledRitualPage("eidolon.codex.page.repelling_ritual", RitualRegistry.REPELLING_RITUAL)
                    .textPage("eidolon.codex.page.repelling_ritual")
                    .build();

            DECEIT_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.deceit_ritual")
                    .titledRitualPage("eidolon.codex.page.deceit_ritual", RitualRegistry.DECEIT_RITUAL)
                    .textPage("eidolon.codex.page.deceit_ritual")
                    .build();

            TIME_RITUALS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.time_rituals")
                    .titledRitualPage("eidolon.codex.page.time_rituals.0", RitualRegistry.DAYLIGHT_RITUAL)
                    .textPage("eidolon.codex.page.time_rituals.0")
                    .titledRitualPage("eidolon.codex.page.time_rituals.1", RitualRegistry.MOONLIGHT_RITUAL)
                    .textPage("eidolon.codex.page.time_rituals.1")
                    .build();

            PURIFY_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.purify_ritual")
                    .titledRitualPage("eidolon.codex.page.purify_ritual", RitualRegistry.PURIFY_RITUAL)
                    .textPage("eidolon.codex.page.purify_ritual")
                    .build();

            SANGUINE_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.sanguine_ritual")
                    .titledRitualPage("eidolon.codex.page.sanguine_ritual.0", Registry.SAPPING_SWORD.get().getDefaultInstance())
                    .textPage("eidolon.codex.page.sanguine_ritual.0")
                    .titledRitualPage("eidolon.codex.page.sanguine_ritual.1", Registry.SANGUINE_AMULET.get().getDefaultInstance())
                    .textPage("eidolon.codex.page.sanguine_ritual.1")
                    .build();

            RECHARGE_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.recharge_ritual")
                    .titledRitualPage("eidolon.codex.page.recharge_ritual.soulfire", RitualRegistry.RECHARGE_SOULFIRE_RITUAL)
                    .titledRitualPage("eidolon.codex.page.recharge_ritual.bonechill", RitualRegistry.RECHARGE_BONECHILL_RITUAL)
                    .textPage("eidolon.codex.page.recharge_ritual")
                    .build();

            CAPTURE_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.capture_ritual")
                    .titledRitualPage("eidolon.codex.page.capture_ritual", RitualRegistry.ABSORB_RITUAL)
                    .textPage("eidolon.codex.page.capture_ritual")
                    .build();

            LOCATE_RITUAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.locate_ritual")
                    .titledRitualPage("eidolon.codex.page.locate_ritual", prefix("ritual_catacomb_locator"))
                    .textPage("eidolon.codex.page.locate_ritual")
                    .build();

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

            WOODEN_STAND = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.wooden_stand")
                    .titlePage("eidolon.codex.page.wooden_stand.0")
                    .craftingPage(Registry.WOODEN_STAND.get().asItem())
                    .titlePage("eidolon.codex.page.wooden_stand.1")
                    .cruciblePage(new ItemStack(Registry.FUNGUS_SPROUTS.get(), 2))
                    .build();

            TALLOW = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.tallow")
                    .titlePage("eidolon.codex.page.tallow.0")
                    .smeltingPage(new ItemStack(Registry.TALLOW.get()), new ItemStack(Items.ROTTEN_FLESH))
                    .titlePage("eidolon.codex.page.tallow.1")
                    .addSupportedRecipePages(Registry.CANDLE.get())
                    .addSupportedRecipePages(Registry.CANDLESTICK.get())
                    .build();

            CRUCIBLE = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.crucible")
                    .titlePage("eidolon.codex.page.crucible.0")
                    .textPage("eidolon.codex.page.crucible.1")
                    .addSupportedRecipePages(Registry.CRUCIBLE.get())
                    .build();

            ARCANE_GOLD = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.arcane_gold")
                    .titlePage("eidolon.codex.page.arcane_gold")
                    .addSupportedRecipePages(prefix("arcane_gold_ingot_alchemy"))
                    .addSupportedRecipePages(Registry.ARCANE_GOLD_BLOCK.get())
                    .addSupportedRecipePages(prefix("decompress_arcane_gold_ingot"))
                    .build();

            REAGENTS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.reagents")
                    .titlePage("eidolon.codex.page.reagents.0")
                    .addSupportedRecipePages(Registry.SULFUR.get())
                    .titlePage("eidolon.codex.page.reagents.1")
                    .addSupportedRecipePages(Registry.DEATH_ESSENCE.get())
                    .titlePage("eidolon.codex.page.reagents.2")
                    .addSupportedRecipePages(prefix("crimson_essence_fungus"))
                    .addSupportedRecipePages(prefix("crimson_essence_vines"))
                    .addSupportedRecipePages(prefix("crimson_essence_roots"))
                    .titlePage("eidolon.codex.page.reagents.3")
                    .addSupportedRecipePages(Registry.ENDER_CALX.get())
                    .build();

            SOUL_GEMS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.soul_gems")
                    .titlePage("eidolon.codex.page.soul_gems")
                    .addSupportedRecipePages(Registry.LESSER_SOUL_GEM.get())
                    .build();

            SHADOW_GEM = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.shadow_gem")
                    .titlePage("eidolon.codex.page.shadow_gem")
                    .addSupportedRecipePages(Registry.SHADOW_GEM.get())
                    .build();

            WARPED_SPROUTS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.warped_sprouts")
                    .titlePage("eidolon.codex.page.warped_sprouts.0")
                    .addSupportedRecipePages(Registry.WARPED_SPROUTS.get())
                    .titlePage("eidolon.codex.page.warped_sprouts.1")
                    .build();

            BASIC_ALCHEMY = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.basic_alchemy")
                    .titlePage("eidolon.codex.page.basic_alchemy.0")
                    .addSupportedRecipePages(prefix("flesh_to_leather"))
                    .titlePage("eidolon.codex.page.basic_alchemy.1")
                    .addSupportedRecipePages(prefix("meat_to_flesh"))
                    .titlePage("eidolon.codex.page.basic_alchemy.2")
                    .addSupportedRecipePages(prefix("gunpowder_alchemy"))
                    .titlePage("eidolon.codex.page.basic_alchemy.3")
                    .addSupportedRecipePages(prefix("gilding_apple"))
                    .addSupportedRecipePages(prefix("gilding_carrot"))
                    .addSupportedRecipePages(prefix("gilding_melon"))
                    .build();

            INLAYS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.inlays")
                    .titlePage("eidolon.codex.page.inlays")
                    .craftingPage(new ItemStack(Registry.PEWTER_INLAY.get(), 2))
                    .craftingPage(new ItemStack(Registry.GOLD_INLAY.get(), 2))
                    .build();

            BASIC_BAUBLES = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.basic_baubles")
                    .titlePage("eidolon.codex.page.basic_baubles")
                    .craftingPage(Registry.BASIC_AMULET.get())
                    .craftingPage(Registry.BASIC_RING.get())
                    .craftingPage(Registry.BASIC_BELT.get())
                    .build();

            MAGIC_WORKBENCH = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.magic_workbench")
                    .titlePage("eidolon.codex.page.magic_workbench")
                    .craftingPage(Registry.WORKTABLE.get().asItem())
                    .build();

            VOID_AMULET = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.void_amulet")
                    .titlePage("eidolon.codex.page.void_amulet")
                    .worktablePage(Registry.VOID_AMULET.get())
                    .build();

            WARDED_MAIL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.warded_mail")
                    .titlePage("eidolon.codex.page.warded_mail")
                    .worktablePage(Registry.WARDED_MAIL.get())
                    .build();

            SOULFIRE_WAND = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.soulfire_wand")
                    .titlePage("eidolon.codex.page.soulfire_wand")
                    .worktablePage(Registry.SOULFIRE_WAND.get())
                    .build();

            BONECHILL_WAND = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.bonechill_wand")
                    .titlePage("eidolon.codex.page.bonechill_wand")
                    .worktablePage(Registry.BONECHILL_WAND.get())
                    .build();

            REAPER_SCYTHE = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.reaper_scythe")
                    .titlePage("eidolon.codex.page.reaper_scythe")
                    .worktablePage(Registry.REAPER_SCYTHE.get())
                    .titlePage("eidolon.codex.page.death_scythe")
                    .worktablePage(Registry.DEATHBRINGER_SCYTHE.get())
                    .build();

            CLEAVING_AXE = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.cleaving_axe")
                    .titlePage("eidolon.codex.page.cleaving_axe")
                    .worktablePage(Registry.CLEAVING_AXE.get())
                    .build();

            SOUL_ENCHANTER = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.soul_enchanter")
                    .titlePage("eidolon.codex.page.soul_enchanter.0")
                    .textPage("eidolon.codex.page.soul_enchanter.1")
                    .worktablePage(Registry.SOUL_ENCHANTER.get().asItem())
                    .build();

            REVERSAL_PICK = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.reversal_pick")
                    .titlePage("eidolon.codex.page.reversal_pick")
                    .worktablePage(Registry.REVERSAL_PICK.get())
                    .build();

            WARLOCK_ARMOR = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.warlock_armor")
                    .titlePage("eidolon.codex.page.warlock_armor.0")
                    .worktablePage(new ItemStack(Registry.WICKED_WEAVE.get(), 8))
                    .titlePage("eidolon.codex.page.warlock_armor.1")
                    .worktablePage(Registry.WARLOCK_HAT.get())
                    .titlePage("eidolon.codex.page.warlock_armor.2")
                    .worktablePage(Registry.WARLOCK_CLOAK.get())
                    .titlePage("eidolon.codex.page.warlock_armor.3")
                    .worktablePage(Registry.WARLOCK_BOOTS.get())
                    .build();

            GRAVITY_BELT = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.gravity_belt")
                    .titlePage("eidolon.codex.page.gravity_belt")
                    .worktablePage(Registry.GRAVITY_BELT.get())
                    .build();

            PRESTIGIOUS_PALM = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.prestigious_palm")
                    .titlePage("eidolon.codex.page.prestigious_palm")
                    .worktablePage(Registry.PRESTIGIOUS_PALM.get())
                    .build();

            MIND_SHIELDING_PLATE = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.mind_shielding_plate")
                    .titlePage("eidolon.codex.page.mind_shielding_plate")
                    .worktablePage(Registry.MIND_SHIELDING_PLATE.get())
                    .build();

            RESOLUTE_BELT = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.resolute_belt")
                    .titlePage("eidolon.codex.page.resolute_belt")
                    .worktablePage(Registry.RESOLUTE_BELT.get())
                    .build();

            GLASS_HAND = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.glass_hand")
                    .titlePage("eidolon.codex.page.glass_hand")
                    .worktablePage(Registry.GLASS_HAND.get())
                    .build();

            SOULBONE = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.soulbone_amulet")
                    .titlePage("eidolon.codex.page.soulbone_amulet")
                    .worktablePage(Registry.SOULBONE_AMULET.get())
                    .titlePage("eidolon.codex.page.soulbone_amulet.1")
                    .titlePage("eidolon.codex.page.bonelord_armor")
                    .textPage("eidolon.codex.page.bonelord_armor.1")
                    .worktablePage(Registry.BONELORD_HELM.get())
                    .worktablePage(Registry.BONELORD_CHESTPLATE.get())
                    .worktablePage(Registry.BONELORD_GREAVES.get())
                    .build();

            RAVEN_CLOAK = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.raven_cloak")
                    .titlePage("eidolon.codex.page.raven_cloak")
                    .worktablePage(Registry.RAVEN_CLOAK.get())
                    .build();

            NECROMANCER_STAFF = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.summoning_staff")
                    .titlePage("eidolon.codex.page.summoning_staff")
                    .textPage("eidolon.codex.page.summoning_staff.1")
                    .build();

            ARROW_RING = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.angel_sight")
                    .titlePage("eidolon.codex.page.angel_sight")
                    .worktablePage(Registry.ANGELS_SIGHT.get())
                    .build();


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
            INTRO_SIGNS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.intro_signs")
                    .titlePage("eidolon.codex.page.intro_signs.0")
                    .textPage("eidolon.codex.page.intro_signs.1")
                    .build();

            EFFIGY = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.effigy")
                    .titlePage("eidolon.codex.page.effigy")
                    .craftingPage(Registry.STRAW_EFFIGY.get().asItem())
                    .build();

            ALTARS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.altars")
                    .titlePage("eidolon.codex.page.altars.0")
                    .textPage("eidolon.codex.page.altars.1")
                    .craftingPage(new ItemStack(Registry.WOODEN_ALTAR.get(), 3))
                    .titlePage("eidolon.codex.page.stone_altar")
                    .worktablePage(new ItemStack(Registry.STONE_ALTAR.get(), 3))
                    .build();

            ALTAR_LIGHTS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.altar_lights")
                    .titlePage("eidolon.codex.page.altar_lights.0")
                    .listPage("eidolon.codex.page.altar_lights.1",
                            new ListEntry("torch", new ItemStack(Items.TORCH), Blocks.TORCH),
                            new ListEntry("soultorch", new ItemStack(Items.SOUL_TORCH), Blocks.SOUL_TORCH),
                            new ListEntry("lantern", new ItemStack(Items.LANTERN), Blocks.LANTERN),
                            new ListEntry("candle", new ItemStack(Registry.CANDLE.get())),
                            new ListEntry("candlestick", new ItemStack(Registry.CANDLESTICK.get())),
                            new ListEntry("magic_candle", new ItemStack(Registry.MAGIC_CANDLE.get())),
                            new ListEntry("magic_candlestick", new ItemStack(Registry.MAGIC_CANDLESTICK.get()))
                    )
                    .build();

            ALTAR_SKULLS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.altar_skulls")
                    .titlePage("eidolon.codex.page.altar_skulls.0")
                    .listPage("eidolon.codex.page.altar_skulls.1",
                            new ListEntry("skull", new ItemStack(Items.SKELETON_SKULL)),
                            new ListEntry("zombie", new ItemStack(Items.ZOMBIE_HEAD)),
                            new ListEntry("wither_skull", new ItemStack(Items.WITHER_SKELETON_SKULL))
                    )
                    .build();

            ALTAR_HERBS = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.altar_herbs")
                    .titlePage("eidolon.codex.page.altar_herbs.0")
                    .listPage("eidolon.codex.page.altar_herbs.1",
                            new ListEntry("crimson_fungus", new ItemStack(Items.CRIMSON_FUNGUS), Blocks.POTTED_CRIMSON_FUNGUS),
                            new ListEntry("warped_fungus", new ItemStack(Items.WARPED_FUNGUS), Blocks.POTTED_WARPED_FUNGUS),
                            new ListEntry("wither_rose", new ItemStack(Items.WITHER_ROSE), Blocks.POTTED_WITHER_ROSE)
                    )
                    .build();

            GOBLET = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.goblet")
                    .titlePage("eidolon.codex.page.goblet")
                    .craftingPage(Registry.GOBLET.get().asItem())
                    .build();

            CENSER = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.censer")
                    .titlePage("eidolon.codex.page.censer")
                    .craftingPage(Registry.CENSER.get().asItem())
                    .textPage("eidolon_repraised.codex.page.incense.1")
                    .addSupportedRecipePages(Registry.RESTORATION_INCENSE.get())
                    .addSupportedRecipePages(Registry.GLOOM_INCENSE.get())
                    .textPage("eidolon_repraised.codex.page.incense.2")
                    .addSupportedRecipePages(Registry.UNDEATH_INCENSE.get())
                    .addSupportedRecipePages(Registry.DEATH_BANE_INCENSE.get())
                    .textPage("eidolon_repraised.codex.page.incense.3")
                    .addSupportedRecipePages(Registry.TOUGH_INCENSE.get())
                    .addSupportedRecipePages(Registry.FRAIL_INCENSE.get())
                    //.addSupportedRecipePages(Registry.WARDING_INCENSE.get())
                    .addSupportedRecipePages(Registry.PURITY_INCENSE.get())
                    .addSupportedRecipePages(Registry.QUICKEN_INCENSE.get())
                    .addSupportedRecipePages(Registry.BLOODLUST_INCENSE.get())
                    .addSupportedRecipePages(Registry.SOUL_HARVEST_INCENSE.get())
                    .textPage("eidolon_repraised.codex.page.incense.4")
                    .addSupportedRecipePages(Registry.FROSTBIND_INCENSE.get())
                    .addSupportedRecipePages(Registry.TETHER_INCENSE.get())
                    .build();

            DARK_PRAYER = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.dark_prayer")
                    .chantPage("eidolon.codex.page.dark_prayer.0", Spells.DARK_PRAYER)
                    .textPage("eidolon.codex.page.dark_prayer.1")
                    .build();

            LIGHT_PRAYER = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.light_prayer")
                    .chantPage("eidolon.codex.page.light_prayer.0", Spells.LIGHT_PRAYER)
                    .textPage("eidolon.codex.page.light_prayer.1")
                    .build();

            ANIMAL_SACRIFICE = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.animal_sacrifice")
                    .chantPage("eidolon.codex.page.animal_sacrifice", Spells.DARK_ANIMAL_SACRIFICE)
                    .build();

            INCENSE_BURN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.censer_offering")
                    .titlePage("eidolon.codex.page.censer_offering")
                    .cruciblePage(new ItemStack(Registry.OFFERING_INCENSE.get(), 2))
                    .build();

            DARK_TOUCH = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.dark_touch")
                    .chantPage("eidolon.codex.page.dark_touch.0", Spells.DARK_TOUCH)
                    .textPage("eidolon.codex.page.dark_touch.1")
                    .build();

            HOLY_TOUCH = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.holy_touch")
                    .chantPage("eidolon.codex.page.holy_touch.0", Spells.HOLY_TOUCH)
                    .textPage("eidolon.codex.page.holy_touch.1")
                    .build();

            UNHOLY_EFFIGY = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.unholy_effigy")
                    .titlePage("eidolon.codex.page.unholy_effigy")
                    .worktablePage(Registry.ELDER_EFFIGY.get().asItem())
                    .build();

            HOLY_EFFIGY = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.holy_effigy")
                    .titlePage("eidolon.codex.page.holy_effigy")
                    .worktablePage(Registry.ELDER_EFFIGY.get().asItem())
                    .build();

            VILLAGER_SACRIFICE = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.villager_sacrifice")
                    .chantPage("eidolon.codex.page.villager_sacrifice", Spells.DARK_VILLAGER_SACRIFICE)
                    .build();

            HEAL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.lay_on_hands")
                    .chantPage("eidolon.codex.page.lay_on_hands", Spells.LAY_ON_HANDS)
                    .build();

            ZOMBIFY = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.villager_zombie")
                    .chantPage("eidolon.codex.page.villager_zombie", Spells.ZOMBIFY)
                    .textPage("eidolon.codex.page.villager_zombie.1")
                    .build();

            CURE_ZOMBIE = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.villager_cure")
                    .chantPage("eidolon.codex.page.villager_cure", Spells.CURE_ZOMBIE_CHANT)
                    .textPage("eidolon.codex.page.villager_cure.1")
                    .build();


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
                            new FactLockedEntry(HEAL, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING), Facts.VILLAGER_HEALING),
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
            WICKED_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.wicked_sign")
                    .titlePage("eidolon.codex.page.wicked_sign")
                    .signPage(Signs.WICKED_SIGN)
                    .build();

            SACRED_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.sacred_sign")
                    .titlePage("eidolon.codex.page.sacred_sign")
                    .signPage(Signs.SACRED_SIGN)
                    .build();

            BLOOD_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.blood_sign")
                    .titlePage("eidolon.codex.page.blood_sign")
                    .signPage(Signs.BLOOD_SIGN)
                    .build();

            SOUL_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.soul_sign")
                    .titlePage("eidolon.codex.page.soul_sign")
                    .signPage(Signs.SOUL_SIGN)
                    .build();

            MIND_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.mind_sign")
                    .titlePage("eidolon.codex.page.mind_sign")
                    .signPage(Signs.MIND_SIGN)
                    .build();

            FLAME_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.flame_sign")
                    .titlePage("eidolon.codex.page.flame_sign")
                    .signPage(Signs.FLAME_SIGN)
                    .build();

            WINTER_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.winter_sign")
                    .titlePage("eidolon.codex.page.winter_sign")
                    .signPage(Signs.WINTER_SIGN)
                    .build();

            HARMONY_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.harmony_sign")
                    .titlePage("eidolon.codex.page.harmony_sign")
                    .signPage(Signs.HARMONY_SIGN)
                    .build();

            DEATH_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.death_sign")
                    .titlePage("eidolon.codex.page.death_sign")
                    .signPage(Signs.DEATH_SIGN)
                    .build();

            WARDING_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.warding_sign")
                    .titlePage("eidolon.codex.page.warding_sign")
                    .signPage(Signs.WARDING_SIGN)
                    .build();

            MAGIC_SIGN = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.magic_sign")
                    .titlePage("eidolon.codex.page.magic_sign")
                    .signPage(Signs.MAGIC_SIGN)
                    .build();

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
            MANA = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.mana")
                    .titlePage("eidolon.codex.page.mana")
                    .textPage("eidolon.codex.page.mana.1")
                    .build();

            LIGHT = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.light")
                    .chantPage("eidolon.codex.page.light", Spells.LIGHT_CHANT)
                    .build();

            FIRE_TOUCH = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.fire_touch")
                    .chantPage("eidolon.codex.page.fire_touch", Spells.FIRE_CHANT)
                    .build();

            CHILL_TOUCH = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.chill_touch")
                    .chantPage("eidolon.codex.page.chill_touch", Spells.FROST_CHANT)
                    .build();

            WATER = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.water")
                    .chantPage("eidolon.codex.page.water", Spells.WATER_CHANT)
                    .textPage("eidolon.codex.page.water.1")
                    .build();

            ENTHRALL = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.enthrall")
                    .chantPage("eidolon.codex.page.enthrall", Spells.ENTHRALL_UNDEAD)
                    .textPage("eidolon.codex.page.enthrall.1")
                    .build();

            SMITE = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.smite")
                    .chantPage("eidolon.codex.page.smite", Spells.SMITE_CHANT)
                    .build();

            SUNDER_ARMOR = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.sunder_armor")
                    .chantPage("eidolon.codex.page.sunder_armor", Spells.SUNDER_ARMOR)
                    .build();

            REINFORCE_ARMOR = new CodexBuilder(level)
                    .title("eidolon.codex.chapter.reinforce_armor")
                    .chantPage("eidolon.codex.page.reinforce_armor", Spells.BLESS_ARMOR)
                    .build();

            SPELLS_INDEX = new Index(
                    "eidolon.codex.chapter.spells",
                    new TitledIndexPage(
                            "eidolon.codex.page.spells",
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

        MinecraftForge.EVENT_BUS.post(new CodexEvents.PostInit(categories, itemToEntryMap));
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
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buf = tesselator.getBuilder();

            buf.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            buf.vertex(cx, cy, 0).color(0.5F, 0.0F, 0.6F, a).endVertex();

            for (float i = angles; i > 0; i--) {
                double rad = Math.toRadians(i - 90);
                buf.vertex((float) (cx + Math.cos(rad) * r), (float) (cy + Math.sin(rad) * r), 0).color(0.75F, 0F, 1.0F, 1F).endVertex();
            }

            buf.vertex(cx, cy, 0).color(.75F, 0F, 1.0F, 0F).endVertex();

            tesselator.end();
            RenderSystem.disableBlend();

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
        Component key = (boundToControl ? mac ? Component.literal("Cmd") : Component.literal("Ctrl") : EidolonKeybindings.OPEN_BOOK.getTranslatedKeyMessage().copy())
                .withStyle(ChatFormatting.BOLD);
        graphics.drawString(mc.font, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF);
        ms.popPose();

        RenderSystem.enableDepthTest();
    }
}
