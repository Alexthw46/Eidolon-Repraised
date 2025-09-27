package alexthw.eidolon_repraised.datagen;

import alexthw.eidolon_repraised.api.deity.Deity;
import alexthw.eidolon_repraised.common.deity.Deities;
import alexthw.eidolon_repraised.recipe.ChantConversionRecipe;
import alexthw.eidolon_repraised.registries.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static alexthw.eidolon_repraised.Eidolon.prefix;

public class EidChantConversionProvider extends SimpleDataProvider {


    public EidChantConversionProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    Map<ResourceLocation, ChantConversionRecipe> chants = new HashMap<>();

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addConversions();
        for (var recipe : chants.entrySet()) {
            Path path = getRecipePath(output, recipe.getKey().getPath());
            saveStable(pOutput, recipe.getValue().toJson(), path);
        }
    }

    protected void addConversions() {
        addConversion("convert_inlay_holy", Deities.LIGHT_DEITY, Registry.GOLD_INLAY.get(), Registry.HOLY_SYMBOL.get().getDefaultInstance(), 10F);
        addConversion("convert_inlay_unholy", Deities.DARK_DEITY, Registry.PEWTER_INLAY.get(), Registry.UNHOLY_SYMBOL.get().getDefaultInstance(), 10F);

        addConversion("convert_top_hat", null, Ingredient.of(Items.BLACK_WOOL), Registry.TOP_HAT.get().getDefaultInstance(), 0F);
        addConversion("convert_disc", null, Ingredient.of(Tags.Items.MUSIC_DISCS), Registry.PAROUSIA_DISC.get().getDefaultInstance(), 0F);

    }

    public void addConversion(String name, @Nullable Deity deity, Item input, ItemStack output, float minDevotion) {
        addConversion(name, deity, Ingredient.of(input), output, minDevotion);
    }

    public void addConversion(String name, @Nullable Deity deity, Ingredient input, ItemStack output, float minDevotion) {
        chants.put(prefix(name), new ChantConversionRecipe(input, output, minDevotion, deity == null ? null : deity.getId()));
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/eidolon_repraised/recipe/" + str + ".json");
    }

    @Override
    public @NotNull String getName() {
        return "Eidolon Conversion Chants";
    }
}
