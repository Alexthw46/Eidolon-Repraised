package alexthw.eidolon_repraised.datagen;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.registries.AdvancementTriggers;
import alexthw.eidolon_repraised.registries.Registry;
import com.google.common.collect.Maps;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static alexthw.eidolon_repraised.registries.AdvancementTriggers.createCriterion;

public class EidAdvancementProvider extends AdvancementProvider {

    public EidAdvancementProvider(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper fileHelperIn) {
        super(generatorIn.getPackOutput(), registries, fileHelperIn, List.of(new EidolonAdvancements()));
    }


    static class EidolonAdvancements implements AdvancementProvider.AdvancementGenerator {
        Consumer<AdvancementHolder> advCon;

        @Override
        public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> con, @NotNull ExistingFileHelper existingFileHelper) {
            this.advCon = con;
            AdvancementHolder root = builder(Eidolon.MODID).display(Registry.CODEX.get(), Component.translatable("eidolon_repraised.advancement.title.root"),
                    Component.translatable("eidolon_repraised.advancement.desc.root"),
                    ResourceLocation.tryParse("eidolon_repraised:textures/block/bone_pile.png"), AdvancementType.TASK, false, false, false).addCriterion("eidolon_repraised:ars_ecclesia",
                    InventoryChangeTrigger.TriggerInstance.hasItems(Registry.CODEX.get())).save(con, "eidolon_repraised:root");
            AdvancementHolder theurgy = saveBasicItem(Registry.STRAW_EFFIGY.get(), root);
            AdvancementHolder altar = saveBasicItem(Registry.WOODEN_ALTAR.get(), theurgy);
            saveBasicItem(Registry.STONE_ALTAR.get(), altar);

            AdvancementHolder sacredRoot = saveWithTrigger(theurgy, Registry.HOLY_SYMBOL.get(), AdvancementTriggers.SACRED);
            AdvancementHolder incense = saveWithTrigger(sacredRoot, Registry.CENSER.get(), AdvancementTriggers.INCENSE);
            AdvancementHolder darkRoot = saveWithTrigger(theurgy, Registry.UNHOLY_SYMBOL.get(), AdvancementTriggers.WICKED);
            AdvancementHolder goblet = saveWithTrigger(darkRoot, Registry.GOBLET.get(), AdvancementTriggers.SACRIFICE);

            AdvancementHolder symbol = saveBasicItem(Registry.HOLY_SYMBOL.get(), incense);

            AdvancementHolder layHands = saveWithTrigger(symbol, Items.GLISTERING_MELON_SLICE, AdvancementTriggers.LAY_ON_HANDS);
            AdvancementHolder cureZombie = saveWithTrigger(layHands, Items.GOLDEN_APPLE, AdvancementTriggers.CURE_ZOMBIE);
            AdvancementHolder smite = saveWithTrigger(cureZombie, Items.GOLDEN_SWORD, AdvancementTriggers.SMITE);

            symbol = saveBasicItem(Registry.UNHOLY_SYMBOL.get(), goblet);

            AdvancementHolder v_sacrifice = saveWithTrigger(symbol, Items.IRON_SWORD, AdvancementTriggers.VSACRIFICE);
            AdvancementHolder zombify = saveWithTrigger(v_sacrifice, Registry.ZOMBIE_HEART.get(), AdvancementTriggers.ZOMBIFY);
            AdvancementHolder enthrall = saveWithTrigger(zombify, Registry.SUMMONING_STAFF.get(), AdvancementTriggers.ENTHRALL);

            AdvancementHolder artificeRoot = saveBasicItem(Registry.PEWTER_INGOT.get(), root);

            AdvancementHolder alchemy = saveBasicItem(Registry.CRUCIBLE.get(), artificeRoot);
            AdvancementHolder researchs = saveBasicItem(Registry.RESEARCH_NOTES.get(), alchemy);
            saveWithTrigger(researchs, Blocks.CAMPFIRE, AdvancementTriggers.FLAME);
            saveWithTrigger(researchs, Blocks.BLUE_ICE, AdvancementTriggers.FROST);

            AdvancementHolder apothecary = saveBasicItem(Registry.WOODEN_STAND.get(), alchemy);

            AdvancementHolder worktable = saveBasicItem(Registry.WORKTABLE.get(), artificeRoot);
            AdvancementHolder athame = saveBasicItem(Registry.ATHAME.get(), worktable);
            AdvancementHolder scythe = saveBasicItem(Registry.REAPER_SCYTHE.get(), worktable);

            AdvancementHolder braziers = saveBasicItem(Registry.BRAZIER.get(), artificeRoot);

            AdvancementHolder soulShard = saveBasicItem(Registry.SOUL_SHARD.get(), braziers);

        }

        private AdvancementHolder saveWithTrigger(AdvancementHolder parent, @NotNull ItemLike display, DeferredHolder<CriterionTrigger<?>, PlayerTrigger> playerTrigger) {
            return builder(playerTrigger.getId().getPath()).display(display, AdvancementType.TASK).addCriterion(createCriterion(playerTrigger)).parent(parent).save(advCon);
        }

        public AdvancementBuilder buildBasicItem(ItemLike item, AdvancementHolder parent) {
            return builder(BuiltInRegistries.ITEM.getKey(item.asItem()).getPath()).normalItemRequirement(item).parent(parent);
        }

        public AdvancementHolder saveBasicItem(ItemLike item, AdvancementHolder parent) {
            return buildBasicItem(item, parent).save(advCon);
        }

        public AdvancementBuilder builder(String key) {
            return AdvancementBuilder.builder(Eidolon.MODID, key);
        }
    }


    static class AdvancementBuilder implements net.neoforged.neoforge.common.extensions.IAdvancementBuilderExtension {
        @Nullable
        private ResourceLocation parentId;
        @Nullable
        private AdvancementHolder parent;
        @Nullable
        private DisplayInfo display;
        private AdvancementRewards rewards = AdvancementRewards.EMPTY;
        private Map<String, Criterion<?>> criteria = Maps.newLinkedHashMap();
        @Nullable
        private AdvancementRequirements requirements;
        private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
        private String modid;
        private String fileKey;

        private AdvancementBuilder(@Nullable ResourceLocation pParentId, @Nullable DisplayInfo pDisplay, AdvancementRewards pRewards, Map<String, Criterion<?>> pCriteria, AdvancementRequirements pRequirements) {
            this.parentId = pParentId;
            this.display = pDisplay;
            this.rewards = pRewards;
            this.criteria = pCriteria;
            this.requirements = pRequirements;
        }

        private AdvancementBuilder(String modid, String fileKey) {
            this.modid = modid;
            this.fileKey = fileKey;
        }

        public static AdvancementBuilder builder(String modid, String fileKey) {
            return new AdvancementBuilder(modid, fileKey);
        }

        public AdvancementBuilder parent(AdvancementHolder parent) {
            this.parent = parent;
            return this.parent(parent.id());
        }

        public AdvancementBuilder parent(ResourceLocation pParentId) {
            this.parentId = pParentId;
            return this;
        }

        public AdvancementBuilder display(ItemStack pStack, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, AdvancementType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
            return this.display(new DisplayInfo(pStack, pTitle, pDescription, Optional.ofNullable(pBackground), pFrame, pShowToast, pAnnounceToChat, pHidden));
        }

        public AdvancementBuilder display(ItemLike pItem, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, AdvancementType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
            return this.display(new DisplayInfo(new ItemStack(pItem.asItem()), pTitle, pDescription, Optional.ofNullable(pBackground), pFrame, pShowToast, pAnnounceToChat, pHidden));
        }

        public AdvancementBuilder display(DisplayInfo pDisplay) {
            this.display = pDisplay;
            return this;
        }

        // The following displays cannot be used for roots.
        public AdvancementBuilder display(ItemStack pItem, AdvancementType pFrame) {
            return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), Optional.empty(), pFrame, true, true, false));
        }

        public AdvancementBuilder display(ItemLike pItem, AdvancementType pFrame) {
            return this.display(new ItemStack(pItem), pFrame);
        }

        // The following displays cannot be used for roots.
        public AdvancementBuilder display(ItemStack pItem, AdvancementType pFrame, boolean hidden) {
            return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), Optional.empty(), pFrame, true, true, hidden));
        }

        public AdvancementBuilder display(ItemLike pItem, AdvancementType pFrame, boolean hidden) {
            return this.display(new ItemStack(pItem), pFrame, hidden);
        }

        public AdvancementBuilder rewards(AdvancementRewards.Builder pRewardsBuilder) {
            return this.rewards(pRewardsBuilder.build());
        }

        public AdvancementBuilder rewards(AdvancementRewards pRewards) {
            this.rewards = pRewards;
            return this;
        }

        public AdvancementBuilder addCriterion(Criterion<?> pCriterion) {
            return this.addCriterion(fileKey, pCriterion);
        }

        public AdvancementBuilder addCriterion(String pKey, Criterion<?> pCriterion) {
            if (this.criteria.containsKey(pKey)) {
                throw new IllegalArgumentException("Duplicate criterion " + pKey);
            } else {
                this.criteria.put(pKey, pCriterion);
                return this;
            }
        }

        public AdvancementBuilder requirements(AdvancementRequirements.Strategy pStrategy) {
            this.requirementsStrategy = pStrategy;
            return this;
        }

        public AdvancementBuilder requirements(AdvancementRequirements pRequirements) {
            this.requirements = pRequirements;
            return this;
        }

        public AdvancementBuilder normalItemRequirement(ItemLike item) {
            return this.display(item, AdvancementType.TASK).requireItem(item);
        }

        public AdvancementBuilder requireItem(ItemLike item) {
            return this.addCriterion("has_" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(item));
        }

        public MutableComponent getComponent(String type) {
            return Component.translatable(modid + ".advancement." + type + "." + fileKey);
        }

        public Advancement build() {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.create(this.criteria.keySet());
            }
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("Advancement " + fileKey + " has no criteria " + this);
            }
            return new Advancement(Optional.ofNullable(this.parentId), Optional.ofNullable(this.display), this.rewards, this.criteria, this.requirements, false);
        }

        public AdvancementHolder save(Consumer<AdvancementHolder> pConsumer, String pId) {
            return save(pConsumer, ResourceLocation.tryParse(pId));
        }

        public AdvancementHolder save(Consumer<AdvancementHolder> pConsumer, ResourceLocation pId) {
            var adv = this.build();
            AdvancementHolder advancement = new AdvancementHolder(pId, adv);
            pConsumer.accept(advancement);
            return advancement;
        }

        public AdvancementHolder save(Consumer<AdvancementHolder> pConsumer) {
            return this.save(pConsumer, ResourceLocation.fromNamespaceAndPath(modid, fileKey));
        }

        public String toString() {
            return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + this.requirements + "}";
        }

        public Map<String, Criterion<?>> getCriteria() {
            return this.criteria;
        }
    }

}