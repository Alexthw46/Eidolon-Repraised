package elucent.eidolon.common.item;

import elucent.eidolon.api.research.Research;
import elucent.eidolon.registries.Researches;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CompletedResearchItem extends ItemBase {
    public CompletedResearchItem(Properties builderIn) {
        super(builderIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (!stack.hasTag() || !stack.getOrCreateTag().contains("research")) return;
        Research r = Researches.find(new ResourceLocation(stack.getOrCreateTag().getString("research")));
        if (r == null) return;
        tooltip.add(Component.literal(String.valueOf(ChatFormatting.ITALIC) + ChatFormatting.GOLD + r.getName()));
    }
    
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.hasTag() && stack.getOrCreateTag().contains("research")) {
            Research r = Researches.find(new ResourceLocation(stack.getOrCreateTag().getString("research")));
            if (r != null && !KnowledgeUtil.knowsResearch(player, r.getRegistryName())) {
                KnowledgeUtil.grantResearch(player, r);
                return InteractionResultHolder.consume(ItemStack.EMPTY);
            }
        }
        return super.use(level, player, hand);
    }
}
