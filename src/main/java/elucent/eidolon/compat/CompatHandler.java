package elucent.eidolon.compat;

import elucent.eidolon.Eidolon;
import elucent.eidolon.compat.apotheosis.Apotheosis;
import net.minecraftforge.fml.ModList;

import java.util.Map;

public class CompatHandler {
    public final static String APOTHEOSIS = "apotheosis";

    private static final Map<String, ModData> MODS = Map.of(
            APOTHEOSIS, new ModData(Apotheosis::initialize)
    );

    public static void initialize() {
        MODS.forEach((modId, data) -> {
            if (ModList.get().isLoaded(modId)) {
                data.initialize.run();
                data.isLoaded = true;
                Eidolon.LOG.info("Loaded [{}] compatibility", modId);
            }
        });
    }

    public static boolean isModLoaded(final String modId) {
        return MODS.get(modId).isLoaded;
    }

    private static class ModData {
        public final Runnable initialize;

        public boolean isLoaded;

        public ModData(final Runnable initialize) {
            this.initialize = initialize;
        }
    }
}
