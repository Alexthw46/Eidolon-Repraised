package elucent.eidolon.registries;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.*;
import elucent.eidolon.codex.Page;
import elucent.eidolon.codex.RitualPage;
import elucent.eidolon.gui.jei.RecipeWrappers;
import elucent.eidolon.ritual.*;
import elucent.eidolon.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.*;
import java.util.Map.Entry;

public class RitualRegistry {
    static final Map<ResourceLocation, Ritual> rituals = new HashMap<>();
    static final BiMap<ItemSacrifice, Ritual> matches = HashBiMap.create();

    public static void register(ItemStack sacrifice, Ritual ritual) {
        ResourceLocation name = ritual.getRegistryName();
        assert name != null;
        rituals.put(name, ritual);
        matches.put(new ItemSacrifice(sacrifice), ritual);
    }

    public static Ritual register(ItemLike sacrifice, Ritual ritual) {
        ResourceLocation name = ritual.getRegistryName();
        assert name != null;
        rituals.put(name, ritual);
        matches.put(new ItemSacrifice(sacrifice), ritual);
        return ritual;
    }

    public static Ritual register(TagKey<Item> sacrifice, Ritual ritual) {
        ResourceLocation name = ritual.getRegistryName();
        assert name != null;
        rituals.put(name, ritual);
        matches.put(new ItemSacrifice(sacrifice), ritual);
        return ritual;
    }

    public static Ritual register(MultiItemSacrifice sacrifice, Ritual ritual) {
        ResourceLocation name = ritual.getRegistryName();
        assert name != null;
        rituals.put(name, ritual);
        matches.put(sacrifice, ritual);
        return ritual;
    }

    public static Page getDefaultPage(Ritual ritual, ItemSacrifice sacrifice) {
        List<RitualPage.RitualIngredient> inputs = new ArrayList<>();
        List<ItemStack> foci = new ArrayList<>();
        if (sacrifice instanceof MultiItemSacrifice) for (Ingredient o : ((MultiItemSacrifice) sacrifice).items) {
            foci.add(RecipeUtil.stackFromObject(o));
        }
        int slot = 0;
        for (IRequirement r : ritual.getRequirements()) {
            if (r instanceof ItemRequirement)
                inputs.add(new RitualPage.RitualIngredient(RecipeUtil.stackFromObject(((ItemRequirement) r).getMatch()), false));
            slot++;
        }
        Iterator<ItemStack> iter = foci.iterator();
        while (iter.hasNext()) {
            ItemStack focus = iter.next();
            for (RitualPage.RitualIngredient input : inputs) {
                if (ItemStack.isSameItem(focus, input.stack) && ItemStack.isSameItemSameTags(focus, input.stack)
                    && !input.isFocus) {
                    input.isFocus = true;
                    iter.remove();
                    break;
                }
            }
        }
        ItemStack center = RecipeUtil.stackFromObject(sacrifice instanceof MultiItemSacrifice ? sacrifice.main : sacrifice);

        return new RitualPage(ritual, center, inputs.toArray(new RitualPage.RitualIngredient[0]));
    }


    public static final List<RecipeWrappers.RitualRecipe> wrappedRituals = new ArrayList<>();

    public static ItemSacrifice getMatch(Ritual ritual) {
        return matches.inverse().getOrDefault(ritual, new ItemSacrifice(Ingredient.EMPTY));
    }

    public static List<RecipeWrappers.RitualRecipe> getWrappedRecipes() {
        return wrappedRituals;
    }

    public static Ritual find(ResourceLocation name) {
        return rituals.get(name);
    }

    static boolean matches(Level world, BlockPos pos, ItemSacrifice match, ItemStack sacrifice) {
        if (match instanceof MultiItemSacrifice mis) {
            // check main item first, avoid complicated work
            if (!match.main.test(sacrifice)) return false;

            List<Ingredient> matches = new ArrayList<>(mis.items);
            List<IRitualItemFocus> foci = Ritual.getTilesWithinAABB(IRitualItemFocus.class, world, Ritual.getDefaultBounds(pos));
            List<ItemStack> items = new ArrayList<>();
            for (IRitualItemFocus focus : foci) items.add(focus.provide());
            if (items.size() != matches.size()) return false;
            for (int i = 0; i < matches.size(); i++) {
                int before = matches.size();
                for (int j = 0; j < items.size(); j ++) {
                    Ingredient m = matches.get(i);
                    ItemStack item = items.get(j);
                    if (m.test(item)) {
                        matches.remove(i);
                        i--; // we removed an item, so we need to go back one
                        items.remove(j);
                        break;
                    }
                }
                if (matches.size() == before) return false; // failed to satisfy match with any item
            }
            return matches.isEmpty(); // all matches satisfied
        } else {
            return match.main.test(sacrifice);
        }
    }

    public static Ritual find(Level world, BlockPos pos, ItemStack sacrifice) {
        for (Entry<ItemSacrifice, Ritual> entry : matches.entrySet()) {
            if (matches(world, pos, entry.getKey(), sacrifice)) return entry.getValue();
        }
        return null;
    }

    public static Ritual CRYSTAL_RITUAL,
        SUMMON_ZOMBIE, SUMMON_SKELETON, SUMMON_PHANTOM, SUMMON_HUSK, SUMMON_DROWNED,
        SUMMON_STRAY, SUMMON_WITHER_SKELETON, SUMMON_WRAITH,
        ALLURE_RITUAL, REPELLING_RITUAL, DECEIT_RITUAL, DAYLIGHT_RITUAL, MOONLIGHT_RITUAL,
        PURIFY_RITUAL, RECHARGE_SOULFIRE_RITUAL, RECHARGE_BONECHILL_RITUAL,
        SANGUINE_SWORD, SANGUINE_AMULET,
        ABSORB_RITUAL;

    public static void init() {
        CRYSTAL_RITUAL = register(Items.BONE_MEAL, new CrystalRitual().setRegistryName(Eidolon.MODID, "crystal")
            .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
            .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE)));

        // summons
        SUMMON_ZOMBIE = register(new MultiItemSacrifice(Items.CHARCOAL, Items.ROTTEN_FLESH), new SummonRitual(EntityType.ZOMBIE).setRegistryName(Eidolon.MODID, "summon_zombie")
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Items.ROTTEN_FLESH))
            .addRequirement(new ItemRequirement(Items.ROTTEN_FLESH)));
        SUMMON_SKELETON = register(new MultiItemSacrifice(Items.CHARCOAL, Items.BONE), new SummonRitual(EntityType.SKELETON).setRegistryName(Eidolon.MODID, "summon_skeleton")
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Items.BONE))
            .addRequirement(new ItemRequirement(Items.BONE)));
        SUMMON_PHANTOM = register(new MultiItemSacrifice(Items.CHARCOAL, Items.PHANTOM_MEMBRANE), new SummonRitual(EntityType.PHANTOM).setRegistryName(Eidolon.MODID, "summon_phantom")
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Items.PHANTOM_MEMBRANE))
            .addRequirement(new ItemRequirement(Items.PHANTOM_MEMBRANE)));
        SUMMON_WITHER_SKELETON = register(new MultiItemSacrifice(Items.CHARCOAL, Blocks.SOUL_SAND), new SummonRitual(EntityType.WITHER_SKELETON).setRegistryName(Eidolon.MODID, "summon_wither_skeleton")
            .addRequirement(new ItemRequirement(Items.BONE))
            .addRequirement(new ItemRequirement(Blocks.SOUL_SAND))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));
        SUMMON_HUSK = register(new MultiItemSacrifice(Items.CHARCOAL, Tags.Items.SAND), new SummonRitual(EntityType.HUSK).setRegistryName(Eidolon.MODID, "summon_husk")
            .addRequirement(new ItemRequirement(Items.ROTTEN_FLESH))
            .addRequirement(new ItemRequirement(Tags.Items.SAND))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));
        SUMMON_DROWNED = register(new MultiItemSacrifice(Items.CHARCOAL, Tags.Items.DUSTS_PRISMARINE), new SummonRitual(EntityType.DROWNED).setRegistryName(Eidolon.MODID, "summon_drowned")
            .addRequirement(new ItemRequirement(Items.ROTTEN_FLESH))
            .addRequirement(new ItemRequirement(Tags.Items.DUSTS_PRISMARINE))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));
        SUMMON_STRAY = register(new MultiItemSacrifice(Items.CHARCOAL, Items.STRING), new SummonRitual(EntityType.STRAY).setRegistryName(Eidolon.MODID, "summon_stray")
            .addRequirement(new ItemRequirement(Items.BONE))
            .addRequirement(new ItemRequirement(Items.STRING))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));
        SUMMON_WRAITH = register(new MultiItemSacrifice(Items.CHARCOAL, Registry.TATTERED_CLOTH.get()), new SummonRitual(EidolonEntities.WRAITH.get()).setRegistryName(Eidolon.MODID, "summon_wraith")
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
                .addRequirement(new ItemRequirement(Registry.TATTERED_CLOTH.get()))
                .addRequirement(new ItemRequirement(Registry.TATTERED_CLOTH.get())));

        DECEIT_RITUAL = register(Tags.Items.GEMS_EMERALD, new DeceitRitual().setRegistryName(Eidolon.MODID, "deceit")
            .addRequirement(new ItemRequirement(Tags.Items.GEMS_EMERALD))
            .addRequirement(new ItemRequirement(Items.FERMENTED_SPIDER_EYE))
            .addRequirement(new ItemRequirement(Tags.Items.MUSHROOMS))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        ALLURE_RITUAL = register(Items.ROSE_BUSH, new AllureRitual().setRegistryName(Eidolon.MODID, "allure")
            .addRequirement(new ItemRequirement(Items.GOLDEN_APPLE))
            .addRequirement(new ItemRequirement(Items.RED_DYE))
            .addRequirement(new ItemRequirement(Items.RED_DYE))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        REPELLING_RITUAL = register(Items.NAUTILUS_SHELL, new RepellingRitual().setRegistryName(Eidolon.MODID, "repelling")
            .addRequirement(new ItemRequirement(Tags.Items.INGOTS_IRON))
            .addRequirement(new ItemRequirement(Items.LEATHER))
            .addRequirement(new ItemRequirement(Tags.Items.GEMS_QUARTZ))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        DAYLIGHT_RITUAL = register(Items.SUNFLOWER, new DaylightRitual().setRegistryName(Eidolon.MODID, "daylight")
            .addRequirement(new ItemRequirement(Items.CHARCOAL))
            .addRequirement(new ItemRequirement(Items.WHEAT_SEEDS))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        MOONLIGHT_RITUAL = register(Tags.Items.DYES_BLACK, new MoonlightRitual().setRegistryName(Eidolon.MODID, "moonlight")
            .addRequirement(new ItemRequirement(Items.SNOWBALL))
            .addRequirement(new ItemRequirement(Items.SPIDER_EYE))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        PURIFY_RITUAL = register(Items.GLISTERING_MELON_SLICE, new PurifyRitual().setRegistryName(Eidolon.MODID, "purify")
            .addRequirement(new ItemRequirement(Registry.ENCHANTED_ASH.get()))
            .addRequirement(new ItemRequirement(Registry.ENCHANTED_ASH.get()))
            .addRequirement(new ItemRequirement(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING)))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        SANGUINE_SWORD = register(new MultiItemSacrifice(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HARMING), Items.IRON_SWORD), new SanguineRitual(new ItemStack(Registry.SAPPING_SWORD.get())).setRegistryName(Eidolon.MODID, "sanguine_sapping_sword")
            .addRequirement(new ItemRequirement(Registry.SHADOW_GEM.get()))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Items.IRON_SWORD))
            .addRequirement(new ItemRequirement(Items.NETHER_WART))
            .addRequirement(new ItemRequirement(Items.NETHER_WART))
            .addRequirement(new ItemRequirement(Items.GHAST_TEAR))
            .addRequirement(new HealthRequirement(20)));

        SANGUINE_AMULET = register(new MultiItemSacrifice(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HARMING), Registry.BASIC_AMULET.get()), new SanguineRitual(new ItemStack(Registry.SANGUINE_AMULET.get())).setRegistryName(Eidolon.MODID, "sanguine_sanguine_amulet")
            .addRequirement(new ItemRequirement(Tags.Items.GEMS_DIAMOND))
            .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
            .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
            .addRequirement(new ItemRequirement(Registry.BASIC_AMULET.get()))
            .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
            .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
            .addRequirement(new ItemRequirement(Registry.LESSER_SOUL_GEM.get()))
            .addRequirement(new HealthRequirement(40)));

        RECHARGE_SOULFIRE_RITUAL = register(Registry.LESSER_SOUL_GEM.get(), new RechargingRitual().setRegistryName(Eidolon.MODID, "recharging")
            .addRequirement(new ItemRequirement(Items.BLAZE_POWDER))
            .addRequirement(new ItemRequirement(Items.BLAZE_POWDER))
            .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
            .addInvariant(new FocusItemPresentRequirement(Registry.SOULFIRE_WAND.get())));

        RECHARGE_BONECHILL_RITUAL = register(Registry.LESSER_SOUL_GEM.get(), new RechargingRitual().setRegistryName(Eidolon.MODID, "recharging")
            .addRequirement(new ItemRequirement(Items.SNOWBALL))
            .addRequirement(new ItemRequirement(Items.SNOWBALL))
            .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
            .addInvariant(new FocusItemPresentRequirement(Registry.BONECHILL_WAND.get())));

        ABSORB_RITUAL = register(Registry.DEATH_ESSENCE.get(), new AbsorptionRitual().setRegistryName(Eidolon.MODID, "absorption")
            .addRequirement(new ItemRequirement(Registry.TATTERED_CLOTH.get()))
            .addRequirement(new ItemRequirement(Registry.TATTERED_CLOTH.get()))
            .addRequirement(new ItemRequirement(Items.BONE))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
            .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));
    }
}
