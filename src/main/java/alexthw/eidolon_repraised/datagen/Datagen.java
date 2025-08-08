package alexthw.eidolon_repraised.datagen;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Eidolon.MODID)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        gen.addProvider(event.includeClient(), new EidBlockStateProvider(gen, fileHelper));
        var blockGen = new EidBlockTagProvider(gen, provider, fileHelper);
        gen.addProvider(event.includeServer(), blockGen);
        gen.addProvider(event.includeServer(), new EidItemTagProvider(gen, provider, blockGen, fileHelper));
        gen.addProvider(event.includeServer(), new EidDataMapProvider(output, provider));
        gen.addProvider(event.includeServer(), new ModLootTables(gen, lookupProvider));
        gen.addProvider(event.includeServer(), new EidRecipeProvider(gen, lookupProvider));
        gen.addProvider(event.includeServer(), new EidBiomeTagProvider(gen, provider, fileHelper));
        gen.addProvider(event.includeServer(), new EidWorldgenProvider(output, provider));
        gen.addProvider(event.includeServer(), new EidDamageProvider.DamageTypeDataProvider(output, provider));
        gen.addProvider(event.includeServer(), new EidDamageProvider.DamageTypeTagGen(output, provider, fileHelper));
        gen.addProvider(event.includeServer(), new EidRitualProvider(gen));
        gen.addProvider(event.includeServer(), new EidChantProvider(gen));
        gen.addProvider(event.includeServer(), new EidForagingProvider(gen));
        gen.addProvider(event.includeServer(), new EidAdvancementProvider(gen, provider, fileHelper));
        gen.addProvider(event.includeServer(), new EidEnchantmentTagProvider(gen, provider, fileHelper));
        gen.addProvider(event.includeServer(), new EidEntityTagProvider(gen, provider, fileHelper));
//        gen.addProvider(true, new StructureUpdater("structures", Eidolon.MODID, fileHelper, output));
//        gen.addProvider(true, new StructureUpdater("structures/catacombs", Eidolon.MODID, fileHelper, output));
//
    }
}