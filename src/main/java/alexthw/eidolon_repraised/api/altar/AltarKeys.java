package alexthw.eidolon_repraised.api.altar;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class AltarKeys {
    Set<ResourceLocation> keys = new HashSet<>();

    public static final ResourceLocation
        LIGHT_KEY = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"light" ),
        SKULL_KEY = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"skull" ),
        PLANT_KEY = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"plant" ),
        OFFERS_KEY = ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,"goblet" );
}
