package elucent.eidolon.common.item;

import elucent.eidolon.network.LifestealEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.util.DamageTypeData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SappingSwordItem extends SwordItem {
    public SappingSwordItem(Properties builderIn) {
        super(Tiers.SanguineTier.INSTANCE, 1, -2.4f, builderIn);
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
    public boolean hurtEnemy(@NotNull ItemStack stack, LivingEntity target, @NotNull LivingEntity attacker) {
        if (target.invulnerableTime > 0) {
            target.invulnerableTime = 0;
            float before = target.getHealth();
            target.hurt(DamageTypeData.source(target.level, DamageTypes.WITHER, attacker, null), 2.0f);
            float healing = before - target.getHealth();
            if (healing > 0) {
                attacker.heal(healing);
                if (!attacker.level.isClientSide)
                    Networking.sendToTracking(attacker.level, attacker.blockPosition(), new LifestealEffectPacket(target.blockPosition(), attacker.blockPosition(), 1.0f, 0.125f, 0.1875f));
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}
