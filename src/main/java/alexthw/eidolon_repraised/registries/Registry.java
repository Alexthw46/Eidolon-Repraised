package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.capability.KnowledgeCommand;
import alexthw.eidolon_repraised.capability.ReputationCommand;
import alexthw.eidolon_repraised.client.particle.*;
import alexthw.eidolon_repraised.common.block.*;
import alexthw.eidolon_repraised.common.block.CandleBlock;
import alexthw.eidolon_repraised.common.item.*;
import alexthw.eidolon_repraised.common.item.Tiers;
import alexthw.eidolon_repraised.common.item.Tiers.SilverTier;
import alexthw.eidolon_repraised.common.item.curio.*;
import alexthw.eidolon_repraised.common.tile.*;
import alexthw.eidolon_repraised.gui.*;
import alexthw.eidolon_repraised.util.DamageTypeData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

import static net.minecraft.tags.EntityTypeTags.UNDEAD;
import static net.minecraft.world.level.block.state.properties.WoodType.register;

@SuppressWarnings({"unused", "DataFlowIssue"})
public class Registry {

    public static final TagKey<Item> ILLWOOD_LOGS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "illwood_logs"));
    public static TagKey<Item>
            INGOTS_LEAD = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/lead"));
    public static TagKey<Item> INGOTS_PEWTER = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/pewter"));
    public static TagKey<Item> INGOTS_ARCANE_GOLD = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/arcane_gold"));
    public static final TagKey<Item> INGOTS_SILVER = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/silver"));
    public static TagKey<Item> GEMS_SHADOW = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "gems/shadow_gem"));
    public static final TagKey<Item> ZOMBIE_FOOD_TAG = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "zombie_food"));

    public static TagKey<Block> CRUCIBLE_HOT_BLOCKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "crucible_hot_blocks"));
    public static TagKey<Block> PLANTER_PLANTS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "planter_plants"));

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Eidolon.MODID);

    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Eidolon.MODID);
    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Eidolon.MODID);
    static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Eidolon.MODID);
    static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, Eidolon.MODID);

    static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARG_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Eidolon.MODID);

    static Item.Properties itemProps() {
        return new Item.Properties();
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("general", () -> CreativeModeTab.builder().icon(Registry.SHADOW_GEM.get()::getDefaultInstance).title(Component.translatable("itemGroup.eidolon"))
            .displayItems((params, output) -> {
                for (var entry : ITEMS.getEntries()) {
                    output.accept(entry.get().getDefaultInstance());
                }
            }).build());

    static BlockBehaviour.Properties blockProps(Block mat, DyeColor color) {
        return BlockBehaviour.Properties.ofFullCopy(mat).mapColor(color);
    }

    static BlockBehaviour.Properties blockProps(Block mat) {
        return BlockBehaviour.Properties.ofFullCopy(mat);
    }

    private static Boolean allowsSpawnOnLeaves(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }

    static DeferredHolder<Item, Item> addItem(String name) {
        return addItem(name, itemProps());
    }

    static DeferredHolder<Item, Item> addItem(String name, Item.Properties props) {
        return addItem(name, () -> new Item(props));
    }

    static DeferredHolder<Item, Item> addItem(String name, String lore) {
        return addItem(name, () -> new ItemBase(itemProps()).setLore(lore));
    }

    static DeferredHolder<Item, Item> addItem(String name, Supplier<Item> item) {
        return ITEMS.register(name, item);
    }

    static DeferredHolder<Block, Block> addBlock(String name, Block.Properties props) {
        Supplier<Block> b = () -> new Block(props);
        var block = BLOCKS.register(name, b);
        ITEMS.register(name, () -> new BlockItem(block.get(), itemProps()));
        return block;
    }

    static <T extends Block> DeferredHolder<Block, T> addBlock(String name, Supplier<T> b) {
        var block = BLOCKS.register(name, b);
        ITEMS.register(name, () -> new BlockItem(block.get(), itemProps()));
        return block;
    }

    static <T extends Block> DeferredHolder<Block, T> addBlock(String name, Supplier<T> b, String lore) {
        var block = BLOCKS.register(name, b);
        ITEMS.register(name, () -> new LoreBlockItem(block.get(), itemProps(), lore));
        return block;
    }

    public static final WoodType ILLWOOD = register(new WoodType("eidolon_repraised:illwood", BlockSetType.DARK_OAK));
    public static final WoodType POLISHED = register(new WoodType("eidolon_repraised:polished", BlockSetType.DARK_OAK));

    static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> addContainer(String name, MenuType.MenuSupplier<T> factory) {
        return CONTAINERS.register(name, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
    }

    public static final DeferredHolder<Item, Item>
            LEAD_INGOT = addItem("lead_ingot");
    public static final DeferredHolder<Item, Item> RAW_LEAD = addItem("raw_lead");
    public static final DeferredHolder<Item, Item> LEAD_NUGGET = addItem("lead_nugget");
    public static final DeferredHolder<Item, Item> SILVER_INGOT = addItem("silver_ingot");
    public static final DeferredHolder<Item, Item> RAW_SILVER = addItem("raw_silver");
    public static final DeferredHolder<Item, Item> SILVER_NUGGET = addItem("silver_nugget");
    public static final DeferredHolder<Item, Item> PEWTER_BLEND = addItem("pewter_blend");
    public static final DeferredHolder<Item, Item> PEWTER_INGOT = addItem("pewter_ingot");
    public static final DeferredHolder<Item, Item> PEWTER_NUGGET = addItem("pewter_nugget");
    public static final DeferredHolder<Item, Item> PEWTER_INLAY = addItem("pewter_inlay");
    public static final DeferredHolder<Item, Item> ARCANE_GOLD_INGOT = addItem("arcane_gold_ingot");
    public static final DeferredHolder<Item, Item> ARCANE_GOLD_NUGGET = addItem("arcane_gold_nugget");
    public static final DeferredHolder<Item, Item> ELDER_BRICK = addItem("elder_brick");
    public static final DeferredHolder<Item, Item> OFFERING_INCENSE = addItem("offering_incense", "lore.eidolon_repraised.offering_incense");
    public static final DeferredHolder<Item, Item> RESTORATION_INCENSE = addItem("restoration_incense", "lore.eidolon_repraised.restoration_incense");
    public static final DeferredHolder<Item, Item> GLOOM_INCENSE = addItem("gloom_incense", "lore.eidolon_repraised.gloom_incense");
    public static final DeferredHolder<Item, Item> DEATH_BANE_INCENSE = addItem("deathbane_incense", "lore.eidolon_repraised.deathbane_incense");
    public static final DeferredHolder<Item, Item> TOUGH_INCENSE = addItem("tough_incense", "lore.eidolon_repraised.tough_incense");
    public static final DeferredHolder<Item, Item> FRAIL_INCENSE = addItem("frail_incense", "lore.eidolon_repraised.frail_incense");
    public static final DeferredHolder<Item, Item> FROSTBIND_INCENSE = addItem("frostbind_incense", "lore.eidolon_repraised.frostbind_incense");
    public static final DeferredHolder<Item, Item> TETHER_INCENSE = addItem("tether_incense", "lore.eidolon_repraised.tether_incense");
    public static final DeferredHolder<Item, Item> PURITY_INCENSE = addItem("purity_incense", "lore.eidolon_repraised.purity_incense");
    public static final DeferredHolder<Item, Item> QUICKEN_INCENSE = addItem("quicken_incense", "lore.eidolon_repraised.quicken_incense");
    public static final DeferredHolder<Item, Item> BLOODLUST_INCENSE = addItem("bloodlust_incense", "lore.eidolon_repraised.bloodlust_incense");
    public static final DeferredHolder<Item, Item> SOUL_HARVEST_INCENSE = addItem("soul_harvest_incense", "lore.eidolon_repraised.soul_harvest_incense");
    public static final DeferredHolder<Item, Item> WARDING_INCENSE = addItem("warding_incense", "lore.eidolon_repraised.warding_incense");
    public static final DeferredHolder<Item, Item> UNDEATH_INCENSE = addItem("undeath_incense", "lore.eidolon_repraised.undeath_incense");
    public static final DeferredHolder<Item, Item> SULFUR = addItem("sulfur");
    public static final DeferredHolder<Item, Item> GOLD_INLAY = addItem("gold_inlay");
    public static final DeferredHolder<Item, Item> ZOMBIE_HEART = addItem("zombie_heart", () -> new ItemBase(itemProps().rarity(Rarity.UNCOMMON).food(
            new FoodProperties.Builder()
                    .nutrition(2).saturationModifier(1.5f)
                    .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 1800), 0.875f)
                    .effect(() -> new MobEffectInstance(MobEffects.POISON, 900, 1), 1.0f)
                    .build())).setLore("lore.eidolon_repraised.zombie_heart"));
    public static final DeferredHolder<Item, Item> TATTERED_CLOTH = addItem("tattered_cloth");
    public static final DeferredHolder<Item, Item> WRAITH_HEART = addItem("wraith_heart", () -> new ItemBase(itemProps()
            .rarity(Rarity.UNCOMMON)).setLore("lore.eidolon_repraised.wraith_heart"));
    public static final DeferredHolder<Item, Item> TOP_HAT = addItem("top_hat", () -> new TopHatItem(itemProps().stacksTo(1).rarity(Rarity.EPIC)).setLore("lore.eidolon_repraised.top_hat"));
    public static final DeferredHolder<Item, Item> BASIC_RING = addItem("basic_ring", () -> new BasicRingItem(itemProps().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BASIC_AMULET = addItem("basic_amulet", () -> new BasicAmuletItem(itemProps().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BASIC_BELT = addItem("basic_belt", () -> new BasicBeltItem(itemProps().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CODEX = addItem("codex", () -> new CodexItem(itemProps().stacksTo(1).rarity(Rarity.UNCOMMON)).setLore("lore.eidolon_repraised.codex"));
    public static final DeferredHolder<Item, Item> CHANT_SCROLL = addItem("chant_scroll", () -> new ChantScrollItem(itemProps().stacksTo(64).rarity(Rarity.UNCOMMON)).setLore("lore.eidolon_repraised.chant_scroll"));
    public static final DeferredHolder<Item, Item> SOUL_SHARD = addItem("soul_shard", "lore.eidolon_repraised.soul_shard");
    public static final DeferredHolder<Item, Item> DEATH_ESSENCE = addItem("death_essence");
    public static final DeferredHolder<Item, Item> CRIMSON_ESSENCE = addItem("crimson_essence");
    public static final DeferredHolder<Item, Item> CRIMSON_GEM = addItem("crimson_gem");

    public static final DeferredHolder<Item, Item> FUNGUS_SPROUTS = addItem("fungus_sprouts", itemProps().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build()));
    public static final DeferredHolder<Item, Item> WARPED_SPROUTS = addItem("warped_sprouts", itemProps().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.6f).effect(() -> new MobEffectInstance(EidolonPotions.ANCHORED_EFFECT, 900), 1).build()));
    public static final DeferredHolder<Item, Item> ENDER_CALX = addItem("ender_calx");
    public static final DeferredHolder<Item, Item> TALLOW = addItem("tallow");
    public static final DeferredHolder<Item, Item> LESSER_SOUL_GEM = addItem("lesser_soul_gem");
    public static final DeferredHolder<Item, Item> UNHOLY_SYMBOL = addItem("unholy_symbol", () -> new TheurgySymbolItem(itemProps().rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.unholy_symbol"));
    public static final DeferredHolder<Item, Item> HOLY_SYMBOL = addItem("holy_symbol", () -> new TheurgySymbolItem(itemProps().rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.holy_symbol"));

    public static final DeferredHolder<Item, Item> REAPER_SCYTHE = addItem("reaper_scythe", () -> new ReaperScytheItem(itemProps().rarity(Rarity.UNCOMMON))
            .setLore("lore.eidolon_repraised.reaper_scythe"));
    public static final DeferredHolder<Item, Item> CLEAVING_AXE = addItem("cleaving_axe", () -> new CleavingAxeItem(itemProps().rarity(Rarity.UNCOMMON))
            .setLore("lore.eidolon_repraised.cleaving_axe"));
    public static final DeferredHolder<Item, Item> SHADOW_GEM = addItem("shadow_gem");
    public static final DeferredHolder<Item, Item> WICKED_WEAVE = addItem("wicked_weave");
    public static final DeferredHolder<Item, Item> WARLOCK_HAT = addItem("warlock_hat", () -> new WarlockRobesItem(Type.HELMET, itemProps().attributes(ItemAttributeModifiers.builder().add(EidolonAttributes.MAGIC_POWER, new AttributeModifier(Eidolon.prefix("warlock_hat"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.HEAD).build())));
    public static final DeferredHolder<Item, Item> WARLOCK_CLOAK = addItem("warlock_cloak", () -> new WarlockRobesItem(Type.CHESTPLATE, itemProps()));
    public static final DeferredHolder<Item, Item> WARLOCK_BOOTS = addItem("warlock_boots", () -> new WarlockRobesItem(Type.BOOTS, itemProps()));
    public static final DeferredHolder<Item, Item> SILVER_HELMET = addItem("silver_helmet", () -> new SilverArmorItem(Type.HELMET, itemProps()));
    public static final DeferredHolder<Item, Item> SILVER_CHESTPLATE = addItem("silver_chestplate", () -> new SilverArmorItem(Type.CHESTPLATE, itemProps()));
    public static final DeferredHolder<Item, Item> SILVER_LEGGINGS = addItem("silver_leggings", () -> new SilverArmorItem(Type.LEGGINGS, itemProps()));
    public static final DeferredHolder<Item, Item> SILVER_BOOTS = addItem("silver_boots", () -> new SilverArmorItem(Type.BOOTS, itemProps()));
    public static final DeferredHolder<Item, Item> SILVER_SWORD = addItem("silver_sword", () -> new SwordItem(SilverTier.INSTANCE, itemProps().attributes(SwordItem.createAttributes(SilverTier.INSTANCE, 3, -2.4f))) {

                @Override
                public boolean hurtEnemy(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
                    if (pTarget.getType().is(UNDEAD)) {
                        pTarget.setRemainingFireTicks(5 * 20);
                    }
                    return super.hurtEnemy(pStack, pTarget, pAttacker);
                }
            }
    );
    public static final DeferredHolder<Item, Item> SILVER_PICKAXE = addItem("silver_pickaxe", () -> new PickaxeItem(SilverTier.INSTANCE, itemProps().attributes(PickaxeItem.createAttributes(SilverTier.INSTANCE, 1, -2.4f))));
    public static final DeferredHolder<Item, Item> SILVER_AXE = addItem("silver_axe", () -> new AxeItem(SilverTier.INSTANCE, itemProps().attributes(AxeItem.createAttributes(SilverTier.INSTANCE, 6, -2.4f))));
    public static final DeferredHolder<Item, Item> SILVER_SHOVEL = addItem("silver_shovel", () -> new ShovelItem(SilverTier.INSTANCE, itemProps().attributes(ShovelItem.createAttributes(SilverTier.INSTANCE, 1.5f, -2.4f))));
    public static final DeferredHolder<Item, Item> SILVER_HOE = addItem("silver_hoe", () -> new HoeItem(SilverTier.INSTANCE, itemProps().attributes(HoeItem.createAttributes(SilverTier.INSTANCE, 0, -2.4f))));
    public static final DeferredHolder<Item, Item> ATHAME = addItem("athame", () -> new AthameItem(itemProps().stacksTo(1).attributes(SwordItem.createAttributes(Tiers.PewterTier.INSTANCE, 1, -1.6f))));
    public static final DeferredHolder<Item, Item> REVERSAL_PICK = addItem("reversal_pick", () -> new ReversalPickItem(itemProps()
            .rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item, Item> VOID_AMULET = addItem("void_amulet", () -> new VoidAmuletItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.void_amulet"));
    public static final DeferredHolder<Item, Item> WARDED_MAIL = addItem("warded_mail", () -> new WardedMailItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.warded_mail"));
    public static final DeferredHolder<Item, Item> SAPPING_SWORD = addItem("sapping_sword", () -> new SappingSwordItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.sapping_sword"));
    public static final DeferredHolder<Item, Item> SANGUINE_AMULET = addItem("sanguine_amulet", () -> new SanguineAmuletItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.sanguine_amulet"));
    public static final DeferredHolder<Item, Item> ENERVATING_RING = addItem("enervating_ring", () -> new EnervatingRingItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.enervating_ring"));
    public static final DeferredHolder<Item, Item> SOULFIRE_WAND = addItem("soulfire_wand", () -> new SoulfireWandItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1).durability(253).setNoRepair())
            .setLore("lore.eidolon_repraised.soulfire_wand"));
    public static final DeferredHolder<Item, Item> BONECHILL_WAND = addItem("bonechill_wand", () -> new BonechillWandItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1).durability(253).setNoRepair())
            .setLore("lore.eidolon_repraised.bonechill_wand"));
    public static final DeferredHolder<Item, Item> GRAVITY_BELT = addItem("gravity_belt", () -> new GravityBeltItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.gravity_belt"));
    public static final DeferredHolder<Item, Item> RESOLUTE_BELT = addItem("resolute_belt", () -> new ResoluteBeltItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.resolute_belt"));
    public static final DeferredHolder<Item, Item> PRESTIGIOUS_PALM = addItem("prestigious_palm", () -> new PrestigiousPalmItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.prestigious_palm"));
    public static final DeferredHolder<Item, Item> MIND_SHIELDING_PLATE = addItem("mind_shielding_plate", () -> new MindShieldingPlateItem(itemProps()
            .rarity(Rarity.UNCOMMON).stacksTo(1)).setLore("lore.eidolon_repraised.mind_shielding_plate"));
    public static final DeferredHolder<Item, Item> GLASS_HAND = addItem("glass_hand", () -> new GlassHandItem(itemProps()
            .rarity(Rarity.RARE).stacksTo(1)).setLore("lore.eidolon_repraised.glass_hand"));
    public static final DeferredHolder<Item, Item> TERMINUS_MIRROR = addItem("terminus_mirror", () -> new TerminusMirrorItem(itemProps()
            .rarity(Rarity.RARE).stacksTo(1)).setLore("lore.eidolon_repraised.terminus_mirror"));
    public static final DeferredHolder<Item, Item> ANGELS_SIGHT = addItem("angels_sight", () -> new AngelSightItem(itemProps()
            .rarity(Rarity.RARE).stacksTo(1)).setLore("lore.eidolon_repraised.angels_sight"));
    public static final DeferredHolder<Item, Item> WITHERED_HEART = addItem("withered_heart", () -> new ItemBase(itemProps().rarity(Rarity.RARE).food(
            new FoodProperties.Builder()
                    .nutrition(2).saturationModifier(1.5f)
                    .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 1800), 0.875f)
                    .effect(() -> new MobEffectInstance(MobEffects.WITHER, 900, 1), 1.0f)
                    .build())).setLore("lore.eidolon_repraised.withered_heart"));
    public static final DeferredHolder<Item, Item> IMBUED_BONES = addItem("imbued_bones", () -> new ItemBase(itemProps().rarity(Rarity.UNCOMMON)).setLore("lore.eidolon_repraised.imbued_bones"));
    public static final DeferredHolder<Item, Item> SUMMONING_STAFF = addItem("summoning_staff", () -> new SummoningStaffItem(itemProps().rarity(Rarity.RARE).stacksTo(1)));
    public static final DeferredHolder<Item, Item> DEATHBRINGER_SCYTHE = addItem("deathbringer_scythe", () -> new DeathbringerScytheItem(itemProps().rarity(Rarity.RARE).stacksTo(1))
            .setLore("lore.eidolon_repraised.deathbringer_scythe"));
    public static final DeferredHolder<Item, Item> SOULBONE_AMULET = addItem("soulbone_amulet", () -> new SoulboneAmuletItem(itemProps()
            .rarity(Rarity.RARE).stacksTo(1)).setLore("lore.eidolon_repraised.soulbone_amulet"));
    public static final DeferredHolder<Item, Item> BONELORD_HELM = addItem("bonelord_helm", () -> new BonelordArmorItem(Type.HELMET, itemProps().rarity(Rarity.RARE).attributes(ItemAttributeModifiers.builder().add(EidolonAttributes.PERSISTENT_SOUL_HEARTS, new AttributeModifier(Eidolon.prefix("bonelord_helm"), 10.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD).build())));
    public static final DeferredHolder<Item, Item> BONELORD_CHESTPLATE = addItem("bonelord_chestplate", () -> new BonelordArmorItem(Type.CHESTPLATE, itemProps().rarity(Rarity.RARE).attributes(ItemAttributeModifiers.builder().add(EidolonAttributes.PERSISTENT_SOUL_HEARTS, new AttributeModifier(Eidolon.prefix("bonelord_chest"), 20.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST).build())));
    public static final DeferredHolder<Item, Item> BONELORD_GREAVES = addItem("bonelord_greaves", () -> new BonelordArmorItem(Type.LEGGINGS, itemProps().rarity(Rarity.RARE).attributes(ItemAttributeModifiers.builder().add(EidolonAttributes.PERSISTENT_SOUL_HEARTS, new AttributeModifier(Eidolon.prefix("bonelord_legs"), 20.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS).build())));
    //TODO: Datagen Disc data
    static ResourceKey<JukeboxSong> PAROUSIA = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "parousia"));
    public static final DeferredHolder<Item, Item> PAROUSIA_DISC = addItem("music_disc_parousia", () -> new Item(itemProps().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(PAROUSIA))); // 3680
    public static final DeferredHolder<Item, Item> RAVEN_FEATHER = addItem("raven_feather");
    public static final DeferredHolder<Item, Item> RAVEN_CLOAK = addItem("raven_cloak", () -> new RavenCloakItem(itemProps().rarity(Rarity.RARE).stacksTo(1)).setLore("lore.eidolon_repraised.raven_cloak"));
    public static final DeferredHolder<Item, Item> MERAMMER_RESIN = addItem("merammer_resin");
    public static final DeferredHolder<Item, Item> MAGIC_INK = addItem("magic_ink");
    public static final DeferredHolder<Item, Item> MAGICIANS_WAX = addItem("magicians_wax");
    public static final DeferredHolder<Item, Item> ARCANE_SEAL = addItem("arcane_seal");
    public static final DeferredHolder<Item, Item> PARCHMENT = addItem("parchment");
    public static final DeferredHolder<Item, Item> NOTETAKING_TOOLS = addItem("notetaking_tools", () -> new NotetakingToolsItem(itemProps().stacksTo(16)));
    public static final DeferredHolder<Item, Item> RESEARCH_NOTES = addItem("research_notes", () -> new ResearchNotesItem(itemProps().rarity(Rarity.UNCOMMON).stacksTo(1)));
    public static final DeferredHolder<Item, Item> COMPLETED_RESEARCH = addItem("completed_research", () -> new CompletedResearchItem(itemProps().rarity(Rarity.UNCOMMON).stacksTo(1)));
    public static final DeferredHolder<Item, Item> RED_CANDY = addItem("red_candy", () -> new ItemBase(itemProps().rarity(Rarity.COMMON).food(
            new FoodProperties.Builder()
                    .nutrition(2).saturationModifier(2)
                    .effect(() -> new MobEffectInstance(EidolonPotions.SOUL_HARVEST, 20 * 60, 0), 0.5f)
                    .build())).setLore(ChatFormatting.RED, "lore.eidolon_repraised.red_candy"));
    public static final DeferredHolder<Item, Item> GRAPE_CANDY = addItem("grape_candy", () -> new ItemBase(itemProps().rarity(Rarity.COMMON).food(
            new FoodProperties.Builder()
                    .nutrition(2).saturationModifier(2)
                    .effect(() -> new MobEffectInstance(EidolonPotions.SOUL_HARVEST, 20 * 60, 0), 0.5f)
                    .build())).setLore(ChatFormatting.LIGHT_PURPLE, "lore.eidolon_repraised.grape_candy"));

    public static final DeferredHolder<Block, Block>
            LEAD_ORE = addBlock("lead_ore", blockProps(Blocks.STONE)
            .sound(SoundType.STONE).strength(2.8f, 3.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> DEEP_LEAD_ORE = addBlock("deep_lead_ore", blockProps(Blocks.STONE)
            .sound(SoundType.DEEPSLATE).strength(3.2f, 3.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> LEAD_BLOCK = addBlock("lead_block", blockProps(Blocks.STONE, DyeColor.PURPLE)
            .sound(SoundType.METAL).strength(3.0f, 3.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> RAW_LEAD_BLOCK = addBlock("raw_lead_block", blockProps(Blocks.STONE, DyeColor.PURPLE)
            .sound(SoundType.DEEPSLATE).strength(2.4f, 3.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> SILVER_ORE = addBlock("silver_ore", blockProps(Blocks.STONE)
            .sound(SoundType.STONE).strength(3.2f, 3.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> DEEP_SILVER_ORE = addBlock("deep_silver_ore", blockProps(Blocks.STONE)
            .sound(SoundType.DEEPSLATE).strength(3.6f, 3.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> SILVER_BLOCK = addBlock("silver_block", blockProps(Blocks.STONE, DyeColor.LIGHT_BLUE)
            .sound(SoundType.METAL).strength(3.0f, 3.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> RAW_SILVER_BLOCK = addBlock("raw_silver_block", blockProps(Blocks.STONE, DyeColor.LIGHT_BLUE)
            .sound(SoundType.STONE).strength(2.4f, 3.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> PEWTER_BLOCK = addBlock("pewter_block", blockProps(Blocks.STONE, DyeColor.LIGHT_GRAY)
            .sound(SoundType.METAL).strength(4.0f, 4.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> ARCANE_GOLD_BLOCK = addBlock("arcane_gold_block", blockProps(Blocks.STONE, DyeColor.YELLOW)
            .sound(SoundType.METAL).strength(3.0f, 4.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> SHADOW_GEM_BLOCK = addBlock("shadow_gem_block", blockProps(Blocks.STONE, DyeColor.PURPLE)
            .sound(SoundType.METAL).strength(3.0f, 4.0f).requiresCorrectToolForDrops());
    public static final DeferredHolder<Block, Block> WOODEN_ALTAR = addBlock("wooden_altar", () -> new TableBlockBase(blockProps(Blocks.OAK_WOOD)
            .sound(SoundType.WOOD).strength(1.6f, 3.0f)));
    public static final DeferredHolder<Block, Block> STONE_ALTAR = addBlock("stone_altar", () -> new TableBlockBase(blockProps(Blocks.STONE)
            .sound(SoundType.STONE).strength(2.8f, 3.0f)
            .requiresCorrectToolForDrops().noOcclusion())
            .setMainShape(Shapes.or(
                    Shapes.box(0, 0.375, 0, 1, 1, 1),
                    Shapes.box(0.0625, 0.125, 0.0625, 0.9375, 0.375, 0.9375)
            )));
    public static final DeferredHolder<Block, Block> CANDLE = addBlock("candle", () -> new CandleBlock(blockProps(Blocks.CANDLE, DyeColor.WHITE)
            .sound(SoundType.STONE).lightLevel(state -> 15).strength(0.6f, 0.8f).noOcclusion()));
    public static final DeferredHolder<Block, Block> CANDLESTICK = addBlock("candlestick", () -> new CandlestickBlock(blockProps(Blocks.CANDLE, DyeColor.YELLOW)
            .sound(SoundType.STONE).lightLevel(state -> 15).strength(1.2f, 2.0f).noOcclusion()));
    public static final DeferredHolder<Block, Block> MAGIC_CANDLE = addBlock("magic_candle", () -> new CandleBlock(blockProps(Blocks.CANDLE, DyeColor.RED)
            .sound(SoundType.STONE).lightLevel(state -> 15).strength(0.6f, 0.8f).noOcclusion()));
    public static final DeferredHolder<Block, Block> MAGIC_CANDLESTICK = addBlock("magic_candlestick", () -> new CandlestickBlock(blockProps(Blocks.CANDLE, DyeColor.YELLOW)
            .sound(SoundType.STONE).lightLevel(state -> 15).strength(1.2f, 2.0f).noOcclusion()));
    public static final DeferredHolder<Block, Block> STRAW_EFFIGY = addBlock("straw_effigy", () -> new EffigyBlock(blockProps(Blocks.HAY_BLOCK, DyeColor.YELLOW)
            .sound(SoundType.WOOD).strength(1.4f, 2.0f)
            .noOcclusion()).setShape(
            Shapes.box(0.28125, 0, 0.28125, 0.71875, 1, 0.71875)
    ));

    public static final Supplier<BlockBehaviour.Properties> GOLD_DUMMY = () -> BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BELL).strength(3.0F, 6.0F).sound(SoundType.METAL);
    public static final DeferredHolder<Block, Block> CENSER = addBlock("censer", () -> new IncenseBurnerBlock(GOLD_DUMMY.get()
            .strength(1.4f, 2.0f)
            .noOcclusion()).setShape(Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.5, 0.6875)));
    public static final DeferredHolder<Block, Block> GOBLET = addBlock("goblet", () -> new GobletBlock(GOLD_DUMMY.get()
            .strength(1.4f, 2.0f)
            .noOcclusion()).setShape(Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.5, 0.6875)));
    public static final DeferredHolder<Block, Block> ELDER_EFFIGY = addBlock("unholy_effigy", () -> new EffigyBlock(blockProps(Blocks.STONE)
            .sound(SoundType.STONE).strength(2.8f, 3.0f)
            .requiresCorrectToolForDrops()
            .noOcclusion()).setShape(
            Shapes.box(0.25, 0, 0.25, 0.75, 1, 0.75)
    ));
    public static final DeferredHolder<Block, Block> WORKTABLE = addBlock("worktable", () -> new WorktableBlock(blockProps(Blocks.OAK_WOOD)
            .sound(SoundType.WOOD).strength(1.6f, 3.0f)
            .noOcclusion()).setShape(Shapes.or(
            Shapes.box(0, 0, 0, 1, 0.25, 1),
            Shapes.box(0.125, 0.25, 0.125, 0.875, 0.625, 0.875),
            Shapes.box(0, 0.625, 0, 1, 1, 1)
    )));
    public static final DeferredHolder<Block, Block> RESEARCH_TABLE = addBlock("research_table", () -> new ResearchTableBlock(blockProps(Blocks.OAK_WOOD)
            .sound(SoundType.WOOD).strength(1.6f, 3.0f)
            .noOcclusion()).setShape(Shapes.or(
            Shapes.box(0, 0, 0, 1, 0.25, 1),
            Shapes.box(0.125, 0.25, 0.125, 0.875, 0.625, 0.875),
            Shapes.box(0, 0.625, 0, 1, 1, 1)
    )));

    public static final DeferredHolder<Block, Block> SCRIPTORIUM = addBlock("scriptorium", () -> new Scriptorium(blockProps(Blocks.CRAFTING_TABLE)));

    public static final DeferredHolder<Block, Block> PLINTH = addBlock("plinth", () -> new PillarBlockBase(blockProps(Blocks.STONE)
            .sound(SoundType.STONE).strength(2.0f, 3.0f)
            .requiresCorrectToolForDrops().noOcclusion())
            .setShape(Shapes.box(0.25, 0, 0.25, 0.75, 1, 0.75)));
    public static final DeferredHolder<Block, Block> OBELISK = addBlock("obelisk", () -> new PillarBlockBase(blockProps(Blocks.STONE)
            .sound(SoundType.STONE).strength(2.0f, 3.0f)
            .requiresCorrectToolForDrops().noOcclusion())
            .setShape(Shapes.box(0.125, 0, 0.125, 0.875, 1, 0.875)));
    public static final DeferredHolder<Block, Block> BRAZIER = addBlock("brazier", () -> new BrazierBlock(blockProps(Blocks.OAK_WOOD, DyeColor.GRAY)
            .sound(SoundType.METAL).strength(2.5f, 3.0f)
            .noOcclusion())
            .setShape(Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.75, 0.8125)));
    public static final DeferredHolder<Block, Block> CRUCIBLE = addBlock("crucible", () -> new CrucibleBlock(blockProps(Blocks.IRON_BLOCK, DyeColor.GRAY)
            .sound(SoundType.METAL).strength(4.0f, 3.0f)
            .requiresCorrectToolForDrops().noOcclusion())
            .setShape(Shapes.or(
                    Shapes.box(0.0625, 0.875, 0.0625, 0.1875, 1, 0.9375),
                    Shapes.box(0.8125, 0.875, 0.0625, 0.9375, 1, 0.9375),
                    Shapes.box(0.0625, 0.875, 0.0625, 0.9375, 1, 0.1875),
                    Shapes.box(0.0625, 0.875, 0.8125, 0.9375, 1, 0.9375),
                    Shapes.box(0, 0.125, 0, 0.125, 0.875, 1),
                    Shapes.box(0.875, 0.125, 0, 1, 0.875, 1),
                    Shapes.box(0, 0.125, 0, 1, 0.875, 0.125),
                    Shapes.box(0, 0.125, 0.875, 1, 0.875, 1),
                    Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.125, 0.9375)
            )));
    public static final DeferredHolder<Block, Block> STONE_HAND = addBlock("stone_hand", () -> new HandBlock(blockProps(Blocks.STONE)
            .sound(SoundType.STONE).strength(2.0f, 3.0f)
            .requiresCorrectToolForDrops().noOcclusion())
            .setShape(Shapes.box(0.25, 0, 0.25, 0.75, 0.75, 0.75)));
    public static final DeferredHolder<Block, Block> ENCHANTED_ASH = addBlock("enchanted_ash", () -> new EnchantedAshBlock(blockProps(Blocks.REDSTONE_WIRE, DyeColor.WHITE)
            .sound(SoundType.STONE).strength(0.0f, 0.75f).noOcclusion())
            .setShape(Shapes.empty()));
    public static final DeferredHolder<Block, Block> NECROTIC_FOCUS = addBlock("necrotic_focus", () -> new NecroticFocusBlock(blockProps(Blocks.STONE)
            .sound(SoundType.STONE).strength(2.8f, 3.0f)
            .requiresCorrectToolForDrops().noOcclusion())
            .setShape(Shapes.box(0.25, 0, 0.25, 0.75, 0.75, 0.75)));
    public static final DeferredHolder<Block, Block> PLANTER = addBlock("planter", () -> new BlockBase(blockProps(Blocks.OAK_WOOD)
            .sound(SoundType.WOOD).strength(2.0f, 3.0f)
            .noOcclusion())
            .setShape(Shapes.or(
                    Shapes.box(0, 0.25, 0, 1, 1, 1),
                    Shapes.box(0.25, 0, 0.25, 0.75, 0.25, 0.75))));
    public static final DeferredHolder<Block, Block> MERAMMER_ROOT = addBlock("merammer_root", () -> new HerbBlockBase(blockProps(Blocks.WHEAT)
            .sound(SoundType.GRASS).noOcclusion()), "lore.eidolon_repraised.merammer_root");
    public static final DeferredHolder<Block, Block> AVENNIAN_SPRIG = addBlock("avennian_sprig", () -> new HerbBlockBase(blockProps(Blocks.WHEAT)
            .sound(SoundType.GRASS).noOcclusion()), "lore.eidolon_repraised.avennian_sprig");
    public static final DeferredHolder<Block, Block> OANNA_BLOOM = addBlock("oanna_bloom", () -> new HerbBlockBase(blockProps(Blocks.WHEAT)
            .sound(SoundType.GRASS).noOcclusion()), "lore.eidolon_repraised.oanna_bloom");
    public static final DeferredHolder<Block, Block> SILDRIAN_SEED = addBlock("sildrian_seed", () -> new HerbBlockBase(blockProps(Blocks.WHEAT)
            .sound(SoundType.GRASS).noOcclusion()), "lore.eidolon_repraised.sildrian_seed");
    public static final DeferredHolder<Block, Block> MIRECAP = addBlock("mirecap", () -> new HerbBlockBase(blockProps(Blocks.BROWN_MUSHROOM)
            .sound(SoundType.NETHER_WART).noOcclusion()), "lore.eidolon_repraised.mirecap");
    public static final DeferredHolder<Block, Block> ILLWOOD_SAPLING = addBlock("illwood_sapling", () -> new SaplingBlock(new TreeGrower("illwood", Optional.empty(), Optional.of(Worldgen.ILLWOOD_TREE_CFG), Optional.empty()), blockProps(Blocks.OAK_SAPLING)
            .sound(SoundType.GRASS).noOcclusion().noCollission()));
    public static final DeferredHolder<Block, Block> ILLWOOD_LEAVES = addBlock("illwood_leaves", () -> new LeavesBlock(blockProps(Blocks.MANGROVE_LEAVES)
            .randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(Registry::allowsSpawnOnLeaves)
            .isSuffocating(Registry::isntSolid).isViewBlocking(Registry::isntSolid)));
    public static final DeferredHolder<Block, Block> STRIPPED_ILLWOOD_LOG = addBlock("stripped_illwood_log", () -> new RotatedPillarBlock(blockProps(Blocks.OAK_WOOD)
            .sound(SoundType.WOOD).strength(1.4f, 3.0f)));
    public static final DeferredHolder<Block, Block> STRIPPED_ILLWOOD_BARK = addBlock("stripped_illwood_bark", () -> new RotatedPillarBlock(blockProps(Blocks.OAK_WOOD)
            .sound(SoundType.WOOD).strength(1.4f, 3.0f)));
    public static final DeferredHolder<Block, Block> ILLWOOD_LOG = addBlock("illwood_log", () -> new StrippableLog(blockProps(Blocks.OAK_WOOD)
            .sound(SoundType.WOOD).strength(1.6f, 3.0f), STRIPPED_ILLWOOD_LOG));
    public static final DeferredHolder<Block, Block> ILLWOOD_BARK = addBlock("illwood_bark", () -> new StrippableLog(blockProps(Blocks.OAK_WOOD)
            .sound(SoundType.WOOD).strength(1.6f, 3.0f), STRIPPED_ILLWOOD_BARK));

    public static final DeferredHolder<Block, Block> SOUL_ENCHANTER = addBlock("soul_enchanter", () -> new SoulEnchanterBlock(blockProps(Blocks.ENCHANTING_TABLE)
            .sound(SoundType.STONE).strength(5.0f, 1200.0f)
            .requiresCorrectToolForDrops().noOcclusion())
            .setShape(Shapes.box(0, 0, 0, 1, 0.75, 1)));
    public static final DeferredHolder<Block, Block> WOODEN_STAND = addBlock("wooden_brewing_stand", () -> new WoodenStandBlock(blockProps(Blocks.BREWING_STAND)
            .sound(SoundType.STONE).strength(2.0f, 3.0f)
            .noOcclusion()));
    public static final DeferredHolder<Block, Block> GHOST_LIGHT = BLOCKS.register("ghost_light", () -> new GhostLight(BlockBehaviour.Properties.of().noCollission().noOcclusion().noLootTable().noTerrainParticles().dynamicShape().strength(0f, 0f)
            .sound(SoundType.FROGLIGHT).lightLevel(p -> p.getValue(GhostLight.DEITY) ? 12 : 8)));

    public static DecoBlockPack
            SMOOTH_STONE_BRICK = new DecoBlockPack(BLOCKS, "smooth_stone_bricks", blockProps(Blocks.STONE)
            .sound(SoundType.STONE).requiresCorrectToolForDrops().strength(2.0f, 3.0f))
            .addWall(),
            SMOOTH_STONE_TILES = new DecoBlockPack(BLOCKS, "smooth_stone_tiles", blockProps(Blocks.STONE)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops().strength(2.0f, 3.0f)),
            SMOOTH_STONE_MASONRY = new DecoBlockPack(BLOCKS, "smooth_stone_masonry", blockProps(Blocks.STONE)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops().strength(1.6f, 3.0f)),
            ELDER_BRICKS = new DecoBlockPack(BLOCKS, "elder_bricks", blockProps(Blocks.STONE, DyeColor.ORANGE)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops().strength(3.0f, 3.0f))
                    .addWall(),
            ELDER_MASONRY = new DecoBlockPack(BLOCKS, "elder_masonry", blockProps(Blocks.STONE, DyeColor.ORANGE)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops().strength(2.4f, 3.0f)),
            BONE_PILE = new DecoBlockPack(BLOCKS, "bone_pile", blockProps(Blocks.BONE_BLOCK)
                    .sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops().strength(1.6f, 3.0f));

    public static DecoBlockPack.WoodDecoBlock
            POLISHED_PLANKS = new DecoBlockPack.WoodDecoBlock(BLOCKS, "polished_planks", POLISHED, blockProps(Blocks.OAK_WOOD)
            .sound(SoundType.WOOD).strength(1.6f, 3.0f))
            .addFence().addButton().addSign().addPressurePlate(),
            ILLWOOD_PLANKS = new DecoBlockPack.WoodDecoBlock(BLOCKS, "illwood_planks", ILLWOOD, blockProps(Blocks.OAK_WOOD)
                    .sound(SoundType.WOOD).strength(1.6f, 3.0f))
                    .addFence().addButton().addSign().addPressurePlate();

    public static final DeferredHolder<Block, Block>
            POLISHED_WOOD_PILLAR = addBlock("polished_wood_pillar", () -> new RotatedPillarBlock(blockProps(Blocks.OAK_WOOD)
            .strength(1.6f, 3.0f))),
            SMOOTH_STONE_ARCH = addBlock("smooth_stone_arch", () -> new PillarBlockBase(blockProps(Blocks.STONE)
                    .sound(SoundType.STONE).strength(2.0f, 3.0f).requiresCorrectToolForDrops())),
            MOSSY_SMOOTH_STONE_BRICKS = addBlock("mossy_smooth_stone_bricks", blockProps(Blocks.STONE)
                    .sound(SoundType.STONE).strength(2.0f, 3.0f).requiresCorrectToolForDrops()),
            ELDER_BRICKS_EYE = addBlock("elder_bricks_eye", blockProps(Blocks.STONE, DyeColor.ORANGE)
                    .sound(SoundType.STONE).strength(3.0f, 3.0f).requiresCorrectToolForDrops()),
            ELDER_PILLAR = addBlock("elder_pillar", () -> new PillarBlockBase(blockProps(Blocks.STONE, DyeColor.ORANGE)
                    .sound(SoundType.STONE).strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()));

    public static final DeferredHolder<MenuType<?>, MenuType<WorktableContainer>>
            WORKTABLE_CONTAINER = addContainer("worktable", WorktableContainer::new);
    public static final DeferredHolder<MenuType<?>, MenuType<SoulEnchanterContainer>>
            SOUL_ENCHANTER_CONTAINER = addContainer("soul_enchanter", SoulEnchanterContainer::new);
    public static final DeferredHolder<MenuType<?>, MenuType<WoodenBrewingStandContainer>>
            WOODEN_STAND_CONTAINER = addContainer("wooden_brewing_stand", WoodenBrewingStandContainer::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ResearchTableContainer>>
            RESEARCH_TABLE_CONTAINER = addContainer("research_table", ResearchTableContainer::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ScriptoriumContainer>>
            SCRIPTORIUM_CONTAINER = addContainer("scriptorium", ScriptoriumContainer::new);

    @SubscribeEvent // on the mod event bus only on the physical client
    public void registerScreens(RegisterMenuScreensEvent event) {
        event.register(Registry.WORKTABLE_CONTAINER.get(), WorktableScreen::new);
        event.register(Registry.SOUL_ENCHANTER_CONTAINER.get(), SoulEnchanterScreen::new);
        event.register(Registry.WOODEN_STAND_CONTAINER.get(), WoodenBrewingStandScreen::new);
        event.register(Registry.RESEARCH_TABLE_CONTAINER.get(), ResearchTableScreen::new);
        event.register(Registry.SCRIPTORIUM_CONTAINER.get(), ScriptoriumScreen::new);
    }

    public static void init(IEventBus modEventBus) {
        EidolonAttributes.ATTRIBUTES.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        EidolonDataComponents.DATA.register(modEventBus);
        EidolonMaterials.MATERIALS.register(modEventBus);
        EidolonEntities.ENTITIES.register(modEventBus);
        EidolonAttachments.ATTACHMENT_TYPES.register(modEventBus);
        EidolonPotions.POTIONS.register(modEventBus);
        EidolonPotions.POTION_TYPES.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
        EidolonParticles.PARTICLES.register(modEventBus);
        EidolonSounds.SOUND_EVENTS.register(modEventBus);
        Worldgen.FEATURES.register(modEventBus);
        CONTAINERS.register(modEventBus);
        EidolonRecipes.RECIPE_TYPES.register(modEventBus);
        EidolonRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        ARG_TYPES.register(modEventBus);
        TABS.register(modEventBus);
        AdvancementTriggers.TRIGGERS.register(modEventBus);
        //modEventBus.addListener(Registry::registerScreens);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientInit() {
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<HandTileEntity>> HAND_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<BrazierTileEntity>> BRAZIER_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<NecroticFocusTileEntity>> NECROTIC_FOCUS_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleTileEntity>> CRUCIBLE_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<EffigyTileEntity>> EFFIGY_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<SoulEnchanterTileEntity>> SOUL_ENCHANTER_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<WoodenStandTileEntity>> WOODEN_STAND_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<GobletTileEntity>> GOBLET_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<CenserTileEntity>> CENSER_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ResearchTableTileEntity>> RESEARCH_TABLE_TILE_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ScriptoriumTile>> SCRIPTORIUM_TILE;


    static {
        HAND_TILE_ENTITY = TILE_ENTITIES.register("hand_tile", () -> BlockEntityType.Builder.of(HandTileEntity::new, STONE_HAND.get()).build(null));
        BRAZIER_TILE_ENTITY = TILE_ENTITIES.register("brazier_tile", () -> BlockEntityType.Builder.of(BrazierTileEntity::new, BRAZIER.get()).build(null));
        NECROTIC_FOCUS_TILE_ENTITY = TILE_ENTITIES.register("necrotic_focus", () -> BlockEntityType.Builder.of(NecroticFocusTileEntity::new, NECROTIC_FOCUS.get()).build(null));
        CRUCIBLE_TILE_ENTITY = TILE_ENTITIES.register("crucible", () -> BlockEntityType.Builder.of(CrucibleTileEntity::new, CRUCIBLE.get()).build(null));
        EFFIGY_TILE_ENTITY = TILE_ENTITIES.register("effigy", () -> BlockEntityType.Builder.of(EffigyTileEntity::new, STRAW_EFFIGY.get(), ELDER_EFFIGY.get()).build(null));
        SOUL_ENCHANTER_TILE_ENTITY = TILE_ENTITIES.register("soul_enchanter", () -> BlockEntityType.Builder.of(SoulEnchanterTileEntity::new, SOUL_ENCHANTER.get()).build(null));
        WOODEN_STAND_TILE_ENTITY = TILE_ENTITIES.register("wooden_brewing_stand", () -> BlockEntityType.Builder.of(WoodenStandTileEntity::new, WOODEN_STAND.get()).build(null));
        GOBLET_TILE_ENTITY = TILE_ENTITIES.register("goblet", () -> BlockEntityType.Builder.of(GobletTileEntity::new, GOBLET.get()).build(null));
        CENSER_TILE_ENTITY = TILE_ENTITIES.register("censer", () -> BlockEntityType.Builder.of(CenserTileEntity::new, CENSER.get()).build(null));
        RESEARCH_TABLE_TILE_ENTITY = TILE_ENTITIES.register("research_table", () -> BlockEntityType.Builder.of(ResearchTableTileEntity::new, RESEARCH_TABLE.get()).build(null));
        SCRIPTORIUM_TILE = TILE_ENTITIES.register("scriptorium", () -> BlockEntityType.Builder.of(ScriptoriumTile::new, SCRIPTORIUM.get()).build(null));
    }


    public static final DamageTypeData SAPPING = DamageTypeData.builder()
            .simpleId("sap")
            .scaling(DamageScaling.ALWAYS)
            .tag(Tags.DamageTypes.IS_WITHER)
            .build();

    public static final DamageTypeData RITUAL_DAMAGE = DamageTypeData.builder()
            .simpleId("ritual")
            .scaling(DamageScaling.ALWAYS)
            .tag(DamageTypeTags.BYPASSES_ARMOR)
            .tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
            .build();
    public static final DamageTypeData FROST_DAMAGE = DamageTypeData.builder()
            .simpleId("frost")
            .effects(DamageEffects.FREEZING)
            .tag(Tags.DamageTypes.IS_MAGIC)
            .build();

    public void registerCaps(RegisterCapabilitiesEvent event) {
//        event.register(IReputation.class);
//        event.register(IKnowledge.class);
//        event.register(ISoul.class);
//        event.register(IPlayerData.class);
    }

    @SuppressWarnings("deprecation")
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerFactories(RegisterParticleProvidersEvent evt) {
        Minecraft.getInstance().particleEngine.register(EidolonParticles.FEATHER_PARTICLE.get(), FeatherParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.FLAME_PARTICLE.get(), FlameParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.SMOKE_PARTICLE.get(), SmokeParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.SPARKLE_PARTICLE.get(), SparkleParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.WISP_PARTICLE.get(), WispParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.BUBBLE_PARTICLE.get(), BubbleParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.STEAM_PARTICLE.get(), SteamParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.LINE_WISP_PARTICLE.get(), LineWispParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.SIGN_PARTICLE.get(), sprite -> new SignParticleType.Factory());
        Minecraft.getInstance().particleEngine.register(EidolonParticles.SLASH_PARTICLE.get(), SlashParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.GLOWING_SLASH_PARTICLE.get(), GlowingSlashParticleType.Factory::new);
        Minecraft.getInstance().particleEngine.register(EidolonParticles.RUNE_PARTICLE.get(), sprite -> new RuneParticleType.Factory());
    }

    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<KnowledgeCommand.SignArgument>> SIGN_ARG = ARG_TYPES.register("sign", () -> ArgumentTypeInfos.registerByClass(KnowledgeCommand.SignArgument.class, SingletonArgumentInfo.contextFree(KnowledgeCommand.SignArgument::signs)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<KnowledgeCommand.RuneArgument>> RUNE_ARG = ARG_TYPES.register("rune", () -> ArgumentTypeInfos.registerByClass(KnowledgeCommand.RuneArgument.class, SingletonArgumentInfo.contextFree(KnowledgeCommand.RuneArgument::runes)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<KnowledgeCommand.ResearchArgument>> RESEARCH_ARG = ARG_TYPES.register("research", () -> ArgumentTypeInfos.registerByClass(KnowledgeCommand.ResearchArgument.class, SingletonArgumentInfo.contextFree(KnowledgeCommand.ResearchArgument::researches)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<ReputationCommand.DeityArgument>> DEITY_ARG = ARG_TYPES.register("deity", () -> ArgumentTypeInfos.registerByClass(ReputationCommand.DeityArgument.class, SingletonArgumentInfo.contextFree(ReputationCommand.DeityArgument::deities)));

}
