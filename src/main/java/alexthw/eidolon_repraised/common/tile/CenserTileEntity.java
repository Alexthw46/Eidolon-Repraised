package alexthw.eidolon_repraised.common.tile;

import alexthw.eidolon_repraised.api.ritual.IncenseRitual;
import alexthw.eidolon_repraised.registries.IncenseRegistry;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class CenserTileEntity extends ContainerTileBase implements IBurner {
    public CenserTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    boolean isBurning;
    int burnCounter;
    IncenseRitual incenseRitual;

    public CenserTileEntity(BlockPos pos, BlockState state) {
        this(Registry.CENSER_TILE_ENTITY.get(), pos, state);
    }

    public boolean canStartBurning() {
        return !isBurning && !stack.isEmpty();
    }

    @Override
    public void onDestroyed(BlockState state, BlockPos pos) {
        super.onDestroyed(state, pos);
        if (!isBurning && !stack.isEmpty() && level != null) {
            // drop the incense item if the censer is destroyed
            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
    }

    public void tick() {
        if (level == null) return;
        if (!level.isClientSide && isBurning && incense() != null) {
            burnCounter++;
            this.incense().tick(burnCounter);
            sync();
        }
        if (burnCounter == 80) {
            stack = ItemStack.EMPTY;
            sync();
        }
        if (level.isClientSide && isBurning && incense() != null) {
            incenseRitual.animateParticles(this, burnCounter);
        }
    }

    @Override
    public ItemInteractionResult onActivated(BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && level instanceof ServerLevel && !isBurning) {
            ItemStack itemInHand = player.getItemInHand(hand);
            if (itemInHand.isEmpty() && !stack.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                stack = ItemStack.EMPTY;
                if (!level.isClientSide) sync();
                return ItemInteractionResult.SUCCESS;
            } else if (!itemInHand.isEmpty() && stack.isEmpty()) {
                if (IncenseRegistry.getIncenseRitual(itemInHand.getItem()) != null) {
                    stack = itemInHand.split(1);
                    if (!level.isClientSide) sync();
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (!itemInHand.isEmpty() && !stack.isEmpty()) {
                if (itemInHand.getItem() instanceof FlintAndSteelItem) {
                    if (!level.isClientSide && canStartBurning()) this.startBurning(player, level, pos);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void startBurning(Player player, @NotNull Level world, BlockPos pos) {
        // call the incense method to init/check and then the start method
        if (incense() != null && incense().start(player, this)) {
            isBurning = true;
            world.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(LIT, isBurning));
            burnCounter = 0;
            sync();
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(pTag, provider);
        if (pTag.contains("incense")) {
            stack = ItemStack.parseOptional(provider, pTag.getCompound("incense"));
        } else stack = ItemStack.EMPTY;
        if (pTag.contains("incenseContext") && stack.isEmpty()) {
            incenseRitual = IncenseRitual.read(pTag);
            incenseRitual.start(null, this);
        }
        burnCounter = pTag.getInt("burnCounter");
        isBurning = pTag.getBoolean("isBurning");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(pTag, provider);
        if (!stack.isEmpty()) {
            pTag.put("incense", stack.saveOptional(provider));
        }
        if (incenseRitual != null) {
            incenseRitual.write(pTag);
        }
        pTag.putInt("burnCounter", burnCounter);
        pTag.putBoolean("isBurning", isBurning);
    }

    public void extinguish() {
        if (level instanceof ServerLevel) {
            isBurning = false;
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(LIT, isBurning));
            burnCounter = 0;
            incenseRitual = null;
            sync();
        }
    }

    public IncenseRitual incense() {
        // if there is an incense item in the censer, get the ritual associated with it
        if (incenseRitual == null && !stack.isEmpty()) {
            incenseRitual = IncenseRegistry.getIncenseRitual(stack.getItem());
        }
        return incenseRitual;
    }
}
