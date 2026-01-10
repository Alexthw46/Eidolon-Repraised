package alexthw.eidolon_repraised.gui;

import alexthw.eidolon_repraised.Config;
import alexthw.eidolon_repraised.compat.CompatHandler;
import alexthw.eidolon_repraised.compat.apotheosis.ApotheosisCompat;
import alexthw.eidolon_repraised.datagen.EidEnchantmentTagProvider;
import alexthw.eidolon_repraised.registries.Registry;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IdMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static alexthw.eidolon_repraised.registries.EidolonDataComponents.SOUL_ENCHANT_USES;
import static net.neoforged.neoforge.common.CommonHooks.onPlayerEnchantItem;

public class SoulEnchanterContainer extends AbstractContainerMenu {

    private final Container tableInventory = new SimpleContainer(2) {
        public void setChanged() {
            super.setChanged();
            SoulEnchanterContainer.this.slotsChanged(this);
        }
    };

    private final ContainerLevelAccess worldPosCallable;
    private final RandomSource rand = RandomSource.create();
    private final DataSlot xpSeed = DataSlot.standalone();
    public final int[] enchantClue = new int[]{-1, -1, -1};
    public final int[] worldClue = new int[]{-1, -1, -1};

    public SoulEnchanterContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, ContainerLevelAccess.NULL);
    }

    public SoulEnchanterContainer(int id, Inventory playerInventory, ContainerLevelAccess worldPosCallable) {
        super(Registry.SOUL_ENCHANTER_CONTAINER.get(), id);
        this.worldPosCallable = worldPosCallable;
        this.addSlot(new Slot(this.tableInventory, 0, 15, 47) {
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.tableInventory, 1, 35, 47) {
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() == Registry.SOUL_SHARD.get();
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlot(this.xpSeed).set(playerInventory.player.getEnchantmentSeed());
        this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
        this.addDataSlot(DataSlot.shared(this.worldClue, 0));
        this.addDataSlot(DataSlot.shared(this.worldClue, 1));
        this.addDataSlot(DataSlot.shared(this.worldClue, 2));
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void slotsChanged(@NotNull Container inventoryIn) {
        if (inventoryIn == this.tableInventory) {
            ItemStack itemstack = inventoryIn.getItem(0);

            this.worldPosCallable.execute((world, pos) -> {
                        if (isValidItem(itemstack, world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT))) {

                            IdMap<Holder<Enchantment>> idmap = world.registryAccess().registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();

                            this.rand.setSeed(xpSeed.get());

                            for (int i1 = 0; i1 < 3; ++i1) {
                                enchantClue[i1] = -1;
                                worldClue[i1] = -1;
                            }

                            for (int j1 = 0; j1 < 3; ++j1) {
                                List<EnchantmentInstance> list = getEnchantmentList(world.registryAccess(), itemstack, j1);

                                if (!list.isEmpty()) {
                                    EnchantmentInstance enchantmentinstance = list.get(rand.nextInt(list.size()));
                                    enchantClue[j1] = idmap.getId(enchantmentinstance.enchantment);
                                    worldClue[j1] = enchantmentinstance.level;
                                }
                            }

                            this.broadcastChanges();
                        } else {
                            for (int i = 0; i < 3; ++i) {
                                this.enchantClue[i] = -1;
                                this.worldClue[i] = -1;
                            }
                        }
                    }
            );
        }
    }

    private boolean isValidItem(final ItemStack itemStack, HolderLookup.RegistryLookup<Enchantment> tHolderLookup) {
        return !itemStack.isEmpty() && canSoulEnchant(itemStack) && hasValidEnchantmentAmount(itemStack, tHolderLookup) && (itemStack.isEnchantable() || itemStack.isEnchanted() || itemStack.getItem() == Items.ENCHANTED_BOOK);
    }

    private boolean hasValidEnchantmentAmount(final ItemStack itemStack, HolderLookup.
            RegistryLookup<Enchantment> tHolderLookup) {
        if (Config.SOUL_ENCHANTER_MAXIMUM_ENCHANTMENTS.get() < 0) {
            return true;
        }

        return itemStack.getAllEnchantments(tHolderLookup).size() <= Config.SOUL_ENCHANTER_MAXIMUM_ENCHANTMENTS.get();
    }

    private void incrementSoulEnchant(final ItemStack enchantedItem) {
        if (Config.SOUL_ENCHANTER_MAXIMUM_USES.get() < 0) {
            return;
        }

        enchantedItem.set(SOUL_ENCHANT_USES, enchantedItem.getOrDefault(SOUL_ENCHANT_USES, 0) + 1);
    }

    private boolean canSoulEnchant(final ItemStack itemstack) {
        if (Config.SOUL_ENCHANTER_MAXIMUM_USES.get() < 0) {
            return true;
        }

        if (!itemstack.isComponentsPatchEmpty()) {
            int soulEnchantUses = itemstack.getOrDefault(SOUL_ENCHANT_USES, 0);
            return soulEnchantUses < Config.SOUL_ENCHANTER_MAXIMUM_USES.get();
        }

        return true;
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean clickMenuButton(@NotNull Player playerIn, int id) {
        ItemStack itemstack = this.tableInventory.getItem(0);

        if (!isValidItem(itemstack, playerIn.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT))) {
            return false;
        }

        ItemStack soulShards = this.tableInventory.getItem(1);
        int i = id + 1;
        // Texture only goes up to 5 - maybe need to dynamically render the level?
        int experienceLevelCost = Math.min(5, this.worldClue[id]);
        if ((soulShards.isEmpty() || soulShards.getCount() < 1) && !playerIn.getAbilities().instabuild) {
            return false;
        } else if (itemstack.isEmpty() || playerIn.experienceLevel < experienceLevelCost && !playerIn.getAbilities().instabuild) {
            return false;
        } else {
            this.worldPosCallable.execute((p_217003_6_, p_217003_7_) -> {
                ItemStack itemstack2 = itemstack;
                RegistryAccess registryAccess = p_217003_6_.registryAccess();
                List<EnchantmentInstance> list = this.getEnchantmentList(registryAccess, itemstack, id);
                if (!list.isEmpty()) {
                    playerIn.onEnchantmentPerformed(itemstack, experienceLevelCost);
                    itemstack2 = itemstack.getItem().applyEnchantments(itemstack, list);

                    this.tableInventory.setItem(0, itemstack2);

                    onPlayerEnchantItem(playerIn, itemstack2, list);

                    if (!playerIn.getAbilities().instabuild) {
                        incrementSoulEnchant(itemstack2);
                        soulShards.shrink(1);
                        if (soulShards.isEmpty()) {
                            this.tableInventory.setItem(1, ItemStack.EMPTY);
                        }
                    }

                    playerIn.awardStat(Stats.ENCHANT_ITEM);
                    if (playerIn instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) playerIn, itemstack2, i);
                    }

                    this.tableInventory.setChanged();
                    this.xpSeed.set(playerIn.getEnchantmentSeed());
                    this.slotsChanged(this.tableInventory);
                    p_217003_6_.playSound(null, p_217003_7_, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, p_217003_6_.random.nextFloat() * 0.1F + 0.7F);
                }
            });
            return true;
        }
    }

    private List<EnchantmentInstance> getEnchantmentList(RegistryAccess registryAccess, ItemStack stack, int enchantSlot) {
        this.rand.setSeed(this.xpSeed.get() + enchantSlot);
        ItemStack test = stack.getItem().getDefaultInstance();
        if (test.getItem() == Items.ENCHANTED_BOOK) test = new ItemStack(Items.BOOK);
        final ItemStack finalTest = test;

        ItemEnchantments existing = stack.getAllEnchantments(registryAccess.registryOrThrow(Registries.ENCHANTMENT).asLookup());
        Optional<HolderSet.Named<Enchantment>> optional = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (optional.isEmpty()) {
            return List.of();
        }
        var valid = Lists.newArrayList(optional.get());
        valid.removeIf(
                enchantment -> {
                    boolean failFast = enchantment == null || enchantment.is(EidEnchantmentTagProvider.SOUL_ENCHANTER_BLACKLIST);
                    if (failFast) return true;

                    int maxLevel = enchantment.value().getMaxLevel();

                    if (CompatHandler.isModLoaded(CompatHandler.APOTHEOSIS)) {
                        maxLevel = ApotheosisCompat.getMaxLevel(enchantment.value());
                    }

                    boolean canApply = finalTest.supportsEnchantment(enchantment) || finalTest.getItem() == Items.BOOK;

                    if (!canApply || enchantment.is(EnchantmentTags.CURSE)) {
                        return true;
                    }

                    return enchantment.is(EnchantmentTags.TREASURE) || existing.getLevel(enchantment) > 0 && existing.getLevel(enchantment) >= maxLevel;
                }
        );

        for (Object2IntMap.Entry<Holder<Enchantment>> e : existing.entrySet()) {
            if (valid.isEmpty()) break;
            valid.removeIf(next -> next == null || (e.getKey() != next && e.getKey().value().exclusiveSet().contains(next)));
        }

        List<EnchantmentInstance> enchants = new ArrayList<>();
        if (valid.isEmpty()) return enchants;
        // System.out.println(enchantSlot + ": " + valid.stream().reduce("", (a, b) -> a + ", " + b, (a, b) -> a + ", " + b));
        for (int i = 0; i < enchantSlot; i++) rand.nextInt(valid.size());
        Holder<Enchantment> enchant = valid.get(this.rand.nextInt(valid.size()));
        int level = Math.max(0, stack.getEnchantmentLevel(enchant));
        enchants.add(new EnchantmentInstance(enchant, level + 1));

        return enchants;
    }

    @OnlyIn(Dist.CLIENT)
    public int getSoulShardAmount() {
        ItemStack itemstack = this.tableInventory.getItem(1);
        return itemstack.isEmpty() ? 0 : itemstack.getCount();
    }

    @OnlyIn(Dist.CLIENT)
    public int getXPSeed() {
        return this.xpSeed.get();
    }

    /**
     * Called when the container is closed.
     */
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        this.worldPosCallable.execute((world, pos) -> this.clearContainer(playerIn, this.tableInventory));
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(this.worldPosCallable, playerIn, Registry.SOUL_ENCHANTER.get());
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 1) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.getItem() == Registry.SOUL_SHARD.get()) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.slots.getFirst().hasItem() || !this.slots.getFirst().mayPlace(itemstack1)) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemstack2 = itemstack1.copy();
                itemstack2.setCount(1);
                itemstack1.shrink(1);
                this.slots.getFirst().set(itemstack2);
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
