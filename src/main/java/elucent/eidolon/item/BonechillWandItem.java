package elucent.eidolon.item;

import elucent.eidolon.entity.BonechillProjectileEntity;
import elucent.eidolon.registries.Entities;
import elucent.eidolon.registries.Sounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player entity, @NotNull InteractionHand hand) {
        ItemStack stack = entity.getItemInHand(hand);
        if (!world.isClientSide) {
            Vec3 pos = entity.position().add(entity.getLookAngle().scale(0.5)).add(0.5 * Math.sin(Math.toRadians(225 - entity.yHeadRot)), entity.getBbHeight() * 2 / 3, 0.5 * Math.cos(Math.toRadians(225 - entity.yHeadRot)));
            Vec3 vel = entity.getEyePosition(0).add(entity.getLookAngle().scale(40)).subtract(pos).scale(1.0 / 20);
            world.addFreshEntity(new BonechillProjectileEntity(Entities.BONECHILL_PROJECTILE.get(), world).shoot(
                    pos.x, pos.y, pos.z, vel.x, vel.y, vel.z, entity.getUUID()
            ));
            world.playSound(null, pos.x, pos.y, pos.z, Sounds.CAST_BONECHILL_EVENT.get(), SoundSource.NEUTRAL, 0.75f, random.nextFloat() * 0.2f + 0.9f);
            stack.hurtAndBreak(1, entity, (player) -> {
                player.broadcastBreakEvent(hand);
            });
            entity.getCooldowns().addCooldown(this, 15);
        }
        if (!entity.swinging) {
            entity.swing(hand);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
