package elucent.eidolon.common.item;

import elucent.eidolon.registries.EidolonEntities;
import elucent.eidolon.registries.EidolonSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
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
import java.util.Random;

public class BonechillWandItem extends WandItem {
    private final Random random = new Random();

    public BonechillWandItem(Properties builderIn) {
        super(builderIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (this.loreTag != null) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(String.valueOf(ChatFormatting.DARK_PURPLE) + ChatFormatting.ITALIC + I18n.get(this.loreTag)));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull final Level world, @NotNull final Player entity, @NotNull final InteractionHand hand) {
        return handleCast(world, entity, hand, EidolonEntities.BONECHILL_PROJECTILE.get(), EidolonSounds.CAST_BONECHILL_EVENT.get());
    }
}
