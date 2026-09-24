package alexthw.eidolon_repraised.compat.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import static alexthw.eidolon_repraised.Eidolon.prefix;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    public static final ResourceLocation ENTHRALL_TOOLTIP = prefix("enthrall_tooltip");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(EnthrallComponentProvider.INSTANCE, LivingEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(EnthrallComponentProvider.INSTANCE, LivingEntity.class);
    }
}