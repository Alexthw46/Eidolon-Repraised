package elucent.eidolon.common.item;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CodexItem extends ItemBase implements IManaRelatedItem {
    public CodexItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.is(Blocks.LECTERN)) {
            return LecternBlock.tryPlaceBook(pContext.getPlayer(), level, blockpos, blockstate, pContext.getItemInHand()) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @NotNull Player entity, @NotNull InteractionHand hand) {
        if (world.isClientSide) {
            entity.playNotifySound(SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
            Eidolon.proxy.openCodexGui(entity);
        }
        return InteractionResultHolder.pass(entity.getItemInHand(hand));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClientSide && stack.hasTag() && stack.getTag().contains("sign")) {
            ResourceLocation loc = new ResourceLocation(stack.getTag().getString("sign"));
            stack.getTag().remove("sign");
            Sign sign = Signs.find(loc);
            if (sign != null) KnowledgeUtil.grantSign(entity, sign);
        }
    }

    public static ItemStack withSign(ItemStack stack, Sign sign) {
        ItemStack newStack = stack.copy();
        newStack.getOrCreateTag().putString("sign", sign.getRegistryName().toString());
        return newStack;
    }
}
