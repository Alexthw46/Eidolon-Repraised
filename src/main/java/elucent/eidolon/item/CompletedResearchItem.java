package elucent.eidolon.item;

import java.util.List;

import elucent.eidolon.research.Research;
import elucent.eidolon.research.Researches;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;

public class CompletedResearchItem extends ItemBase {
    public CompletedResearchItem(Properties builderIn) {
        super(builderIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (!stack.hasTag() || !stack.getTag().contains("research")) return;
        Research r = Researches.find(new ResourceLocation(stack.getTag().getString("research")));
        if (r == null) return;
        tooltip.add(Component.literal(String.valueOf(ChatFormatting.ITALIC) + ChatFormatting.GOLD + r.getName()));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.hasTag() && stack.getTag().contains("research")) {
            Research r = Researches.find(new ResourceLocation(stack.getTag().getString("research")));
            if (r != null && !KnowledgeUtil.knowsResearch(player, r.getRegistryName())) {
                KnowledgeUtil.grantResearch(player, r.getRegistryName());
                return InteractionResultHolder.consume(ItemStack.EMPTY);
            }
        }
        return super.use(level, player, hand);
    }
}
