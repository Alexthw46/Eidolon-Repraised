package alexthw.eidolon_repraised.common.tile;

import alexthw.eidolon_repraised.api.ritual.IRitualItemFocus;
import alexthw.eidolon_repraised.api.ritual.IRitualItemProvider;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.api.ritual.Ritual.RitualResult;
import alexthw.eidolon_repraised.api.ritual.Ritual.SetupResult;
import alexthw.eidolon_repraised.client.particle.Particles;
import alexthw.eidolon_repraised.common.block.SingleItemTile;
import alexthw.eidolon_repraised.network.*;
import alexthw.eidolon_repraised.recipe.RitualRecipe;
import alexthw.eidolon_repraised.registries.EidolonParticles;
import alexthw.eidolon_repraised.registries.EidolonRecipes;
import alexthw.eidolon_repraised.registries.Registry;
import alexthw.eidolon_repraised.registries.RitualRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BrazierTileEntity extends SingleItemTile implements IBurner, RecipeInput {
    boolean burning = false;
    int findingCounter = 0;
    int stepCounter = 0;
    Ritual ritual = null;
    int step = 0;
    boolean ritualDone = false;

    public BrazierTileEntity(BlockPos pos, BlockState state) {
        this(Registry.BRAZIER_TILE_ENTITY.get(), pos, state);
    }

    public BrazierTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onDestroyed(BlockState state, BlockPos pos) {
        super.onDestroyed(state, pos);
        if (!stack.isEmpty() && level != null)
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
    }

    @Override
    public ItemInteractionResult onActivated(BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && level != null) {
            if (burning && player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
                extinguish();
                return ItemInteractionResult.SUCCESS;
            } else if (!burning && player.getItemInHand(hand).isEmpty() && !stack.isEmpty()) {
                player.addItem(stack);
                stack = ItemStack.EMPTY;
                if (!level.isClientSide) sync();
                return ItemInteractionResult.SUCCESS;
            } else {
                boolean canBurn = canStartBurning();
                if (canBurn
                    && player.getItemInHand(hand).getItem() instanceof FlintAndSteelItem) {
                    player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                    startBurning();
                    return ItemInteractionResult.SUCCESS;
                } else if (!player.getItemInHand(hand).isEmpty() && stack.isEmpty()) {
                    stack = player.getItemInHand(hand).copy();
                    stack.setCount(1);
                    player.getItemInHand(hand).shrink(1);
                    if (player.getItemInHand(hand).isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                    if (!level.isClientSide) sync();
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public boolean canStartBurning() {
        return !burning && !stack.isEmpty();
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        burning = tag.getBoolean("burning");

        step = tag.getInt("step");
        ritualDone = tag.getBoolean("ritualDone");
        //sync if there is a ritual running
        if (burning && tag.contains("ritual")) {
            var rid = ResourceLocation.tryParse(tag.getString("ritual"));
            //try match with classic Rituals
            ritual = RitualRegistry.find(rid);
            //try match with other recipes
            if (ritual == null && level != null)
                getRitualRecipes(level).stream().filter(r -> r.getId().equals(rid)).findFirst().ifPresent(r -> ritual = r.getRitual());
        } else ritual = null;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("burning", burning);
        if (ritual != null) tag.putString("ritual", ritual.getRegistryName().toString());
        tag.putInt("step", step);
        tag.putBoolean("ritualDone", ritualDone);
    }

    protected void complete() {
        burning = false;
        stepCounter = 0;
        findingCounter = 0;
        if (level != null && !level.isClientSide) {
            if (ritual != null)
                Networking.sendToNearbyClient(level, worldPosition.above(2), new RitualCompletePacket(worldPosition.above(2), ritual.getRed(), ritual.getGreen(), ritual.getBlue()));
            ritual = null;
            Networking.sendToNearbyClient(level, worldPosition, new ExtinguishEffectPacket(worldPosition));
            sync();
        }
        ritual = null;
    }

    public void extinguish() {
        burning = false;
        stepCounter = 0;
        findingCounter = 0;
        if (level != null && !level.isClientSide) {
            if (ritual != null)
                Networking.sendToNearbyClient(level, worldPosition.above(2), new FlameEffectPacket(worldPosition.above(2), ritual.getRed(), ritual.getGreen(), ritual.getBlue()));
            ritual = null;
            Networking.sendToNearbyClient(level, worldPosition, new ExtinguishEffectPacket(worldPosition));
            sync();
        }
        ritual = null;
    }

    @Override
    public void startBurning(Player player, @NotNull Level world, BlockPos pos) {
        startBurning();
    }

    public void startBurning() {
        burning = true;
        findingCounter = 0;
        if (level != null && !level.isClientSide) {
            Networking.sendToNearbyClient(level, worldPosition, new IgniteEffectPacket(worldPosition, 1.0f, 0.5f, 0.25f));
            sync();
        }
    }

    protected void setRitual(Ritual ritual) {
        this.ritual = ritual;
        if (ritual == null) extinguish();
        else {
            stepCounter = 0;
            step = 0;
            ritualDone = false;
            if (level != null && !level.isClientSide) {
                Networking.sendToNearbyClient(level, worldPosition.above(2), new FlameEffectPacket(worldPosition.above(2), ritual.getRed(), ritual.getGreen(), ritual.getBlue()));
                sync();
            }
        }
    }

    public void tick() {
        if (level == null) return;
        if (burning && findingCounter < 80 && ritual == null) {
            float progress = (findingCounter - 40) / 40.0f;
            if (progress >= 0) for (int i = 0; i < 8; i++) {
                float angle = progress * (float) Math.PI / 4 + i * (float) Math.PI / 4;
                float radius = 0.625f * Mth.sin(4 * angle);
                angle += (float) Math.PI / 4;
                double x = getBlockPos().getX() + 0.5 + Mth.sin(angle) * radius;
                double y = getBlockPos().getY() + 0.875;
                double z = getBlockPos().getZ() + 0.5 + Mth.cos(angle) * radius;
                Particles.create(EidolonParticles.WISP_PARTICLE.get())
                        .setAlpha(0.25f * progress, 0).setScale(0.125f, 0.0625f).setLifetime(20)
                        .setColor(1.0f, 0.5f, 0.25f, 1.0f, 0.25f, 0.375f)
                        .spawn(level, x, y, z);
            }
            findingCounter++;
            if (findingCounter == 80) {
                List<RitualRecipe> recipes = getRitualRecipes(level);
                recipes.stream().filter(recipe -> recipe.matches(this, level)).findFirst().ifPresentOrElse(recipe -> {
                    setRitual(recipe.getRitualWithRequirements());
                    stack = ItemStack.EMPTY;
                    findingCounter = 81;
                }, () -> {
                    ritualDone = true;
                    extinguish();
                });
            }
        }
        if (burning && ritual != null && !ritualDone) {
            stepCounter++;
            if (stepCounter == 40) {
                SetupResult result = ritual.setup(level, worldPosition, step);
                if (result == SetupResult.SUCCEED) {
                    ritualDone = true;
                    if (!level.isClientSide) sync();
                    if (ritual.start(level, worldPosition) == RitualResult.TERMINATE) complete();
                } else if (result == SetupResult.FAIL && !level.isClientSide) extinguish();
                else if (!level.isClientSide) {
                    stepCounter = 0;
                    step++;
                    sync();
                }
            }
        }
        if (burning && ritual != null && ritualDone) {
            if (ritual.tick(level, worldPosition) == RitualResult.TERMINATE) complete();
        }
        if (level.isClientSide && burning) {
            double x = getBlockPos().getX() + 0.5, y = getBlockPos().getY() + 1.0, z = getBlockPos().getZ() + 0.5;
            float r = ritual == null ? 1.0f : ritual.getRed();
            float g = ritual == null ? 0.5f : ritual.getGreen();
            float b = ritual == null ? 0.25f : ritual.getBlue();
            Particles.create(EidolonParticles.FLAME_PARTICLE.get())
                    .setAlpha(0.5f, 0).setScale(0.3125f, 0.125f).setLifetime(20)
                    .randomOffset(0.25, 0.125).randomVelocity(0.00625f, 0.01875f)
                    .addVelocity(0, 0.00625f, 0)
                    .setColor(r, g, b, r, g * 0.5f, b * 1.5f)
                    .spawn(level, x, y, z);
            if (level.random.nextInt(5) == 0) Particles.create(EidolonParticles.SMOKE_PARTICLE.get())
                    .setAlpha(0.125f, 0).setScale(0.375f, 0.125f).setLifetime(80)
                    .randomOffset(0.25, 0.125).randomVelocity(0.025f, 0.025f)
                    .addVelocity(0, 0.1f, 0)
                    .setColor(0.5f, 0.5f, 0.5f, 0.25f, 0.25f, 0.25f)
                    .spawn(level, x, y + 0.125, z);
            if (level.random.nextInt(40) == 0) Particles.create(EidolonParticles.SPARKLE_PARTICLE.get())
                    .setAlpha(1, 0).setScale(0.0625f, 0).setLifetime(40)
                    .randomOffset(0.0625, 0).randomVelocity(0.125f, 0)
                    .addVelocity(0, 0.125f, 0)
                    .setColor(r, g * 1.5f, b * 2, r, g, b)
                    .enableGravity().setSpin(0.4f)
                    .spawn(level, x, y, z);
        }
    }

    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 1, worldPosition.getX() + 1, worldPosition.getY() + 4, worldPosition.getZ() + 1);
    }

    public void providePedestalItems(List<ItemStack> pedestalItems, List<ItemStack> focusItems) {
        Ritual.getTilesWithinAABB(IRitualItemProvider.class, level, Ritual.getDefaultBounds(this.worldPosition)).forEach(tile -> {
            if (tile instanceof IRitualItemFocus provider) {
                focusItems.add(provider.provide());
            } else {
                pedestalItems.add(tile.provide());
            }
        });
        //clean up empty stacks
        focusItems.removeIf(ItemStack::isEmpty);
        pedestalItems.removeIf(ItemStack::isEmpty);
    }

    public static List<RitualRecipe> getRitualRecipes(Level world) {
        List<RitualRecipe> recipes = new ArrayList<>();
        RecipeManager manager = world.getRecipeManager();
        for (RecipeType<? extends RitualRecipe> type : EidolonRecipes.ritualRecipeTypes) {
            recipes.addAll(manager.getAllRecipesFor(type).stream().map(RecipeHolder::value).toList());
        }
        return recipes;
    }


    @Override
    public int size() {
        return 1;
    }
}
