package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Eidolon.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EidolonKeybindings {

    public static final KeyMapping OPEN_BOOK = new KeyMapping("key.eidolon_repraised.open_docs", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.eidolon");

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        // Register keybindings here
        event.register(OPEN_BOOK);
    }

}
