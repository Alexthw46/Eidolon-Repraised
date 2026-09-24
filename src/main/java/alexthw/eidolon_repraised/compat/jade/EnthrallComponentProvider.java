package alexthw.eidolon_repraised.compat.jade;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.util.CommonProxy;

import static alexthw.eidolon_repraised.util.EntityUtil.getMasterUUID;
import static alexthw.eidolon_repraised.util.EntityUtil.isEnthralled;

public enum EnthrallComponentProvider implements IEntityComponentProvider, StreamServerDataProvider<EntityAccessor, String> {
    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.ENTHRALL_TOOLTIP;
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getEntity() instanceof LivingEntity) {
            String name = this.decodeFromData(entityAccessor).orElse("");
            if (name.isEmpty()) return;
            iTooltip.add(Component.translatable("jade.owner", name));
        }
    }


    public String streamData(EntityAccessor accessor) {
        if (!(accessor.getEntity() instanceof LivingEntity livingEntity) || !isEnthralled(livingEntity)) return "";
        return CommonProxy.getLastKnownUsername(getMasterUUID(livingEntity));
    }

    public StreamCodec<RegistryFriendlyByteBuf, String> streamCodec() {
        return ByteBufCodecs.STRING_UTF8.cast();
    }

    public boolean shouldRequestData(EntityAccessor accessor) {
        Entity entity = accessor.getEntity();
        return entity instanceof LivingEntity l && Eidolon.isValidUndead(l);
    }
}