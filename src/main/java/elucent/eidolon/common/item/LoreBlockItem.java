package elucent.eidolon.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoreBlockItem extends BlockItem {

    String loreTag;

    public LoreBlockItem(Block pBlock, Properties pProperties, String lore) {
        super(pBlock, pProperties);
        this.loreTag = lore;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext tooltipContext, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (this.loreTag != null) {
            tooltip.add(Component.translatable(this.loreTag).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
        }
    }

}
