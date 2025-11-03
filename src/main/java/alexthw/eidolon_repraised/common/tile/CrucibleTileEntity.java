package alexthw.eidolon_repraised.common.tile;

import alexthw.eidolon_repraised.Config;
import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.network.CrucibleFailPacket;
import alexthw.eidolon_repraised.network.CrucibleSuccessPacket;
import alexthw.eidolon_repraised.network.Networking;
import alexthw.eidolon_repraised.recipe.CrucibleHelper;
import alexthw.eidolon_repraised.recipe.CrucibleRecipe;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class CrucibleTileEntity extends TileEntityBase implements Container {
    boolean boiling = false;
    public boolean hasWater = false;

    public int getStirs() {
        return stirs;
    }

    public int getStirTicks() {
        return stirTicks;
    }

    public List<CrucibleStep> getSteps() {
        return steps;
    }

    int stirTicks = 0;
    int stirs = 0;
    int stepCounter = 0;
    final List<CrucibleStep> steps = new ArrayList<>();
    List<ItemStack> currentStepContents = NonNullList.withSize(9, ItemStack.EMPTY);
    long seed = 0;
    final Random random = new Random();

    //internal tank that can hold one water bucket
    public FluidTank tank = new FluidTank(1000) {

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(Fluids.WATER);
        }

        @Override
        public void onContentsChanged() {
            super.onContentsChanged();
            if (level != null) {
                hasWater = getFluid().getAmount() == 1000;
                if (!level.isClientSide) sync();
            }
        }

    };

    public float getRed() {
        random.setSeed(seed);
        return random.nextFloat();
    }

    public float getGreen() {
        random.setSeed(seed * 2);
        return random.nextFloat();
    }

    public float getBlue() {
        random.setSeed(seed * 3);
        return random.nextFloat();
    }

    public void fill() {
        tank.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
    }

    private void drain() {
        this.clearContent();
        this.tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
    }

    public static class CrucibleStep {
        final int stirs;
        final List<ItemStack> contents = new ArrayList<>();

        public CrucibleStep(int stirs, List<ItemStack> contents) {
            this.stirs = stirs;
            this.contents.addAll(contents);
        }

        public int getStirs() {
            return stirs;
        }

        public List<ItemStack> getContents() {
            return contents;
        }

        public CrucibleStep(CompoundTag nbt, HolderLookup.Provider provider) {
            stirs = nbt.getInt("stirs");
            ListTag list = nbt.getList("contents", Tag.TAG_COMPOUND);
            for (Tag item : list) contents.add(ItemStack.parseOptional(provider, (CompoundTag) item));
        }

        public CompoundTag write(HolderLookup.Provider provider) {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("stirs", stirs);
            ListTag list = new ListTag();
            for (ItemStack stack : contents) list.add(stack.save(provider, new CompoundTag()));
            nbt.put("contents", list);
            return nbt;
        }
    }

    public static final List<Predicate<BlockState>> HOT_BLOCKS = new ArrayList<>(List.of(
            (BlockState b) -> b.is(Registry.CRUCIBLE_HOT_BLOCKS),
            (BlockState b) -> b.getBlock() instanceof CampfireBlock && b.hasProperty(CampfireBlock.LIT) && b.getValue(CampfireBlock.LIT)));

    public CrucibleTileEntity(BlockPos pos, BlockState state) {
        this(Registry.CRUCIBLE_TILE_ENTITY.get(), pos, state);
    }

    public CrucibleTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }


    @Override
    public InteractionResult onActivated(BlockState state, BlockPos pos, Player player) {
        InteractionHand hand = player.getUsedItemHand();
        if (level != null) {
            if (FluidUtil.interactWithFluidHandler(player, hand, this.tank))
                return InteractionResult.SUCCESS;

            if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty() && hasWater) {
                boiling = false;
                stirs = 0;
                steps.clear();
                if (!level.isClientSide) {
                    drain(); //sync();
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                return InteractionResult.SUCCESS;
            } else if (player.getItemInHand(hand).isEmpty() && stirTicks == 0 && !this.steps.isEmpty()) {
                stir(pos);
                return InteractionResult.SUCCESS;
            }

        }
        return InteractionResult.PASS;
    }

    public void stir(BlockPos pos) {
        stirs++;
        stirTicks = 20;
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f);
            sync();
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.steps.clear();
        ListTag steps = tag.getList("steps", Tag.TAG_COMPOUND);
        for (Tag step : steps) this.steps.add(new CrucibleStep((CompoundTag) step, provider));
        boiling = tag.getBoolean("boiling");
        tank.readFromNBT(provider, tag);
        hasWater = tank.getFluidAmount() == 1000;
        stirs = tag.getInt("stirs");
        stirTicks = tag.getInt("stirTicks");
        seed = steps.stream().map(Object::hashCode).reduce(0, (a, b) -> a ^ b);
        currentStepContents.clear();
        ListTag currentContents = tag.getList("currentContents", Tag.TAG_COMPOUND);
        int i = 0;
        for (Tag item : currentContents) {
            ItemStack stack = ItemStack.parseOptional(provider, (CompoundTag) item);
            if (!stack.isEmpty() && i < currentStepContents.size()) {
                currentStepContents.set(i, stack);
            }
            i++;
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ListTag steps = new ListTag();
        for (CrucibleStep step : this.steps) steps.add(step.write(provider));
        tag.put("steps", steps);
        tag.putBoolean("boiling", boiling);
        tag.putInt("stirs", stirs);
        tag.putInt("stirTicks", stirTicks);
        if (!tank.isEmpty()) {
            tank.writeToNBT(provider, tag);
        }
        var currentContents = new ListTag();
        for (ItemStack stack : currentStepContents) {
            if (!stack.isEmpty()) {
                currentContents.add(stack.save(provider, new CompoundTag()));
            }
        }
        tag.put("currentContents", currentContents);
    }

    public void tick() {
        if (stirTicks > 0) stirTicks--;
        if (level == null) return;
        if (hasWater && level.getGameTime() % 200 == 0) {
            BlockState state = level.getBlockState(worldPosition.below());
            boolean isHeated = false;
            for (Predicate<BlockState> pred : HOT_BLOCKS)
                if (pred.test(state)) {
                    isHeated = true;
                    break;
                }
            if (boiling && !isHeated) {
                boiling = false;
                if (!level.isClientSide) sync();
            } else if (!boiling && isHeated) {
                boiling = true;
                if (!level.isClientSide) sync();
            }
        }

        boolean stepSize = steps.isEmpty();

        float steamR = !stepSize ? Math.min(1.0f, 1 - (float) Math.pow(1 - getRed(), 2)) : 1.0f;
        float steamG = !stepSize ? Math.min(1.0f, 1 - (float) Math.pow(1 - getGreen(), 2)) : 1.0f;
        float steamB = !stepSize ? Math.min(1.0f, 1 - (float) Math.pow(1 - getBlue(), 2)) : 1.0f;

        if (level.isClientSide && hasWater && boiling) {
            float bubbleR = !stepSize ? Math.min(1.0f, getRed() * 1.25f) : 0.25f;
            float bubbleG = !stepSize ? Math.min(1.0f, getGreen() * 1.25f) : 0.5f;
            float bubbleB = !stepSize ? Math.min(1.0f, getBlue() * 1.25f) : 1.0f;

            for (int i = 0; i < 2; i++) {
                Particles.create(EidolonParticles.BUBBLE_PARTICLE.get())
                        .setScale(0.05f)
                        .setLifetime(10)
                        .addVelocity(0, 0.015625, 0)
                        .setColor(bubbleR, bubbleG, bubbleB)
                        .setAlpha(1.0f, 0.75f)
                        .spawn(level, worldPosition.getX() + 0.125 + 0.75 * level.random.nextFloat(), worldPosition.getY() + 0.6875, worldPosition.getZ() + 0.125 + 0.75 * level.random.nextFloat());
                if (level.random.nextInt(8) == 0) Particles.create(EidolonParticles.STEAM_PARTICLE.get())
                        .setAlpha(0.0625f, 0).setScale(0.375f, 0.125f).setLifetime(80)
                        .randomOffset(0.375, 0.125).randomVelocity(0.0125f, 0.025f)
                        .addVelocity(0, 0.05f, 0)
                        .setColor(steamR, steamG, steamB)
                        .spawn(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.625, worldPosition.getZ() + 0.5);
            }
        }

        if (!level.isClientSide && boiling && hasWater && level.getGameTime() % 8 == 0) {
            // Hopper-like behavior: try to pick up items from inside the crucible
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).deflate(0.125));
            for (ItemEntity entity : items) {
                ItemStack stack = entity.getItem();
                if (stack.isEmpty() || entity.isRemoved()) continue;

                for (int i = 0; i < currentStepContents.size(); i++) {
                    ItemStack slot = currentStepContents.get(i);
                    if (slot.isEmpty()) {
                        currentStepContents.set(i, stack.copy());
                        entity.discard();
                        break;
                    }
                }
            }
        }

        if (!level.isClientSide && stepCounter > 0) {
            if (--stepCounter == 0)
                if (Config.TURN_BASED_CRUCIBLE.get()) {
                    handleTurnBasedUpdate(steamR, steamG, steamB);
                } else {
                    handleTimedUpdate(steamR, steamG, steamB);
                }
        }

        if (stepCounter == 0 && (stepSize || Config.TURN_BASED_CRUCIBLE.get()) && hasWater && boiling && level.getGameTime() % 100 == 0) {
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).deflate(0.125));
            if (!items.isEmpty() || !this.isEmpty()) {
                stepCounter = Config.CRUCIBLE_STEP_DURATION.get() / 2;
            }
        }
    }

    private List<ItemStack> consumeFromInventory() {
        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < currentStepContents.size(); i++) {
            ItemStack stack = currentStepContents.get(i).copy();
            if (!stack.isEmpty()) {
                while (!stack.isEmpty())
                    contents.add(stack.split(1));
                currentStepContents.set(i, ItemStack.EMPTY);
            }
        }
        return contents;
    }

    private void handleTimedUpdate(float steamR, float steamG, float steamB) {
        List<ItemStack> contents = consumeFromInventory();
        if (level == null || level.isClientSide) return;
        if (stirs == 0 && contents.isEmpty()) {
            Networking.sendToNearbyClient(level, worldPosition, new CrucibleFailPacket(worldPosition));
            steps.clear();
            stirs = 0;
            boiling = false;
            drain();
        } else {
            CrucibleStep step = new CrucibleStep(stirs, contents);
            steps.add(step);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            stirs = 0;

            // try to find a finished recipe with the current steps
            CrucibleRecipe recipe = CrucibleHelper.find(level, steps);
            if (recipe != null) {
                completeCraft(steamR, steamG, steamB, contents, recipe);
            } else {
                // no recipe found, try to advance to the next step
                level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f);
                stepCounter = Config.CRUCIBLE_STEP_DURATION.get();
                sync();
            }
        }
    }

    private void handleTurnBasedUpdate(float steamR, float steamG, float steamB) {
        if (level == null || level.isClientSide) return;

        // Peek at contents (non-destructive)
        List<ItemStack> snapshot = new ArrayList<>();
        for (ItemStack stack : currentStepContents) {
            if (!stack.isEmpty()) snapshot.add(stack.copy());
        }

        // Nothing changed — don't progress step timer
        if (stirs == 0 && snapshot.isEmpty()) {
            stepCounter = Config.CRUCIBLE_STEP_BACKOFF.get();
            return;
        }

        CrucibleStep trialStep = new CrucibleStep(stirs, snapshot);
        List<CrucibleStep> trialSteps = new ArrayList<>(steps);
        trialSteps.add(trialStep);

        // Check if this hypothetical step has potential
        if (!CrucibleHelper.doStepsHaveSomeResult(level, trialSteps)) {
            Networking.sendToNearbyClient(level, worldPosition, new CrucibleFailPacket(worldPosition));
            steps.clear();
            boiling = false;
            drain();
            return;
        }

        // Step is valid — now we consume the inventory
        List<ItemStack> consumed = consumeFromInventory();
        CrucibleStep finalStep = new CrucibleStep(stirs, consumed);
        steps.add(finalStep);
        level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());

        stirs = 0;

        CrucibleRecipe recipe = CrucibleHelper.find(level, steps);
        if (recipe != null) {
            completeCraft(steamR, steamG, steamB, consumed, recipe);
        } else {
            level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f);
            stepCounter = Config.CRUCIBLE_STEP_DURATION.get();
            sync();
        }
    }

    private void completeCraft(float steamR, float steamG, float steamB, List<ItemStack> contents, CrucibleRecipe recipe) {
        Networking.sendToNearbyClient(level, worldPosition, new CrucibleSuccessPacket(worldPosition, steamR, steamG, steamB));
        double angle = level.random.nextDouble() * Math.PI * 2;
        ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.75, worldPosition.getZ() + 0.5, recipe.getResult().copy());
        entity.setDeltaMovement(Math.sin(angle) * 0.125, 0.25, Math.cos(angle) * 0.125);
        entity.setPickUpDelay(10);
        level.addFreshEntity(entity);
        contents.clear();
        steps.clear();
        boiling = false;
        drain();
    }

    @Override
    public int getContainerSize() {
        return hasWater ? 9 : 0; // Crucible can only hold items when it has water
    }

    @Override
    public boolean isEmpty() {
        return currentStepContents.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return currentStepContents.get(i);
    }

    @Override
    public @NotNull ItemStack removeItem(int i, int amount) {
        if (i >= currentStepContents.size()) return ItemStack.EMPTY;

        ItemStack existing = currentStepContents.get(i);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        ItemStack removed = existing.split(amount);
        if (existing.isEmpty()) {
            currentStepContents.set(i, ItemStack.EMPTY);
        }
        sync();
        return removed;
    }


    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        var itemStack = currentStepContents.get(i);
        currentStepContents.set(i, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack) {
        if (i < currentStepContents.size()) currentStepContents.set(i, itemStack);
        sync();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        currentStepContents.clear();
        sync();
    }

}
