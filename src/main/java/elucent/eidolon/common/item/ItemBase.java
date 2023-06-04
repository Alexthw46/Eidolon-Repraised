package elucent.eidolon.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemBase extends Item {
    ChatFormatting loreFormat = ChatFormatting.DARK_PURPLE;
    String loreTag = null;

    public ItemBase(Properties properties) {
        super(properties);
    }

    public ItemBase setLore(String tag) {
        this.loreTag = tag;
        return this;
    }

    public ItemBase setLore(ChatFormatting format, String tag) {
        this.loreFormat = format;
        this.loreTag = tag;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (this.loreTag != null) {
            tooltip.add(Component.translatable(this.loreTag).withStyle(loreFormat, ChatFormatting.ITALIC));
        }
    }
}
