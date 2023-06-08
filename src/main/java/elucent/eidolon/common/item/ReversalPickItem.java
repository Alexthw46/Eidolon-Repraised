package elucent.eidolon.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReversalPickItem extends PickaxeItem {
    public ReversalPickItem(Properties builderIn) {
        super(Tiers.MagicToolTier.INSTANCE, 1, -2.8F, builderIn);
        MinecraftForge.EVENT_BUS.addListener(ReversalPickItem::onStartBreak);
    }

    String loreTag = null;

    public Item setLore(String tag) {
        this.loreTag = tag;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (this.loreTag != null) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(String.valueOf(ChatFormatting.DARK_PURPLE) + ChatFormatting.ITALIC + I18n.get(this.loreTag)));
        }
    }

    @SubscribeEvent
    public static void onStartBreak(PlayerEvent.BreakSpeed event) {
        if (event.getEntity().getMainHandItem().getItem() instanceof ReversalPickItem && event.getPosition().isPresent()) {
            float hardness = event.getState().getDestroySpeed(event.getEntity().level, event.getPosition().get());
            float adjHardness = 1 / (hardness / 2.0f);
            float newSpeed = Mth.sqrt(event.getOriginalSpeed() * 0.25f) * Mth.sqrt(hardness / adjHardness);
            event.setNewSpeed(newSpeed);
        }
    }
}
