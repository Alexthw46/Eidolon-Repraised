package elucent.eidolon.common.tile;

import elucent.eidolon.Config;
import elucent.eidolon.client.particle.Particles;
import elucent.eidolon.network.CrucibleFailPacket;
import elucent.eidolon.network.CrucibleSuccessPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.recipe.CrucibleRecipe;
import elucent.eidolon.recipe.CrucibleRegistry;
import elucent.eidolon.registries.EidolonParticles;
import elucent.eidolon.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class CrucibleTileEntity extends TileEntityBase {
    boolean boiling = false;
    public boolean hasWater = false;
    int stirTicks = 0;
    int stirs = 0;
    int stepCounter = 0;
    final List<CrucibleStep> steps = new ArrayList<>();
    long seed = 0;
    final Random random = new Random();

    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> this.tank);

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

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return capability == ForgeCapabilities.FLUID_HANDLER ? this.holder.cast() : super.getCapability(capability, facing);
    }

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

        public CrucibleStep(CompoundTag nbt) {
            stirs = nbt.getInt("stirs");
            ListTag list = nbt.getList("contents", Tag.TAG_COMPOUND);
            for (Tag item : list) contents.add(ItemStack.of((CompoundTag) item));
        }

        public CompoundTag write() {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("stirs", stirs);
            ListTag list = new ListTag();
            for (ItemStack stack : contents) list.add(stack.save(new CompoundTag()));
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
    public InteractionResult onActivated(BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && level != null) {
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
                stirs++;
                stirTicks = 20;
                if (!level.isClientSide) {
                    level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f);
                    sync();
                }
                return InteractionResult.SUCCESS;
            }

        }
        return InteractionResult.PASS;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.steps.clear();
        ListTag steps = tag.getList("steps", Tag.TAG_COMPOUND);
        for (Tag step : steps) this.steps.add(new CrucibleStep((CompoundTag) step));
        boiling = tag.getBoolean("boiling");
        tank.readFromNBT(tag);
        hasWater = tank.getFluidAmount() == 1000;
        stirs = tag.getInt("stirs");
        stirTicks = tag.getInt("stirTicks");
        seed = steps.stream().map(Object::hashCode).reduce(0, (a, b) -> a ^ b);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        ListTag steps = new ListTag();
        for (CrucibleStep step : this.steps) steps.add(step.write());
        tag.put("steps", steps);
        tag.putBoolean("boiling", boiling);
        tag.putInt("stirs", stirs);
        tag.putInt("stirTicks", stirTicks);
        if (!tank.isEmpty()) {
            tank.writeToNBT(tag);
        }
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
        float bubbleR = !stepSize ? Math.min(1.0f, getRed() * 1.25f) : 0.25f;
        float bubbleG = !stepSize ? Math.min(1.0f, getGreen() * 1.25f) : 0.5f;
        float bubbleB = !stepSize ? Math.min(1.0f, getBlue() * 1.25f) : 1.0f;
        float steamR = !stepSize ? Math.min(1.0f, 1 - (float) Math.pow(1 - getRed(), 2)) : 1.0f;
        float steamG = !stepSize ? Math.min(1.0f, 1 - (float) Math.pow(1 - getGreen(), 2)) : 1.0f;
        float steamB = !stepSize ? Math.min(1.0f, 1 - (float) Math.pow(1 - getBlue(), 2)) : 1.0f;

        if (level.isClientSide && hasWater && boiling) for (int i = 0; i < 2; i++) {
            Particles.create(EidolonParticles.BUBBLE_PARTICLE)
                    .setScale(0.05f)
                    .setLifetime(10)
                    .addVelocity(0, 0.015625, 0)
                    .setColor(bubbleR, bubbleG, bubbleB)
                    .setAlpha(1.0f, 0.75f)
                    .spawn(level, worldPosition.getX() + 0.125 + 0.75 * level.random.nextFloat(), worldPosition.getY() + 0.6875, worldPosition.getZ() + 0.125 + 0.75 * level.random.nextFloat());
            if (level.random.nextInt(8) == 0) Particles.create(EidolonParticles.STEAM_PARTICLE)
                    .setAlpha(0.0625f, 0).setScale(0.375f, 0.125f).setLifetime(80)
                    .randomOffset(0.375, 0.125).randomVelocity(0.0125f, 0.025f)
                    .addVelocity(0, 0.05f, 0)
                    .setColor(steamR, steamG, steamB)
                    .spawn(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.625, worldPosition.getZ() + 0.5);
        }

        if (!level.isClientSide && boiling && hasWater && level.getGameTime() % 8 == 0) {
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).deflate(0.125));
            for (ItemEntity item : items) item.setPickUpDelay(20);
        }

        if (!level.isClientSide && stepCounter > 0) {
            if (--stepCounter == 0)
                if (Config.TURN_BASED_CRUCIBLE.get()) {
                    handleTurnBasedUpdate(steamR, steamG, steamB);
                } else {
                    handleTimedUpdate(steamR, steamG, steamB);
                }
        }

        if (stepCounter == 0 && (!stepSize || Config.TURN_BASED_CRUCIBLE.get()) && hasWater && boiling && level.getGameTime() % 100 == 0) {
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).deflate(0.125));
            if (!items.isEmpty()) {
                stepCounter = Config.CRUCIBLE_STEP_DURATION.get() / 2;
            }
        }
    }

    // TODO: Optionally cache the valid recipes for the current steps (after the first) to speedup the lookup,
    //  when only one is left it's possible to hint the player about the rest of the recipe
    private void handleTurnBasedUpdate(float steamR, float steamG, float steamB) {

        List<ItemStack> contents = tryConsumeItems();

        // Nothing at all to do here, no stirs and no content changes means we can just sleep for a bit
        if (stirs == 0 && contents.isEmpty()) {
            stepCounter = Config.CRUCIBLE_STEP_BACKOFF.get(); // checking every tick is overkill, and the player wouldn't have the time to drop and stir once the step timer is over
            return;
        }

        CrucibleStep step = new CrucibleStep(stirs, contents);

        // Reset stir state
        stirs = 0;

        // Current set of steps don't have any yield, so let's just forget this whole thing ever happened...
        if (!CrucibleRegistry.doStepsHaveSomeResult(steps)) {
            Networking.sendToTracking(level, worldPosition, new CrucibleFailPacket(worldPosition));
            steps.clear();
            boiling = false;
            drain(); //sync();
            return;
        } else {
            steps.add(step);
        }

        CrucibleRecipe recipe = CrucibleRegistry.find(steps);
        // Recipe has been completed, let's go!!
        if (recipe != null) {
            completeCraft(steamR, steamG, steamB, contents, recipe);
        } else { // Recipe hasn't been found, but this item is definitely at least part of the recipe so do the whole shabang
            level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f); // try continue
            stepCounter = Config.CRUCIBLE_STEP_DURATION.get();
            sync();
        }
    }

    private List<ItemStack> tryConsumeItems() {
        List<ItemStack> contents = new ArrayList<>();
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).deflate(0.125));
        for (ItemEntity item : items) {
            for (int i = 0; i < item.getItem().getCount(); i++) {
                ItemStack stack = item.getItem().copy();
                stack.setCount(1);
                contents.add(stack);
            }
            item.remove(RemovalReason.DISCARDED);
        }
        return contents;
    }

    private void handleTimedUpdate(float steamR, float steamG, float steamB) {
        List<ItemStack> contents = tryConsumeItems();
        if (stirs == 0 && contents.isEmpty()) { // no action done; end recipe
            Networking.sendToTracking(level, worldPosition, new CrucibleFailPacket(worldPosition));
            steps.clear();
            stirs = 0;
            boiling = false;
            drain(); //sync();
        } else {
            CrucibleStep step = new CrucibleStep(stirs, contents);
            steps.add(step);
            stirs = 0;

            CrucibleRecipe recipe = CrucibleRegistry.find(steps);
            if (recipe != null) { // if recipe found
                completeCraft(steamR, steamG, steamB, contents, recipe);
            } else {
                level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f); // try continue
                stepCounter = Config.CRUCIBLE_STEP_DURATION.get();
                sync();
            }
        }
    }


    private void completeCraft(float steamR, float steamG, float steamB, List<ItemStack> contents, CrucibleRecipe recipe) {
        Networking.sendToTracking(level, worldPosition, new CrucibleSuccessPacket(worldPosition, steamR, steamG, steamB));
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
}
