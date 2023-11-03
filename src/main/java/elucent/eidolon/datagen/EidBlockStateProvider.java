package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.DecoBlockPack;
import elucent.eidolon.registries.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import static elucent.eidolon.Eidolon.prefix;
import static elucent.eidolon.util.RegistryUtil.getRegistryName;

public class EidBlockStateProvider extends BlockStateProvider {
    public EidBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper) {
        super(gen, Eidolon.MODID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        var button_pplate_sign = new DecoBlockPack.WoodDecoBlock[]{Registry.ILLWOOD_PLANKS, Registry.POLISHED_PLANKS};

        for (DecoBlockPack.WoodDecoBlock pack : button_pplate_sign) {
            ResourceLocation baseTex = prefix("block/" + pack.baseBlockName);
            buttonBlock(pack.getButton(), baseTex);
            signBlock(pack.getStandingSign(), pack.getWallSign(), baseTex);
            pressurePlateBlock(pack.getPressurePlate(), baseTex);
        }
    }

    @Override
    public void pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown) {
        super.pressurePlateBlock(block, pressurePlate, pressurePlateDown);
        simpleBlockItem(block, pressurePlate);
    }

    String name(Block block) {
        return getRegistryName(block).getPath();
    }

}
