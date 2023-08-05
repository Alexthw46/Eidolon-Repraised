package elucent.eidolon.api.research;

import elucent.eidolon.mixin.AbstractContainerMenuMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public abstract class ResearchTask {
    public abstract CompoundTag write();

    public abstract void read(CompoundTag tag);

    public abstract CompletenessResult isComplete(AbstractContainerMenu menu, Player player, int slotStart);

    public abstract void onComplete(AbstractContainerMenu menu, Player player, int slotStart);

    @OnlyIn(Dist.CLIENT)
    public abstract int getWidth();

    @OnlyIn(Dist.CLIENT)
    public abstract void drawIcon(GuiGraphics stack, ResourceLocation texture, int x, int y);

    @OnlyIn(Dist.CLIENT)
    public int drawCustom(@NotNull GuiGraphics stack, ResourceLocation texture, int x, int y) {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getDefaultWidth() {
        return 64;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawTooltip(@NotNull GuiGraphics stack, AbstractContainerScreen<?> gui, double mouseX, double mouseY) {
    }

    public record CompletenessResult(int nextSlot, boolean complete) {
    }

    public int getSlotCount() {
        return 0;
    }

    public void modifyContainer(AbstractContainerMenu menu, int x, int y) {
    }

    public static class TaskItems extends ResearchTask {
        List<ItemStack> items;
        final InternalContainer container;

        class InternalContainer implements Container {
            final List<ItemStack> items = new ArrayList<>();

            protected void updateItems() {
                while (items.size() < TaskItems.this.items.size()) items.add(ItemStack.EMPTY);
            }

            @Override
            public void clearContent() {
                items.clear();
            }

            @Override
            public int getContainerSize() {
                return TaskItems.this.items.size();
            }

            @Override
            public boolean isEmpty() {
                for (ItemStack s : items) if (!s.isEmpty()) return false;
                return true;
            }

            @Override
            public @NotNull ItemStack getItem(int slot) {
                updateItems();
                return items.get(slot);
            }

            @Override
            public @NotNull ItemStack removeItem(int slot, int amount) {
                updateItems();
                ItemStack stack = ContainerHelper.removeItem(this.items, slot, amount);
                if (!stack.isEmpty()) {
                    this.setChanged();
                }
                return stack;
            }

            @Override
            public @NotNull ItemStack removeItemNoUpdate(int slot) {
                updateItems();
                ItemStack itemstack = this.items.get(slot);
                if (itemstack.isEmpty()) {
                    return ItemStack.EMPTY;
                } else {
                    this.items.set(slot, ItemStack.EMPTY);
                    return itemstack;
                }
            }

            @Override
            public void setItem(int slot, @NotNull ItemStack stack) {
                updateItems();
                items.set(slot, stack);
            }

            @Override
            public void setChanged() {
                // stub. should this be implemented?
            }

            @Override
            public boolean stillValid(@NotNull Player p_18946_) {
                return true;
            }
        }

        public TaskItems(ItemStack... stacks) {
            this.items = List.of(stacks);
            this.container = new InternalContainer();
        }

        public TaskItems(List<ItemStack> stacks) {
            this.items = stacks;
            this.container = new InternalContainer();
        }

        public static Function<Random, ResearchTask> fromTag(TagKey<Item> tagKey, int maxCount) {
            return (random) -> {
                var items = List.of(Ingredient.of(tagKey).getItems());
                for (var item : items) {
                    if (item.isStackable())
                        item.setCount(random.nextInt(1, maxCount));
                }
                return new TaskItems(items.get(random.nextInt(items.size())));
            };
        }

        @Override
        public CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            tag.put("stacks", new ListTag());
            return items.stream().map(s -> s.save(new CompoundTag())).reduce(tag, (t, s) -> {
                t.getList("stacks", Tag.TAG_COMPOUND).add(s);
                return t;
            });
        }

        @Override
        public void read(CompoundTag tag) {
            ListTag list = tag.getList("stacks", Tag.TAG_COMPOUND);
            for (Tag t : list) {
                items.add(ItemStack.of((CompoundTag) t));
            }
        }

        @Override
        public int getWidth() {
            return getDefaultWidth() + 8 + 17 * items.size();
        }

        @Override
        public void drawIcon(GuiGraphics stack, ResourceLocation texture, int x, int y) {
            int offset = (items.size() - 1) * -4;
            ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
            for (int i = 0; i < items.size(); i++) {
                stack.renderItem(items.get(i), x + i * 8 + offset, y);
                stack.renderItemDecorations(Minecraft.getInstance().font, items.get(i), x + i * 8 + offset, y, null);
            }
        }

        @Override
        public int drawCustom(@NotNull GuiGraphics guiGraphics, ResourceLocation texture, int x, int y) {
            guiGraphics.blit(texture, x, y, 0, 88, 224, 1, 32, 256, 256);
            x += 1;
            for (int i = 0; i < items.size(); i++) {
                if (items.size() == 1) {
                    guiGraphics.blit(texture, x, y, 0, 192, 0, 22, 32, 256, 256);
                    x += 22;
                } else if (i == 0) {
                    guiGraphics.blit(texture, x, y, 0, 192, 32, 20, 32, 256, 256);
                    x += 19;
                } else if (i == items.size() - 1) {
                    guiGraphics.blit(texture, x, y, 0, 228, 32, 20, 32, 256, 256);
                    x += 20;
                } else {
                    guiGraphics.blit(texture, x, y, 0, 211, 32, 18, 32, 256, 256);
                    x += 17;
                }
            }
            guiGraphics.blit(texture, x, y, 0, 88, 224, 2, 32, 256, 256);
            x += 2;
            return 8 + 17 * items.size();
        }

        @Override
        public void drawTooltip(@NotNull GuiGraphics stack, AbstractContainerScreen<?> gui, double mouseX, double mouseY) {
            List<Component> tooltip = gui.getTooltipFromItem(gui.getMinecraft(), items.get(0));
            stack.renderComponentTooltip(Minecraft.getInstance().font, tooltip, (int) mouseX, (int) mouseY);
        }

        @Override
        public int getSlotCount() {
            return items.size();
        }

        @Override
        public void modifyContainer(AbstractContainerMenu menu, int x, int y) {
            for (int i = 0; i < items.size(); i++) {
                var stack = items.get(i);
                ((AbstractContainerMenuMixin) menu).callAddSlot(new Slot(container, i, x + 11 + 17 * i, y + 7) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack pStack) {
                        return ItemStack.isSameItemSameTags(pStack, stack);
                    }
                });
            }
        }

        @Override
        public CompletenessResult isComplete(AbstractContainerMenu menu, Player player, int slotStart) {
            boolean isMatching = true;
            for (int i = 0; i < items.size() && isMatching; i++) {
                if (menu.getItems().size() <= slotStart + i) {
                    isMatching = false;
                    continue;
                }
                ItemStack slot = menu.getSlot(slotStart + i).getItem();
                if (!ItemStack.isSameItemSameTags(items.get(i), slot)) isMatching = false;
                if (slot.getCount() < items.get(i).getCount()) isMatching = false;
            }
            return new CompletenessResult(slotStart + items.size(), isMatching);
        }

        @Override
        public void onComplete(AbstractContainerMenu menu, Player player, int slotStart) {
            for (int i = 0; i < items.size(); i++) {
                menu.getSlot(slotStart + i).remove(items.get(i).getCount());
            }
        }
    }

    public static class XP extends ResearchTask {
        int levels;

        public XP(Random random) {
            levels = random.nextInt(1, 6); // 1-5 XP levels as cost
        }

        public XP(int i) {
            levels = i;
        }

        @Override
        public CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("levels", levels);
            return tag;
        }

        @Override
        public void read(CompoundTag tag) {
            this.levels = tag.getInt("levels");
        }

        @Override
        public int getWidth() {
            return getDefaultWidth();
        }

        @Override
        public void drawIcon(GuiGraphics stack, ResourceLocation texture, int x, int y) {
            int offY = Minecraft.getInstance().player.experienceLevel < levels ? 16 : 0;
            stack.blit(texture, x, y, 0, (levels - 1) * 16, 224 + offY, 16, 16, 256, 256);
        }

        @Override
        public CompletenessResult isComplete(AbstractContainerMenu menu, Player player, int slotStart) {
            return new CompletenessResult(slotStart, player.experienceLevel >= levels);
        }

        @Override
        public void drawTooltip(@NotNull GuiGraphics stack, AbstractContainerScreen<?> gui, double mouseX, double mouseY) {
            MutableComponent tooltip;
            if (levels == 1) {
                tooltip = Component.translatable("container.enchant.level.one");
            } else {
                tooltip = Component.translatable("container.enchant.level.many", levels);
            }

            stack.renderTooltip(Minecraft.getInstance().font, tooltip, (int) mouseX, (int) mouseY);
        }

        @Override
        public void onComplete(AbstractContainerMenu menu, Player player, int slotStart) {
            player.giveExperienceLevels(-levels);
        }
    }
}
