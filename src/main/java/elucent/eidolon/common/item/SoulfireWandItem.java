package elucent.eidolon.common.item;

import elucent.eidolon.common.entity.SoulfireProjectileEntity;
import elucent.eidolon.registries.EidolonEntities;
import elucent.eidolon.registries.EidolonSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SoulfireWandItem extends WandItem {
    static final Random random = new Random();

    public SoulfireWandItem(Properties builderIn) {
        super(builderIn);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player entity, @NotNull InteractionHand hand) {
        ItemStack stack = entity.getItemInHand(hand);
        if (!world.isClientSide) {
            Vec3 pos = entity.position().add(entity.getLookAngle().scale(0.5)).add(0.5 * Math.sin(Math.toRadians(225 - entity.yHeadRot)), entity.getBbHeight() * 2 / 3, 0.5 * Math.cos(Math.toRadians(225 - entity.yHeadRot)));
            Vec3 vel = entity.getEyePosition(0).add(entity.getLookAngle().scale(40)).subtract(pos).scale(1.0 / 20);
            world.addFreshEntity(new SoulfireProjectileEntity(EidolonEntities.SOULFIRE_PROJECTILE.get(), world).shoot(
                    pos.x, pos.y, pos.z, vel.x, vel.y, vel.z, entity.getUUID()
            ));
            world.playSound(null, pos.x, pos.y, pos.z, EidolonSounds.CAST_SOULFIRE_EVENT.get(), SoundSource.NEUTRAL, 0.75f, random.nextFloat() * 0.2f + 0.9f);
            stack.hurtAndBreak(1, entity, (player) -> player.broadcastBreakEvent(hand));
            entity.getCooldowns().addCooldown(this, 15);
        }
        if (!entity.swinging) entity.swing(hand);
        return InteractionResultHolder.success(stack);
    }
}
