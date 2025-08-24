package elucent.eidolon.datagen;

import elucent.eidolon.api.deity.Deity;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.recipe.ChantConversionRecipe;
import elucent.eidolon.registries.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static elucent.eidolon.Eidolon.prefix;

public class EidChantConversionProvider extends SimpleDataProvider {


    public EidChantConversionProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    List<ChantConversionRecipe> chants = new ArrayList<>();

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addConversions();
        for (ChantConversionRecipe recipe : chants) {
            Path path = getRecipePath(output, recipe.getId().getPath());
            saveStable(pOutput, recipe.toJson(), path);
        }
    }

    protected void addConversions() {
        addConversion("convert_inlay_holy", Deities.LIGHT_DEITY, Registry.GOLD_INLAY.get(), Registry.HOLY_SYMBOL.get().getDefaultInstance(), 10F);
        addConversion("convert_inlay_unholy", Deities.DARK_DEITY, Registry.PEWTER_INLAY.get(), Registry.UNHOLY_SYMBOL.get().getDefaultInstance(), 10F);

        addConversion("convert_top_hat", null, Ingredient.of(Items.BLACK_WOOL), Registry.TOP_HAT.get().getDefaultInstance(), 0F);
        addConversion("convert_disc", null, Ingredient.of(ItemTags.MUSIC_DISCS), Registry.PAROUSIA_DISC.get().getDefaultInstance(), 0F);

    }

    public void addConversion(String name, @Nullable Deity deity, Item input, ItemStack output, float minDevotion) {
        addConversion(name, deity, Ingredient.of(input), output, minDevotion);
    }

    public void addConversion(String name, @Nullable Deity deity, Ingredient input, ItemStack output, float minDevotion) {
        chants.add(new ChantConversionRecipe(prefix(name), input, output, minDevotion, deity == null ? null : deity.getId()));
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/eidolon/recipes/" + str + ".json");
    }

    @Override
    public @NotNull String getName() {
        return "Eidolon Conversion Chants";
    }
}
