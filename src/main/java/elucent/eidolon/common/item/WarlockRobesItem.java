package elucent.eidolon.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.IDyeable;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.common.item.model.WarlockArmorModel;
import elucent.eidolon.registries.EidolonAttributes;
import elucent.eidolon.registries.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class WarlockRobesItem extends ArmorItem implements IDyeable {
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    public static class Material implements ArmorMaterial {

        @Override
        public int getDurabilityForType(Type slot) {
            return MAX_DAMAGE_ARRAY[slot.getSlot().getIndex()] * 21;
        }

        @Override
        public int getDefenseForType(Type slot) {
            return switch (slot) {
                case CHESTPLATE -> 7;
                case HELMET -> 3;
                case BOOTS -> 2;
                default -> 0;
            };
        }

        @Override
        public int getEnchantmentValue() {
            return 25;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return ArmorMaterials.LEATHER.getEquipSound();
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(new ItemStack(Registry.WICKED_WEAVE.get()));
        }

        @Override
        public @NotNull String getName() {
            return Eidolon.MODID + ":warlock_robes";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }

        public static final Material INSTANCE = new Material();
    }

    public WarlockRobesItem(Type slot, Properties builderIn) {
        super(Material.INSTANCE, slot, builderIn);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        var og = super.getName(pStack);
        if (!(pStack.hasTag() && pStack.getTag().contains("color"))) return og;
        return Component.literal(og.getString() + " (" + Component.translatable(getColor(pStack).getName()).getString() + ")");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull WarlockArmorModel getHumanoidArmorModel(LivingEntity entity, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel _default) {
                float pticks = Minecraft.getInstance().getFrameTime();
                float f = Mth.rotLerp(pticks, entity.yBodyRotO, entity.yBodyRot);
                float f1 = Mth.rotLerp(pticks, entity.yHeadRotO, entity.yHeadRot);
                float netHeadYaw = f1 - f;
                float netHeadPitch = Mth.lerp(pticks, entity.xRotO, entity.getXRot());
                ClientRegistry.WARLOCK_ARMOR_MODEL.slot = getEquipmentSlot();
                ClientRegistry.WARLOCK_ARMOR_MODEL.copyFromDefault(_default);
                ClientRegistry.WARLOCK_ARMOR_MODEL.setupAnim(entity, entity.walkAnimation.position(), entity.walkAnimation.speed(), entity.tickCount + pticks, netHeadYaw, netHeadPitch);
                return ClientRegistry.WARLOCK_ARMOR_MODEL;
            }
        });
    }

    private DyeColor getColor(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        return tag.contains("color") ? DyeColor.byId(tag.getInt("color")) : DyeColor.BLUE;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        DyeColor dyeColor = getColor(stack);
        return Eidolon.MODID + ":textures/entity/warlock_robes/" + dyeColor.getName() + ".png";
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot pEquipmentSlot) {
        Multimap<Attribute, AttributeModifier> map = super.getDefaultAttributeModifiers(pEquipmentSlot);
        if (pEquipmentSlot == EquipmentSlot.HEAD && this == Registry.WARLOCK_HAT.get()) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(map);
            builder.put(EidolonAttributes.MAGIC_POWER.get(), new AttributeModifier(Eidolon.MODID + ":warlock_hat", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
            map = builder.build();
        }
        return map;
    }

}
