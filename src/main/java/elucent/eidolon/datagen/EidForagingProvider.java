package elucent.eidolon.datagen;

import elucent.eidolon.recipe.ForagingRecipe;
import elucent.eidolon.registries.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static elucent.eidolon.Eidolon.prefix;
import static elucent.eidolon.util.RegistryUtil.getRegistryName;

public class EidForagingProvider extends SimpleDataProvider {
    public EidForagingProvider(DataGenerator gen) {
        super(gen);
    }

    List<ForagingRecipe> recipes = new ArrayList<>();

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addRecipes();
        for (ForagingRecipe recipe : recipes) {
            Path path = getRecipePath(output, recipe.getId().getPath());
            saveStable(pOutput, recipe.toJson(), path);
        }
    }

    private void addRecipes() {

        addForaging(Registry.SILDRIAN_SEED.get(), Blocks.JUNGLE_LEAVES);
        addForaging(Registry.OANNA_BLOOM.get(), Blocks.LILY_PAD);
        addForaging(Registry.MERAMMER_ROOT.get(), Blocks.OXEYE_DAISY, Blocks.LILY_OF_THE_VALLEY, Blocks.WHITE_TULIP);
        addForaging(Registry.AVENNIAN_SPRIG.get(), Blocks.FERN, Blocks.LARGE_FERN);

    }

    public void addForaging(ItemLike result, Block... blocks) {
        recipes.add(new ForagingRecipe(prefix("forage_" + getRegistryName(result.asItem()).getPath()), new ItemStack(result), Ingredient.of(blocks)));
    }

    public void addForaging(ItemLike result, TagKey<Item> blockItemTag) {
        recipes.add(new ForagingRecipe(prefix("forage_" + getRegistryName(result.asItem()).getPath()), new ItemStack(result), Ingredient.of(blockItemTag)));
    }


    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public @NotNull String getName() {
        return "Eidolon Foraging";
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/eidolon/recipes/" + str + ".json");
    }
}
