package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.api.altar.AltarEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;


public class EidolonDataMaps {
    public static final ResourceLocation ALTAR_ENTRY_ID = Eidolon.prefix("altar_entries");

    public static final DataMapType<Block, AltarEntry> ALTAR_ENTRY_MAP = DataMapType.builder(
            ALTAR_ENTRY_ID,
            Registries.BLOCK,
            AltarEntry.CODEC
    ).synced(AltarEntry.CODEC, false).build();

    @SubscribeEvent
    public static void onRegisterDataMaps(RegisterDataMapTypesEvent event) {
        event.register(ALTAR_ENTRY_MAP);
    }

}
