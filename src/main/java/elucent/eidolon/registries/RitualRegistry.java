package elucent.eidolon.registries;

import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.ritual.*;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import static elucent.eidolon.Eidolon.prefix;

public class RitualRegistry {
    static final Map<ResourceLocation, Ritual> rituals = new HashMap<>();

    public static Ritual register(ResourceLocation name, Ritual ritual) {
        assert name != null;
        ritual.setRegistryName(name);
        rituals.put(name, ritual);
        return ritual;
    }


    /*
    public static Page getDefaultPage(Ritual ritual, ItemSacrifice sacrifice) {
        List<RitualPage.RitualIngredient> inputs = new ArrayList<>();
        List<ItemStack> foci = new ArrayList<>();
        if (sacrifice instanceof MultiItemSacrifice) for (Ingredient o : ((MultiItemSacrifice) sacrifice).items) {
            foci.add(RecipeUtil.stackFromObject(o));
        }
        int slot = 0;
        for (IRequirement r : ritual.getRequirements()) {
            if (r instanceof ItemRequirement ir)
                inputs.add(new RitualPage.RitualIngredient(ir.getMatch(), false));
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
     */


    public static Ritual find(ResourceLocation name) {
        return rituals.get(name);
    }

    public static Ritual CRYSTAL_RITUAL,
            SUMMON_ZOMBIE, SUMMON_SKELETON, SUMMON_PHANTOM, SUMMON_HUSK, SUMMON_DROWNED,
            SUMMON_STRAY, SUMMON_WITHER_SKELETON, SUMMON_WRAITH,
            ALLURE_RITUAL, REPELLING_RITUAL, DECEIT_RITUAL, DAYLIGHT_RITUAL, MOONLIGHT_RITUAL,
            PURIFY_RITUAL, RECHARGE_SOULFIRE_RITUAL, RECHARGE_BONECHILL_RITUAL,
            SANGUINE_SWORD, SANGUINE_AMULET,
            ABSORB_RITUAL;

    public static ResourceLocation CRYSTAL_RITUAL_RL = prefix("crystal");
    public static ResourceLocation ALLURE_RITUAL_RL = prefix("allure");
    public static ResourceLocation REPELLING_RITUAL_RL = prefix("repelling");
    public static ResourceLocation DECEIT_RITUAL_RL = prefix("deceit");
    public static ResourceLocation DAYLIGHT_RITUAL_RL = prefix("daylight");
    public static ResourceLocation MOONLIGHT_RITUAL_RL = prefix("moonlight");
    public static ResourceLocation PURIFY_RITUAL_RL = prefix("purify");
    public static ResourceLocation RECHARGE_SOULFIRE_RITUAL_RL = prefix("recharging_soulfire");
    public static ResourceLocation RECHARGE_BONECHILL_RITUAL_RL = prefix("recharging_chill");
    public static ResourceLocation ABSORB_RITUAL_RL = prefix("absorption");


    public static void init() {

        /*
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

        //crafting

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

        */

        //hardcoded

        CRYSTAL_RITUAL = register(CRYSTAL_RITUAL_RL, new CrystalRitual());

        DECEIT_RITUAL = register(DECEIT_RITUAL_RL, new DeceitRitual());

        ALLURE_RITUAL = register(ALLURE_RITUAL_RL, new AllureRitual());

        REPELLING_RITUAL = register(REPELLING_RITUAL_RL, new RepellingRitual());

        DAYLIGHT_RITUAL = register(DAYLIGHT_RITUAL_RL, new DaylightRitual());

        MOONLIGHT_RITUAL = register(MOONLIGHT_RITUAL_RL, new MoonlightRitual());

        PURIFY_RITUAL = register(PURIFY_RITUAL_RL, new PurifyRitual());

        RECHARGE_SOULFIRE_RITUAL = register(RECHARGE_SOULFIRE_RITUAL_RL, new RechargingRitual());

        RECHARGE_BONECHILL_RITUAL = register(RECHARGE_BONECHILL_RITUAL_RL, new RechargingRitual());

        ABSORB_RITUAL = register(ABSORB_RITUAL_RL, new AbsorptionRitual());
    }

}
