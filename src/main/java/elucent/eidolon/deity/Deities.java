package elucent.eidolon.deity;

import elucent.eidolon.Eidolon;
import elucent.eidolon.api.deity.Deity;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deities {
    static final Map<ResourceLocation, Deity> deities = new HashMap<>();

    public static Deity register(Deity deity) {
        deities.put(deity.getId(), deity);
        return deity;
    }

    public static Deity find(ResourceLocation deity) {
        return deities.getOrDefault(deity, null);
    }

    public static final ResourceLocation
            DARK_DEITY_ID = new ResourceLocation(Eidolon.MODID, "dark"),
            LIGHT_DEITY_ID = new ResourceLocation(Eidolon.MODID, "light");
    public static final Deity
            DARK_DEITY = register(new DarkDeity(DARK_DEITY_ID, 154, 77, 255)),
            LIGHT_DEITY = register(new LightDeity(LIGHT_DEITY_ID, 255, 230, 117));

    public static List<Deity> getDeities() {
        return new ArrayList<>(deities.values());
    }
}
