package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.DecoBlockPack;
import elucent.eidolon.registries.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import static elucent.eidolon.Eidolon.prefix;

public class EidBlockStateProvider extends BlockStateProvider {
    public EidBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper) {
        super(gen, Eidolon.MODID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        var woodDecoBlocks = new DecoBlockPack.WoodDecoBlock[]{Registry.ILLWOOD_PLANKS, Registry.POLISHED_PLANKS};

        for (DecoBlockPack.WoodDecoBlock pack : woodDecoBlocks) {
            ResourceLocation baseTex = prefix("block/" + pack.baseBlockName);
            buttonBlock(pack.getButton(), baseTex);
            signBlock(pack.getStandingSign(), pack.getWallSign(), baseTex);
            pressurePlateBlock(pack.getPressurePlate(), baseTex);
            if (pack.getDoor() != null) {
                ResourceLocation doorTopTex = prefix("block/" + pack.woodName + "_door_top");
                ResourceLocation doorBottomTex = prefix("block/" + pack.woodName + "_door_bottom");
                doorBlock(pack.getDoor(), doorBottomTex, doorTopTex);
            }
            if (pack.getTrapdoor() != null) {
                ResourceLocation trapdoorTex = prefix("block/" + pack.woodName + "_trapdoor");
                trapdoorBlockWithRenderType(pack.getTrapdoor(), trapdoorTex, true, "cutout");
            }

        }
    }

    @Override
    public void pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown) {
        super.pressurePlateBlock(block, pressurePlate, pressurePlateDown);
        simpleBlockItem(block, pressurePlate);
    }

}
