package elucent.eidolon.common.item;

import elucent.eidolon.network.DeathbringerSlashEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.EidolonPotions;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeathbringerScytheItem extends SwordItem {
    public DeathbringerScytheItem(Properties builderIn) {
        super(Tiers.NecroticTier.INSTANCE, 7, -2.9f, builderIn);
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

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ToolAction action) {
        if (action == ToolActions.SWORD_SWEEP) return false;
        return super.canPerformAction(stack, action);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, LivingEntity target, @NotNull LivingEntity attacker) {
        if (target.getMobType() != MobType.UNDEAD) {
            target.addEffect(new MobEffectInstance(EidolonPotions.UNDEATH_EFFECT.get(), 900));
        }
        if (!attacker.level.isClientSide)
            Networking.sendToTracking(attacker.level, attacker.blockPosition(), new DeathbringerSlashEffectPacket(
                    attacker.getX(), attacker.getY() + target.getBbHeight() / 2, attacker.getZ(),
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                    ColorUtil.packColor(255, 33, 26, 23),
                    ColorUtil.packColor(255, 10, 10, 11),
                    ColorUtil.packColor(255, 161, 255, 123),
                    ColorUtil.packColor(255, 194, 171, 70)
            ));
        return super.hurtEnemy(stack, target, attacker);
    }
}
