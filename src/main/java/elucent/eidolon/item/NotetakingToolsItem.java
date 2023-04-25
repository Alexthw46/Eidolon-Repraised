package elucent.eidolon.item;

import elucent.eidolon.Registry;
import elucent.eidolon.research.Research;
import elucent.eidolon.research.Researches;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class NotetakingToolsItem extends ItemBase {
    public NotetakingToolsItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        Collection<Research> researches = Researches.getEntityResearches(entity);
        if (!researches.isEmpty()) {
            Research r = researches.iterator().next();
            if (!player.level.isClientSide && r != null) {
                ItemStack notes = new ItemStack(Registry.RESEARCH_NOTES.get(), 1);
                notes.getOrCreateTag().putString("research", r.getRegistryName().toString());
                notes.getTag().putInt("stepsDone", 0);
                stack.shrink(1);
                if (stack.getCount() == 0) player.setItemInHand(hand, notes);
                else if (!player.getInventory().add(notes)) {
                    player.drop(notes, false);
                }
            }
            return InteractionResult.PASS;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        Collection<Research> researches = Researches.getBlockResearches(state.getBlock());
        researches.removeIf((r) -> KnowledgeUtil.knowsResearch(ctx.getPlayer(), r.getRegistryName()));
        if (!researches.isEmpty()) {
            Research r = researches.iterator().next();
            ItemStack notes = new ItemStack(Registry.RESEARCH_NOTES.get(), 1);
            notes.getOrCreateTag().putString("research", r.getRegistryName().toString());
            notes.getTag().putInt("stepsDone", 0);
            ctx.getItemInHand().shrink(1);
            if (ctx.getItemInHand().getCount() == 0)
                ctx.getPlayer().setItemInHand(ctx.getHand(), ItemStack.EMPTY);
            if (!ctx.getPlayer().getInventory().add(notes.copy())) {
                ctx.getPlayer().drop(notes, false);
             }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(ctx);
    }
}
