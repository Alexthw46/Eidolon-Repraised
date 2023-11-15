package elucent.eidolon.common.item;

import com.mojang.datafixers.util.Pair;
import elucent.eidolon.common.entity.SpellProjectileEntity;
import elucent.eidolon.compat.CompatHandler;
import elucent.eidolon.compat.apotheosis.Apotheosis;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static elucent.eidolon.common.item.SoulfireWandItem.random;

public class WandItem extends ItemBase implements IRechargeableWand {
    private static final double OFFSET = 1.5;

    public WandItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 20;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchant) {
        return super.canApplyAtEnchantingTable(stack, enchant)
                || enchant == Enchantments.UNBREAKING
                || enchant == Enchantments.MENDING;
    }

    @Override
    public ItemStack recharge(ItemStack stack) {
        stack.setDamageValue(0);
        return stack;
    }

    public InteractionResultHolder<ItemStack> handleCast(final Level world, final Player entity, final InteractionHand hand, @NotNull final EntityType<? extends SpellProjectileEntity> spellProjectile, final SoundEvent soundEvent) {
        ItemStack stack = entity.getItemInHand(hand);

        if (!world.isClientSide()) {
            Vec3 pos = entity.position().add(entity.getLookAngle().scale(0.5)).add(0.5 * Math.sin(Math.toRadians(225 - entity.yHeadRot)), entity.getBbHeight() * 2 / 3, 0.5 * Math.cos(Math.toRadians(225 - entity.yHeadRot)));
            Vec3 vel = entity.getEyePosition(0).add(entity.getLookAngle().scale(40)).subtract(pos).scale(1.0 / 20);

            Pair<Integer, Integer> affixData = CompatHandler.isModLoaded(CompatHandler.APOTHEOSIS) ? Apotheosis.handleWandAffix(stack) : Pair.of(1, 0);
            int projectileAmount = affixData.getFirst();
            int trackingAmount = affixData.getSecond();

            for (int i = 0; i < projectileAmount; i++) {
                SpellProjectileEntity spellProjectileEntity = spellProjectile.create(world);

                if (spellProjectileEntity != null) {
                    if (trackingAmount > 0) {
                        spellProjectileEntity.isTracking = true;
                        trackingAmount--;
                    }

                    Vec3 randomized = Vec3.ZERO;

                    if (i > 0) {
                        randomized = new Vec3(random.nextDouble(-OFFSET, OFFSET), random.nextDouble(-OFFSET, OFFSET), random.nextDouble(-OFFSET, OFFSET));
                        spellProjectileEntity.noImmunityFrame = true;
                    }

                    world.addFreshEntity(spellProjectileEntity.shoot(
                            pos.x + randomized.x(), pos.y + randomized.y(), pos.z + randomized.z(), vel.x, vel.y, vel.z, entity.getUUID(), stack
                    ));
                }
            }

            world.playSound(null, pos.x, pos.y, pos.z, soundEvent, SoundSource.NEUTRAL, 0.75f, random.nextFloat() * 0.2f + 0.9f);
            stack.hurtAndBreak(1, entity, player -> player.broadcastBreakEvent(hand));
            entity.getCooldowns().addCooldown(this,  15);
        }

        if (!entity.swinging) {
            entity.swing(hand);
        }

        return InteractionResultHolder.success(stack);
    }

}
