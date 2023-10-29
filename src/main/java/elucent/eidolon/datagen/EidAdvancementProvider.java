package elucent.eidolon.datagen;

import com.google.common.collect.Maps;
import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.AdvancementTriggers;
import elucent.eidolon.registries.Registry;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class EidAdvancementProvider extends ForgeAdvancementProvider {

    public EidAdvancementProvider(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper fileHelperIn) {
        super(generatorIn.getPackOutput(), registries, fileHelperIn, List.of(new EidolonAdvancements()));
    }


    static class EidolonAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {
        Consumer<Advancement> advCon;

        @Override
        public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<Advancement> con, @NotNull ExistingFileHelper existingFileHelper) {
            this.advCon = con;
            Advancement root = builder(Eidolon.MODID).display(Registry.CODEX.get(), Component.translatable("eidolon.advancement.title.root"),
                    Component.translatable("eidolon.advancement.desc.root"),
                    new ResourceLocation("eidolon:textures/block/bone_pile.png"),
                    FrameType.TASK, false, false, false).addCriterion("eidolon:ars_ecclesia",
                    InventoryChangeTrigger.TriggerInstance.hasItems(Registry.CODEX.get())).save(con, "eidolon:root");
            Advancement theurgy = saveBasicItem(Registry.STRAW_EFFIGY.get(), root);
            Advancement altar = saveBasicItem(Registry.WOODEN_ALTAR.get(), theurgy);
            saveBasicItem(Registry.STONE_ALTAR.get(), altar);

            Advancement sacredRoot = saveWithTrigger(theurgy, Registry.HOLY_SYMBOL.get(), AdvancementTriggers.SACRED);
            Advancement incense = saveWithTrigger(sacredRoot, Registry.CENSER.get(), AdvancementTriggers.INCENSE);
            Advancement darkRoot = saveWithTrigger(theurgy, Registry.UNHOLY_SYMBOL.get(), AdvancementTriggers.WICKED);
            Advancement goblet = saveWithTrigger(darkRoot, Registry.GOBLET.get(), AdvancementTriggers.SACRIFICE);

            Advancement symbol = saveBasicItem(Registry.HOLY_SYMBOL.get(), incense);

            Advancement layHands = saveWithTrigger(symbol, Items.GLISTERING_MELON_SLICE, AdvancementTriggers.LAY_ON_HANDS);
            Advancement cureZombie = saveWithTrigger(layHands, Items.GOLDEN_APPLE, AdvancementTriggers.CURE_ZOMBIE);

            symbol = saveBasicItem(Registry.UNHOLY_SYMBOL.get(), goblet);

            Advancement v_sacrifice = saveWithTrigger(symbol, Items.IRON_SWORD, AdvancementTriggers.VSACRIFICE);
            Advancement zombify = saveWithTrigger(v_sacrifice, Registry.ZOMBIE_HEART.get(), AdvancementTriggers.ZOMBIFY);

            Advancement artificeRoot = saveBasicItem(Registry.PEWTER_INGOT.get(), root);

            Advancement alchemy = saveBasicItem(Registry.CRUCIBLE.get(), artificeRoot);
            Advancement researchs = saveBasicItem(Registry.RESEARCH_NOTES.get(), alchemy);
            saveWithTrigger(researchs, Blocks.CAMPFIRE, AdvancementTriggers.FLAME);
            saveWithTrigger(researchs, Blocks.BLUE_ICE, AdvancementTriggers.FROST);

            Advancement apothecary = saveBasicItem(Registry.WOODEN_STAND.get(), alchemy);

            Advancement worktable = saveBasicItem(Registry.WORKTABLE.get(), artificeRoot);
            Advancement athame = saveBasicItem(Registry.ATHAME.get(), worktable);
            Advancement scythe = saveBasicItem(Registry.REAPER_SCYTHE.get(), worktable);

            Advancement braziers = saveBasicItem(Registry.BRAZIER.get(), artificeRoot);

            Advancement soulShard = saveBasicItem(Registry.SOUL_SHARD.get(), braziers);

        }

        private Advancement saveWithTrigger(Advancement parent, @NotNull ItemLike display, PlayerTrigger playerTrigger) {
            return builder(playerTrigger.getId().getPath()).display(display, FrameType.TASK).addCriterion(new PlayerTrigger.TriggerInstance(playerTrigger.getId(), ContextAwarePredicate.ANY)).parent(parent).save(advCon);
        }

        public AdvancementBuilder buildBasicItem(ItemLike item, Advancement parent) {
            return builder(ForgeRegistries.ITEMS.getKey(item.asItem()).getPath()).normalItemRequirement(item).parent(parent);
        }

        public Advancement saveBasicItem(ItemLike item, Advancement parent) {
            return buildBasicItem(item, parent).save(advCon);
        }

        public AdvancementBuilder builder(String key) {
            return AdvancementBuilder.builder(Eidolon.MODID, key);
        }
    }


    static class AdvancementBuilder implements net.minecraftforge.common.extensions.IForgeAdvancementBuilder {
        @Nullable
        private ResourceLocation parentId;
        @Nullable
        private Advancement parent;
        @Nullable
        private DisplayInfo display;
        private AdvancementRewards rewards = AdvancementRewards.EMPTY;
        private Map<String, Criterion> criteria = Maps.newLinkedHashMap();
        @Nullable
        private String[][] requirements;
        private RequirementsStrategy requirementsStrategy = RequirementsStrategy.AND;
        private String modid;
        private String fileKey;

        private AdvancementBuilder(@Nullable ResourceLocation pParentId, @Nullable DisplayInfo pDisplay, AdvancementRewards pRewards, Map<String, Criterion> pCriteria, String[] @org.jetbrains.annotations.Nullable [] pRequirements) {
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

        public AdvancementBuilder parent(Advancement pParent) {
            this.parent = pParent;
            return this;
        }

        public AdvancementBuilder parent(ResourceLocation pParentId) {
            this.parentId = pParentId;
            return this;
        }

        public AdvancementBuilder display(ItemStack pStack, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, FrameType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
            return this.display(new DisplayInfo(pStack, pTitle, pDescription, pBackground, pFrame, pShowToast, pAnnounceToChat, pHidden));
        }

        public AdvancementBuilder display(ItemLike pItem, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, FrameType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
            return this.display(new DisplayInfo(new ItemStack(pItem.asItem()), pTitle, pDescription, pBackground, pFrame, pShowToast, pAnnounceToChat, pHidden));
        }

        public AdvancementBuilder display(DisplayInfo pDisplay) {
            this.display = pDisplay;
            return this;
        }

        // The following displays cannot be used for roots.
        public AdvancementBuilder display(ItemStack pItem, FrameType pFrame) {
            return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), null, pFrame, true, true, false));
        }

        public AdvancementBuilder display(ItemLike pItem, FrameType pFrame) {
            return this.display(new ItemStack(pItem), pFrame);
        }

        // The following displays cannot be used for roots.
        public AdvancementBuilder display(ItemStack pItem, FrameType pFrame, boolean hidden) {
            return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), null, pFrame, true, true, hidden));
        }

        public AdvancementBuilder display(ItemLike pItem, FrameType pFrame, boolean hidden) {
            return this.display(new ItemStack(pItem), pFrame, hidden);
        }


        public AdvancementBuilder rewards(AdvancementRewards.Builder pRewardsBuilder) {
            return this.rewards(pRewardsBuilder.build());
        }

        public AdvancementBuilder rewards(AdvancementRewards pRewards) {
            this.rewards = pRewards;
            return this;
        }

        public AdvancementBuilder addCriterion(String pKey, CriterionTriggerInstance pCriterion) {
            return this.addCriterion(pKey, new Criterion(pCriterion));
        }

        public AdvancementBuilder addCriterion(CriterionTriggerInstance pCriterion) {
            return this.addCriterion(fileKey, new Criterion(pCriterion));
        }

        public AdvancementBuilder addCriterion(String pKey, Criterion pCriterion) {
            if (this.criteria.containsKey(pKey)) {
                throw new IllegalArgumentException("Duplicate criterion " + pKey);
            } else {
                this.criteria.put(pKey, pCriterion);
                return this;
            }
        }

        public AdvancementBuilder requirements(RequirementsStrategy pStrategy) {
            this.requirementsStrategy = pStrategy;
            return this;
        }

        public AdvancementBuilder requirements(String[][] pRequirements) {
            this.requirements = pRequirements;
            return this;
        }

        public AdvancementBuilder normalItemRequirement(ItemLike item) {
            return this.display(item, FrameType.TASK).requireItem(item);
        }

        public AdvancementBuilder requireItem(ItemLike item) {
            return this.addCriterion("has_" + ForgeRegistries.ITEMS.getKey(item.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(item));
        }

        public MutableComponent getComponent(String type) {
            return Component.translatable(modid + ".advancement." + type + "." + fileKey);
        }

        /**
         * Tries to resolve the parent of this advancement, if possible. Returns true on success.
         */
        public boolean canBuild(Function<ResourceLocation, Advancement> pParentLookup) {
            if (this.parentId == null) {
                return true;
            } else {
                if (this.parent == null) {
                    this.parent = pParentLookup.apply(this.parentId);
                }

                return this.parent != null;
            }
        }

        public Advancement build(ResourceLocation pId) {
            if (!this.canBuild((p_138407_) -> {
                return null;
            })) {
                throw new IllegalStateException("Tried to build incomplete advancement!");
            } else {
                if (this.requirements == null) {
                    this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
                }

                return new Advancement(pId, this.parent, this.display, this.rewards, this.criteria, this.requirements, false);
            }
        }

        public Advancement save(Consumer<Advancement> pConsumer, String pId) {
            Advancement advancement = this.build(new ResourceLocation(pId));
            pConsumer.accept(advancement);
            return advancement;
        }

        public Advancement save(Consumer<Advancement> pConsumer) {
            return this.save(pConsumer, new ResourceLocation(modid, fileKey).toString());
        }

        public String toString() {
            return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
        }

        public Map<String, Criterion> getCriteria() {
            return this.criteria;
        }
    }

}