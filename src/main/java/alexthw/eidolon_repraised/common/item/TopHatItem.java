package alexthw.eidolon_repraised.common.item;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.client.ClientRegistry;
import alexthw.eidolon_repraised.common.item.model.TopHatModel;
import alexthw.eidolon_repraised.registries.EidolonMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TopHatItem extends ArmorItem {
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    public TopHatItem(Properties builderIn) {
        super(EidolonMaterials.TOP_HAT, Type.HELMET, builderIn);
    }

    String loreTag = null;

    public Item setLore(String tag) {
        this.loreTag = tag;
        return this;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (this.loreTag != null) {
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.literal(String.valueOf(ChatFormatting.DARK_PURPLE) + ChatFormatting.ITALIC + I18n.get(this.loreTag)));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(java.util.function.Consumer<net.neoforged.neoforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            @Override
            public @NotNull TopHatModel getHumanoidArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot armorSlot, @NotNull HumanoidModel _default) {
                float pticks = Minecraft.getInstance().getFrameTimeNs();
                float f = Mth.rotLerp(pticks, entity.yBodyRotO, entity.yBodyRot);
                float f1 = Mth.rotLerp(pticks, entity.yHeadRotO, entity.yHeadRot);
                float netHeadYaw = f1 - f;
                float netHeadPitch = Mth.lerp(pticks, entity.xRotO, entity.getXRot());
                ClientRegistry.TOP_HAT_MODEL.slot = getEquipmentSlot();
                ClientRegistry.TOP_HAT_MODEL.copyFromDefault(_default);
                ClientRegistry.TOP_HAT_MODEL.setupAnim(entity, entity.walkAnimation.position(), entity.walkAnimation.speed(), entity.tickCount + pticks, netHeadYaw, netHeadPitch);
                return ClientRegistry.TOP_HAT_MODEL;
            }
        });
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(Eidolon.MODID, "textures/entity/hat.png");
    }
}
