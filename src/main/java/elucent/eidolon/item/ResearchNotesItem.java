package elucent.eidolon.item;

import elucent.eidolon.research.Research;
import elucent.eidolon.research.Researches;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;

public class ResearchNotesItem extends ItemBase {
    private final Random random = new Random();

    public ResearchNotesItem(Properties builderIn) {
        super(builderIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (!stack.hasTag() || !stack.getTag().contains("research")) return;
        Research r = Researches.find(new ResourceLocation(stack.getTag().getString("research")));
        if (r == null) return;
        int done = stack.getTag().getInt("stepsDone");
        StringBuilder stars = new StringBuilder();
        stars.append(ChatFormatting.GOLD);
        for (int i = 0; i < r.getStars(); i ++) {
            if (i == done) stars.append(ChatFormatting.GRAY);
            if (i < done) stars.append("\u2605");
            else stars.append("\u2606");
        }
        tooltip.add(Component.literal(stars.toString()));
        boolean known = done >= r.getStars() || KnowledgeUtil.knowsResearch(Minecraft.getInstance().player, r.getRegistryName());
        String name = known ? r.getName() : "???";
        tooltip.add(Component.literal(String.valueOf(known ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY) + ChatFormatting.ITALIC + name));
    }
}
