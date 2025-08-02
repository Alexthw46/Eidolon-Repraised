package elucent.eidolon.common.item;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.IDyeable;
import elucent.eidolon.client.ClientRegistry;
import elucent.eidolon.common.item.model.WarlockArmorModel;
import elucent.eidolon.registries.EidolonMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class WarlockRobesItem extends ArmorItem implements IDyeable {

    public WarlockRobesItem(Type slot, Properties builderIn) {
        super(EidolonMaterials.WARLOCK_ROBES, slot, builderIn);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        var og = super.getName(pStack);
        if (getColor(pStack) == DyeColor.BLUE) return og;
        return Component.literal(og.getString() + " (" + Component.translatable(getColor(pStack).getName()).getString() + ")");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull WarlockArmorModel getHumanoidArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot armorSlot, @NotNull HumanoidModel _default) {
                float pticks = Minecraft.getInstance().getFrameTimeNs();
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
        return stack.has(DataComponents.BASE_COLOR) ? stack.get(DataComponents.BASE_COLOR) : DyeColor.BLUE;
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        DyeColor dyeColor = getColor(stack);
        return ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "textures/entity/warlock_robes/" + dyeColor.getName() + ".png");
    }

}
