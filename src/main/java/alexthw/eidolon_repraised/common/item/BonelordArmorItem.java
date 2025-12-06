package alexthw.eidolon_repraised.common.item;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.ClientRegistry;
import alexthw.eidolon_repraised.common.item.model.BonelordArmorModel;
import alexthw.eidolon_repraised.registries.EidolonAttributes;
import alexthw.eidolon_repraised.registries.EidolonMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BonelordArmorItem extends ArmorItem implements IItemExtension {
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    public BonelordArmorItem(Type slot, Properties builderIn) {
        super(EidolonMaterials.BONELORD, slot, builderIn.stacksTo(1).durability(slot.getDurability(25)));
    }

    static int getSoulHeartBonusForSlot(Type slot) {
        return switch (slot) {
            case HELMET, BOOTS -> 10;
            case CHESTPLATE -> 20;
            case LEGGINGS -> 15;
            default -> 0;
        };
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        var modifiers = super.getDefaultAttributeModifiers(stack);
        modifiers.withModifierAdded(EidolonAttributes.PERSISTENT_SOUL_HEARTS, new AttributeModifier(Eidolon.prefix("bonelord_" + this.type.getName()), getSoulHeartBonusForSlot(this.type), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(this.type.getSlot()));
        return super.getDefaultAttributeModifiers(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(java.util.function.Consumer<net.neoforged.neoforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull BonelordArmorModel getHumanoidArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot armorSlot, @NotNull HumanoidModel _default) {
                float pticks = Minecraft.getInstance().getFrameTimeNs();
                float f = Mth.rotLerp(pticks, entity.yBodyRotO, entity.yBodyRot);
                float f1 = Mth.rotLerp(pticks, entity.yHeadRotO, entity.yHeadRot);
                float netHeadYaw = f1 - f;
                float netHeadPitch = Mth.lerp(pticks, entity.xRotO, entity.getXRot());
                ClientRegistry.BONELORD_ARMOR_MODEL.slot = getEquipmentSlot();
                ClientRegistry.BONELORD_ARMOR_MODEL.copyFromDefault(_default);
                ClientRegistry.BONELORD_ARMOR_MODEL.setupAnim(entity, entity.walkAnimation.position(), entity.walkAnimation.speed(), entity.tickCount + pticks, netHeadYaw, netHeadPitch);
                return ClientRegistry.BONELORD_ARMOR_MODEL;
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "textures/entity/bonelord_armor.png");
    }

}
