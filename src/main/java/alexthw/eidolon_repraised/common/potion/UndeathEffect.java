package alexthw.eidolon_repraised.common.potion;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMobEffectExtension;
import org.jetbrains.annotations.NotNull;

public class UndeathEffect extends MobEffect implements IMobEffectExtension {
    public UndeathEffect() {
        super(MobEffectCategory.HARMFUL, ColorUtil.packColor(255, 51, 39, 42));
    }

    protected static final ResourceLocation EFFECT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "textures/mob_effect/undeath.png");

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (isSunBurnTick(livingEntity)) {
            boolean flag = true;
            ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty()) {
                if (itemstack.isDamageableItem()) {
                    Item item = itemstack.getItem();
                    itemstack.setDamageValue(itemstack.getDamageValue() + livingEntity.getRandom().nextInt(2));
                    if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                        livingEntity.onEquippedItemBroken(item, EquipmentSlot.HEAD);
                        livingEntity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag) {
                livingEntity.igniteForSeconds(8.0F);
            }
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    protected boolean isSunBurnTick(LivingEntity entity) {
        if (entity.level().isDay() && !entity.level().isClientSide) {
            float f = entity.getLightLevelDependentMagicValue();
            BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
            boolean flag = entity.isInWaterRainOrBubble() || entity.isInPowderSnow || entity.wasInPowderSnow;
            return f > 0.5F && entity.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && entity.level().canSeeSky(blockpos);
        }

        return false;
    }

}
