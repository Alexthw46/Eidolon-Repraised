package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.DecoBlockPack;
import elucent.eidolon.registries.Registry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class EidRecipeProvider extends RecipeProvider {
    public EidRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        buildDecoPack(pFinishedRecipeConsumer, Registry.ILLWOOD_PLANKS);
        buildDecoPack(pFinishedRecipeConsumer, Registry.ELDER_BRICKS, List.of(Registry.ELDER_BRICKS_EYE.get(), Registry.ELDER_PILLAR.get(), Registry.ELDER_MASONRY.getBlock()));
        buildDecoPack(pFinishedRecipeConsumer, Registry.ELDER_MASONRY);

        makeStonecutter(pFinishedRecipeConsumer, Blocks.SMOOTH_STONE, Registry.SMOOTH_STONE_MASONRY.getBlock(), "smooth_stone_masonry");
        buildDecoPack(pFinishedRecipeConsumer, Registry.SMOOTH_STONE_MASONRY);

        buildDecoPack(pFinishedRecipeConsumer, Registry.BONE_PILE);
        buildDecoPack(pFinishedRecipeConsumer, Registry.POLISHED_PLANKS);

        makeStonecutter(pFinishedRecipeConsumer, Registry.SMOOTH_STONE_BRICK.getBlock(), Registry.SMOOTH_STONE_ARCH.get(), "smooth_stone_arch");
        ShapelessRecipeBuilder.shapeless(Registry.MOSSY_SMOOTH_STONE_BRICKS.get()).requires(Registry.SMOOTH_STONE_BRICK.getBlock()).requires(Items.VINE).unlockedBy("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(Registry.SMOOTH_STONE_BRICK.getBlock())).save(pFinishedRecipeConsumer, Eidolon.prefix("mossy_smooth_stone_bricks"));

        woodFromLogs(pFinishedRecipeConsumer, Registry.ILLWOOD_BARK.get(), Registry.ILLWOOD_LOG.get());
        planksFromLog(pFinishedRecipeConsumer, Registry.ILLWOOD_PLANKS.getBlock(), Registry.ILLWOOD_LOGS);
        strippedLogToWood(pFinishedRecipeConsumer, Registry.STRIPPED_ILLWOOD_LOG.get(), Registry.STRIPPED_ILLWOOD_BARK.get());
    }

    private void buildDecoPack(Consumer<FinishedRecipe> consumer, DecoBlockPack decoBlockPack, List<Block> extras) {
        Block block = decoBlockPack.getBlock();

        makeSlab(consumer, block, decoBlockPack.getSlab(), decoBlockPack.baseBlockName);
        makeStairs(consumer, block, decoBlockPack.getStairs(), decoBlockPack.baseBlockName);
        makeStonecutter(consumer, block, decoBlockPack.getSlab(), 2, decoBlockPack.baseBlockName);
        makeStonecutter(consumer, block, decoBlockPack.getStairs(), decoBlockPack.baseBlockName);

        if (decoBlockPack.getWall() != null) {
            makeWall(consumer, block, decoBlockPack.getWall(), decoBlockPack.baseBlockName);
            makeStonecutter(consumer, block, decoBlockPack.getWall(), decoBlockPack.baseBlockName);
        }

        if (decoBlockPack.getPressurePlate() != null) {
            makePressurePlate(consumer, block, decoBlockPack.getPressurePlate(), decoBlockPack.baseBlockName);
        }

        if (decoBlockPack instanceof DecoBlockPack.WoodDecoBlock woodDecoBlock) {

            if (woodDecoBlock.getFence() != null) {
                makeFence(consumer, block, woodDecoBlock.getFence(), decoBlockPack.baseBlockName);
                makeStonecutter(consumer, block, woodDecoBlock.getFence(), decoBlockPack.baseBlockName);
            }
            if (woodDecoBlock.getFenceGate() != null) {
                makeGate(consumer, block, woodDecoBlock.getFenceGate(), decoBlockPack.baseBlockName);
                makeStonecutter(consumer, block, woodDecoBlock.getFence(), decoBlockPack.baseBlockName);
            }
            if (woodDecoBlock.getButton() != null) {
                makeButton(consumer, block, woodDecoBlock.getButton(), decoBlockPack.baseBlockName);
            }
            /*
            if (woodDecoBlock.getDoor() != null) {
                makeDoor(consumer, block, woodDecoBlock.getDoor(), decoBlockPack.baseBlockName);
            }*/
            if (woodDecoBlock.getStandingSign() != null) {
                makeSign(consumer, decoBlockPack, woodDecoBlock, block);
            }

        }

        for (Block extra : extras) {
            makeStonecutter(consumer, block, extra, decoBlockPack.baseBlockName);
        }

        STONECUTTER_COUNTER = 0;
    }

    private static void makeSign(Consumer<FinishedRecipe> consumer, DecoBlockPack decoBlockPack, DecoBlockPack.WoodDecoBlock woodDecoBlock, Block block) {
        signBuilder(woodDecoBlock.getStandingSign(), Ingredient.of(block)).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(block)).save(consumer, new ResourceLocation(Eidolon.MODID, decoBlockPack.baseBlockName + "_sign"));
    }

    private void makeButton(Consumer<FinishedRecipe> consumer, Block block, ButtonBlock button, String baseBlockName) {
        buttonBuilder(button, Ingredient.of(block)).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(block)).save(consumer, new ResourceLocation(Eidolon.MODID, baseBlockName + "_button"));
    }

    private void buildDecoPack(Consumer<FinishedRecipe> consumer, DecoBlockPack decoBlockPack) {
        buildDecoPack(consumer, decoBlockPack, List.of());
    }

    private void makeDoor(Consumer<FinishedRecipe> consumer, Block block, Block door, String basename) {
        doorBuilder(door, Ingredient.of(block)).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(block)).save(consumer, new ResourceLocation(Eidolon.MODID, basename + "_door"));
    }

    private void makePressurePlate(Consumer<FinishedRecipe> consumer, Block block, Block pressurePlate, String basename) {
        pressurePlateBuilder(pressurePlate, Ingredient.of(block)).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(block)).save(consumer, new ResourceLocation(Eidolon.MODID, basename + "_pressure_plate"));
    }

    private void makeSlab(Consumer<FinishedRecipe> consumer, Block block, Block slab, String basename) {
        ShapedRecipeBuilder.shaped(slab, 6).pattern("BBB").define('B', block).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(block)).save(consumer, new ResourceLocation(Eidolon.MODID, basename + "_slab"));
    }

    private void makeWall(Consumer<FinishedRecipe> consumer, Block block, Block wall, String basename) {
        ShapedRecipeBuilder.shaped(wall, 6).pattern("BBB").pattern("BBB").define('B', block).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(block)).save(consumer, new ResourceLocation(Eidolon.MODID, basename + "_wall"));
    }

    private void makeStairs(Consumer<FinishedRecipe> consumer, Block block, Block stairs, String basename) {
        ShapedRecipeBuilder.shaped(stairs, 4).pattern("B  ").pattern("BB ").pattern("BBB").define('B', block).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(block)).save(consumer, new ResourceLocation(Eidolon.MODID, basename + "_stair"));
    }

    private void makeFence(Consumer<FinishedRecipe> consumer, Block block, Block fence, String basename) {
        ShapedRecipeBuilder.shaped(fence, 3).pattern("BSB").pattern("BSB").define('B', block).define('S', Items.STICK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(block)).save(consumer, new ResourceLocation(Eidolon.MODID, basename + "_fence"));
    }

    private void makeGate(Consumer<FinishedRecipe> consumer, Block block, Block gate, String basename) {
        ShapedRecipeBuilder.shaped(gate, 1).pattern("SBS").pattern("SBS").define('B', block).define('S', Items.STICK).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(block)).save(consumer, new ResourceLocation(Eidolon.MODID, basename + "_gate"));
    }

    private static int STONECUTTER_COUNTER = 0;

    public static void makeStonecutter(Consumer<FinishedRecipe> consumer, ItemLike input, ItemLike output, String reg) {
        makeStonecutter(consumer, input, output, 1, reg);
    }

    public static void makeStonecutter(Consumer<FinishedRecipe> consumer, ItemLike input, ItemLike output, int quantity, String reg) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), output, quantity).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(input)).save(consumer, new ResourceLocation(Eidolon.MODID, reg + "_stonecutter_" + STONECUTTER_COUNTER));
        STONECUTTER_COUNTER++;
    }

    private static void strippedLogToWood(Consumer<FinishedRecipe> recipeConsumer, ItemLike stripped, ItemLike output) {
        ShapedRecipeBuilder.shaped(output, 3).define('#', stripped).pattern("##").pattern("##").group("bark")
                .unlockedBy("has_illwood", InventoryChangeTrigger.TriggerInstance.hasItems(stripped))
                .save(recipeConsumer);
    }
}
