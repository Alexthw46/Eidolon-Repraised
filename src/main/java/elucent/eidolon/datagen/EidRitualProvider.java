package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.*;
import elucent.eidolon.common.ritual.*;
import elucent.eidolon.recipe.*;
import elucent.eidolon.registries.EidolonEntities;
import elucent.eidolon.registries.Registry;
import elucent.eidolon.registries.Worldgen;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static elucent.eidolon.Eidolon.prefix;
import static elucent.eidolon.util.RecipeUtil.ingredientsFromObjects;
import static elucent.eidolon.util.RegistryUtil.getRegistryName;

public class EidRitualProvider extends SimpleDataProvider {


    public EidRitualProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    List<RitualRecipe> rituals = new ArrayList<>();

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addRituals();
        for (RitualRecipe recipe : rituals) {
            Path path = getRecipePath(output, recipe.getId().getPath());
            saveStable(pOutput, recipe.asRecipe(), path);
        }
    }


    protected void addRituals() {

        //SummonRituals
        makeSummon(prefix("summon_zombie"),
                EntityType.ZOMBIE,
                List.of(Registry.SOUL_SHARD.get(), Items.ROTTEN_FLESH),
                List.of(Items.ROTTEN_FLESH));

        makeSummon(prefix("summon_skeleton"),
                EntityType.SKELETON,
                List.of(Registry.SOUL_SHARD.get(), Items.BONE),
                List.of(Items.BONE));

        makeSummon(prefix("summon_phantom"), EntityType.PHANTOM,
                List.of(Registry.SOUL_SHARD.get(), Items.PHANTOM_MEMBRANE),
                List.of(Items.PHANTOM_MEMBRANE));

        makeSummon(prefix("summon_creeper"), EntityType.CREEPER,
                List.of(Registry.SOUL_SHARD.get(), Items.GUNPOWDER),
                List.of(Items.GUNPOWDER));

        makeSummon(prefix("summon_wither_skeleton"),
                EntityType.WITHER_SKELETON,
                List.of(Registry.SOUL_SHARD.get(), Items.BONE),
                List.of(Blocks.SOUL_SAND));

        makeSummon(prefix("summon_husk"),
                EntityType.STRAY,
                Items.CHARCOAL,
                ingredientsFromObjects(List.of(Registry.SOUL_SHARD.get(), Items.ROTTEN_FLESH)),
                List.of(Ingredient.of(Tags.Items.SAND)));

        makeSummon(prefix("summon_drowned"), EntityType.DROWNED,
                Items.CHARCOAL,
                ingredientsFromObjects(List.of(Registry.SOUL_SHARD.get(), Items.ROTTEN_FLESH)),
                List.of(Ingredient.of(Tags.Items.DUSTS_PRISMARINE)));

        makeSummon(prefix("summon_stray"), EntityType.STRAY,
                List.of(Registry.SOUL_SHARD.get(), Items.BONE),
                List.of(Items.STRING));

        makeSummon(prefix("summon_wraith"), EidolonEntities.WRAITH.get(),
                List.of(Registry.SOUL_SHARD.get(), Registry.TATTERED_CLOTH.get()),
                List.of(Registry.TATTERED_CLOTH.get()));

        ItemStack HarmingPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HARMING);
        var harmingIngredient = PartialNBTIngredient.of(HarmingPotion.getItem(), HarmingPotion.getOrCreateTag());

        crafting(Registry.SAPPING_SWORD.get().getDefaultInstance(), Ingredient.of(Items.IRON_SWORD),
                ingredientsFromObjects(List.of(Registry.SHADOW_GEM.get(), Registry.SOUL_SHARD.get(), Registry.SOUL_SHARD.get(),
                        Items.NETHER_WART, Items.NETHER_WART, Items.GHAST_TEAR)),
                List.of(harmingIngredient), 20);

        crafting(Registry.SANGUINE_AMULET.get().getDefaultInstance(), Ingredient.of(Registry.BASIC_AMULET.get()),
                ingredientsFromObjects(List.of(Tags.Items.GEMS_DIAMOND, Tags.Items.DUSTS_REDSTONE, Tags.Items.DUSTS_REDSTONE,
                        Tags.Items.DUSTS_REDSTONE, Tags.Items.DUSTS_REDSTONE,
                        Registry.LESSER_SOUL_GEM.get())),
                List.of(harmingIngredient), 40);

        generic(Items.BONE_MEAL, new CrystalRitual().setRegistryName(Eidolon.MODID, "crystal")
                .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
                .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE)));

        generic(Tags.Items.GEMS_EMERALD, new DeceitRitual().setRegistryName(Eidolon.MODID, "deceit")
                .addRequirement(new ItemRequirement(Tags.Items.GEMS_EMERALD))
                .addRequirement(new ItemRequirement(Items.FERMENTED_SPIDER_EYE))
                .addRequirement(new ItemRequirement(Tags.Items.MUSHROOMS))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        generic(Items.ROSE_BUSH, new AllureRitual().setRegistryName(Eidolon.MODID, "allure")
                .addRequirement(new ItemRequirement(Items.GOLDEN_APPLE))
                .addRequirement(new ItemRequirement(Items.RED_DYE))
                .addRequirement(new ItemRequirement(Items.RED_DYE))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        generic(Items.NAUTILUS_SHELL, new RepellingRitual().setRegistryName(Eidolon.MODID, "repelling")
                .addRequirement(new ItemRequirement(Tags.Items.INGOTS_IRON))
                .addRequirement(new ItemRequirement(Items.LEATHER))
                .addRequirement(new ItemRequirement(Tags.Items.GEMS_QUARTZ))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        generic(Items.SUNFLOWER, new DaylightRitual().setRegistryName(Eidolon.MODID, "daylight")
                .addRequirement(new ItemRequirement(Items.CHARCOAL))
                .addRequirement(new ItemRequirement(Items.WHEAT_SEEDS))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        generic(Tags.Items.DYES_BLACK, new MoonlightRitual().setRegistryName(Eidolon.MODID, "moonlight")
                .addRequirement(new ItemRequirement(Items.SNOWBALL))
                .addRequirement(new ItemRequirement(Items.SPIDER_EYE))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        generic(Items.GLISTERING_MELON_SLICE, new PurifyRitual().setRegistryName(Eidolon.MODID, "purify")
                .addRequirement(new ItemRequirement(Registry.ENCHANTED_ASH.get()))
                .addRequirement(new ItemRequirement(Registry.ENCHANTED_ASH.get()))
                .addRequirement(new ItemRequirement(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING)))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get())));

        generic(Registry.DEATH_ESSENCE.get(), new AbsorptionRitual().setRegistryName(Eidolon.MODID, "absorption")
                .addRequirement(new ItemRequirement(Registry.TATTERED_CLOTH.get()))
                .addRequirement(new ItemRequirement(Registry.TATTERED_CLOTH.get()))
                .addRequirement(new ItemRequirement(Items.BONE))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
                .addRequirement(new ItemRequirement(Registry.SOUL_SHARD.get()))
                .addInvariant(new FocusItemPresentRequirement(Registry.SUMMONING_STAFF.get())));

        generic(Registry.LESSER_SOUL_GEM.get(), new RechargingRitual().setRegistryName(Eidolon.MODID, "recharging_soulfire")
                .addRequirement(new ItemRequirement(Items.BLAZE_POWDER))
                .addRequirement(new ItemRequirement(Items.BLAZE_POWDER))
                .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
                .addInvariant(new FocusItemPresentRequirement(Registry.SOULFIRE_WAND.get())));

        generic(Registry.LESSER_SOUL_GEM.get(), new RechargingRitual().setRegistryName(Eidolon.MODID, "recharging_chill")
                .addRequirement(new ItemRequirement(Items.SNOWBALL))
                .addRequirement(new ItemRequirement(Items.SNOWBALL))
                .addRequirement(new ItemRequirement(Tags.Items.DUSTS_REDSTONE))
                .addInvariant(new FocusItemPresentRequirement(Registry.BONECHILL_WAND.get()))
        );

        location(Worldgen.CATACOMBS, prefix("ritual_catacomb_locator"), Ingredient.of(Items.MAP), ingredientsFromObjects(List.of(Items.COMPASS, Registry.MAGIC_INK.get(), Registry.RAVEN_FEATHER.get())), List.of(), 0);

        // tester for command ritual
        // rituals.add(new CommandRitualRecipe(prefix("ritual_command"), "/kill @e", Ingredient.of(Items.COMMAND_BLOCK), List.of(), List.of(), 0));
    }

    public void crafting(ItemStack result, Ingredient reagent, List<Ingredient> pedestal, List<Ingredient> foci, int healthCost) {
        rituals.add(new ItemRitualRecipe(getRegistryName(result.getItem()), pedestal, foci, reagent, result, true, healthCost));
    }

    public void location(TagKey<Structure> structureTagKey, ResourceLocation location, Ingredient reagent, List<Ingredient> pedestal, List<Ingredient> foci, int healthCost) {
        rituals.add(new LocationRitualRecipe(location, structureTagKey.location(), reagent, pedestal, foci, healthCost));
    }


    public void makeSummon(ResourceLocation id, EntityType<?> type, ItemLike item, List<Ingredient> pedestal, List<Ingredient> foci) {
        rituals.add(new SummonRitualRecipe(id, getRegistryName(type), Ingredient.of(item), pedestal, foci));
    }

    public void makeSummon(ResourceLocation id, EntityType<?> type, List<ItemLike> pedestal, List<ItemLike> foci) {
        makeSummon(id, type, Items.CHARCOAL, pedestal.stream().map(Ingredient::of).toList(), foci.stream().map(Ingredient::of).toList());
    }

    public void generic(ItemLike item, Ritual ritual) {
        generic(new ItemSacrifice(item), ritual);
    }

    public void generic(TagKey<Item> item, Ritual ritual) {
        generic(new ItemSacrifice(item), ritual);
    }

    public void generic(ItemSacrifice keys, Ritual ritual) {
        List<Ingredient> pedestal = ritual.getRequirements().stream().filter(req -> req instanceof ItemRequirement).map(req -> (ItemRequirement) req).map(ItemRequirement::getMatch).toList();
        Ingredient reagent = keys.main;
        List<Ingredient> foci = keys instanceof MultiItemSacrifice mis ? mis.items : List.of();
        List<Ingredient> invariants = ritual.getInvariants().stream().filter(iRequirement -> iRequirement instanceof FocusItemPresentRequirement).map(iRequirement -> (FocusItemPresentRequirement) iRequirement).map(FocusItemPresentRequirement::getMatch).collect(Collectors.toList());
        float health = ritual.getRequirements().stream().filter(req -> req instanceof HealthRequirement).map(req -> (HealthRequirement) req).map(HealthRequirement::getHealth).findFirst().orElse(0f);
        rituals.add(new GenericRitualRecipe(prefix("ritual_" + ritual.getRegistryName().getPath()), ritual.getRegistryName(), reagent, pedestal, foci, invariants, health));
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public @NotNull String getName() {
        return "Eidolon Rituals";
    }

    protected static Path getRecipePath(Path pathIn, Item item) {
        return getRecipePath(pathIn, getRegistryName(item).getPath());
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/eidolon/recipes/rituals/" + str + ".json");
    }
}
