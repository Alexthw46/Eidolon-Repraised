package alexthw.eidolon_repraised.datagen;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.registries.DecoBlockPack;
import alexthw.eidolon_repraised.registries.EidolonEntities;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static alexthw.eidolon_repraised.registries.Registry.*;

public class ModLootTables extends LootTableProvider {

    public ModLootTables(DataGenerator dataGeneratorIn, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(dataGeneratorIn.getPackOutput(), new HashSet<>(), List.of(new SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK), new SubProviderEntry(EntityLootTable::new, LootContextParamSets.ENTITY)), lookupProvider);
    }

    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public static class BlockLootTable extends BlockLootSubProvider {
        public final List<Block> list = new ArrayList<>();

        protected BlockLootTable(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> p_249322_) {
            this.generate();
            Set<ResourceKey<LootTable>> set = new HashSet<>();

            for (Block block : list) {
                if (block.isEnabled(this.enabledFeatures)) {
                    ResourceKey<LootTable> resourceKey = block.getLootTable();
                    if (resourceKey != BuiltInLootTables.EMPTY && set.add(resourceKey)) {
                        LootTable.Builder loottable$builder = this.map.remove(resourceKey);
                        if (loottable$builder == null) {
                            continue;
                        }

                        p_249322_.accept(resourceKey, loottable$builder);
                    }
                }
            }
        }

        protected void generate() {

            registerLeavesAndSticks(ILLWOOD_LEAVES.get(), ILLWOOD_SAPLING.get());
            registerDropSelf(ILLWOOD_SAPLING.get());
            registerDropSelf(ILLWOOD_LOG.get());
            registerDropSelf(ILLWOOD_BARK.get());
            registerDropSelf(STRIPPED_ILLWOOD_LOG.get());
            registerDropSelf(STRIPPED_ILLWOOD_BARK.get());
            registerDropSelf(ILLWOOD_PLANKS);
            registerDropSelf(ELDER_BRICKS);
            registerDropSelf(ELDER_MASONRY);
            registerDropSelf(SMOOTH_STONE_MASONRY);
            registerDropSelf(BONE_PILE);
            registerDropSelf(ELDER_PILLAR.get());
            registerDropSelf(ELDER_BRICKS_EYE.get());
            registerDropSelf(SMOOTH_STONE_ARCH.get());
            registerDropSelf(MOSSY_SMOOTH_STONE_BRICKS.get());
        }

        public void registerDropSelf(Block block) {
            this.list.add(block);
            this.dropSelf(block);
        }

        public void registerDropSelf(DecoBlockPack blockpack) {
            List<Block> blocks = new ArrayList<>();
            blocks.add(blockpack.getBlock());
            blocks.add(blockpack.getSlab());
            blocks.add(blockpack.getStairs());
            blocks.add(blockpack.getWall());

            if (blockpack instanceof DecoBlockPack.WoodDecoBlock woodDecoBlock) {
                blocks.add(woodDecoBlock.getFence());
                blocks.add(woodDecoBlock.getFenceGate());
                blocks.add(woodDecoBlock.getButton());
                blocks.add(woodDecoBlock.getPressurePlate());
                blocks.add(woodDecoBlock.getStandingSign());
                blocks.add(woodDecoBlock.getWallSign());
                blocks.add(woodDecoBlock.getHangingSign());
                blocks.add(woodDecoBlock.getHangingWallSign());
            }

            for (var block : blocks) {
                if (block == null) continue;
                this.list.add(block);
                this.dropSelf(block);
            }
        }

        public void registerLeavesAndSticks(Block leaves, Block sapling) {
            list.add(leaves);
            this.add(leaves, l_state -> createLeavesDrops(l_state, sapling, DEFAULT_SAPLING_DROP_RATES));
        }

        private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

        protected @NotNull Iterable<Block> getKnownBlocks() {
            return this.list;
        }
    }

    public static class EntityLootTable extends EntityLootSubProvider {
        private final Map<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>> map = Maps.newHashMap();

        protected EntityLootTable(HolderLookup.Provider pRegistries) {
            super(FeatureFlags.REGISTRY.allFlags(), pRegistries);
        }
        @Override
        public void generate() {
            // Zombie Brute
            this.add(EidolonEntities.ZOMBIE_BRUTE.get(),
                    LootTable.lootTable()
                            // Rotten Flesh
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(Items.ROTTEN_FLESH)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries,UniformGenerator.between(0.0F, 2.0F)))
                                    )
                            )
                            // Bone
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(Items.BONE)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries,UniformGenerator.between(0.0F, 1.0F)))
                                    )
                            )
                            // Zombie Heart
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(ZOMBIE_HEART.get()))
                                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.15F, 0.1F))
                            )
                            // Lead Ingot
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(LEAD_INGOT.get()))
                                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025F, 0.01F))
                            )
            );

            // Wraith
            this.add(EidolonEntities.WRAITH.get(),
                    LootTable.lootTable()
                            // Tattered Cloth
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(TATTERED_CLOTH.get())
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries,UniformGenerator.between(0.0F, 2.0F)))
                                    )
                            )
                            // Wraith Heart
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(WRAITH_HEART.get()))
                                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.05F, 0.025F))
                            )
            );

            // Necromancer
            this.add(EidolonEntities.NECROMANCER.get(),
                    LootTable.lootTable()
                            // Summoning Staff
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(SUMMONING_STAFF.get())
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                    )
                            )
                            // Emerald
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(Items.EMERALD)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries,UniformGenerator.between(0.0F, 1.0F)))
                                    )
                                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            )
            );

            // Giant Skeleton
            this.add(EidolonEntities.GIANT_SKEL.get(),
                    LootTable.lootTable()
                            // Bone
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(Items.BONE)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                                    )
                            )
                            // Imbued Bones
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(IMBUED_BONES.get()))
                                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.15F, 0.15F))
                            )
            );

            // Raven
            this.add(EidolonEntities.RAVEN.get(),
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(RAVEN_FEATHER.get())
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                                    )
                            )
            );

            // Slimy Slug
            this.add(EidolonEntities.SLIMY_SLUG.get(),
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(Items.SLIME_BALL)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                                    )
                            )
            );
        }

        @Override
        protected void add(@NotNull EntityType<?> pEntityType, LootTable.@NotNull Builder pBuilder) {
            super.add(pEntityType, pBuilder);
            this.map.put(pEntityType, ImmutableMap.of(pEntityType.getDefaultLootTable(), pBuilder));
        }

        @Override
        protected void add(@NotNull EntityType<?> pEntityType, @NotNull ResourceKey<LootTable> pLootTableLocation, LootTable.@NotNull Builder pBuilder) {
            super.add(pEntityType, pLootTableLocation, pBuilder);
            this.map.computeIfAbsent(pEntityType, (p_249004_) -> {
                return Maps.newHashMap();
            }).put(pLootTableLocation, pBuilder);
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> pGenerator) {
            this.generate();
            Set<ResourceKey<LootTable>> set = Sets.newHashSet();
            this.getKnownEntityTypes().map(EntityType::builtInRegistryHolder).forEach((p_249003_) -> {
                EntityType<?> entitytype = p_249003_.value();
                if (canHaveLootTable(entitytype)) {
                    Map<ResourceKey<LootTable>, LootTable.Builder> map = this.map.remove(entitytype);
                    ResourceKey<LootTable> resourcelocation = entitytype.getDefaultLootTable();
                    if (map != null) {
                        map.forEach((p_250376_, p_250972_) -> {
                            if (!set.add(p_250376_)) {
                                throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", p_250376_, p_249003_.key().location()));
                            } else {
                                pGenerator.accept(p_250376_, p_250972_);
                            }
                        });
                    }
                } else {
                    Map<ResourceKey<LootTable>, LootTable.Builder> map1 = this.map.remove(entitytype);
                    if (map1 != null) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", map1.keySet().stream().map(ResourceKey::toString).collect(Collectors.joining(",")), p_249003_.key().location()));
                    }
                }

            });
        }

        @Override
        protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
            return BuiltInRegistries.ENTITY_TYPE.stream().filter(block -> BuiltInRegistries.ENTITY_TYPE.getKey(block).getNamespace().equals(Eidolon.MODID)).toList().stream();
        }
    }

}