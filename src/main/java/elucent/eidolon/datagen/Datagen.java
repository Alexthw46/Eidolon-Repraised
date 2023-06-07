package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Eidolon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen {

    //use runData configuration to generate stuff, event.includeServer() for data, event.includeClient() for assets
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        var blockGen = new EidBlockTagProvider(gen, fileHelper);
        gen.addProvider(event.includeServer(), blockGen);
        gen.addProvider(event.includeServer(), new EidItemTagProvider(gen, blockGen, fileHelper));
        gen.addProvider(event.includeServer(), new ModLootTables(gen));
        gen.addProvider(event.includeServer(), new EidRecipeProvider(gen));
    }

}