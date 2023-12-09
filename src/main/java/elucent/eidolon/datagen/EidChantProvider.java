package elucent.eidolon.datagen;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.api.spells.Spell;
import elucent.eidolon.recipe.ChantRecipe;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.registries.Spells;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EidChantProvider extends SimpleDataProvider {


    public EidChantProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    List<ChantRecipe> chants = new ArrayList<>();

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addChants();
        for (ChantRecipe recipe : chants) {
            Path path = getRecipePath(output, recipe.getId().getPath());
            saveStable(pOutput, recipe.toJson(), path);
        }
    }


    protected void addChants() {
        addChant(Spells.DARK_PRAYER, Signs.WICKED_SIGN, Signs.WICKED_SIGN, Signs.WICKED_SIGN);
        addChant(Spells.DARK_ANIMAL_SACRIFICE, Signs.WICKED_SIGN, Signs.BLOOD_SIGN, Signs.WICKED_SIGN);
        addChant(Spells.DARK_TOUCH, Signs.WICKED_SIGN, Signs.SOUL_SIGN, Signs.WICKED_SIGN, Signs.SOUL_SIGN);
        addChant(Spells.DARKLIGHT_CHANT, Signs.WICKED_SIGN, Signs.FLAME_SIGN, Signs.WICKED_SIGN, Signs.FLAME_SIGN);
        addChant(Spells.DARK_VILLAGER_SACRIFICE, Signs.BLOOD_SIGN, Signs.WICKED_SIGN, Signs.BLOOD_SIGN, Signs.SOUL_SIGN);
        addChant(Spells.ZOMBIFY, Signs.DEATH_SIGN, Signs.BLOOD_SIGN, Signs.WICKED_SIGN, Signs.DEATH_SIGN, Signs.SOUL_SIGN, Signs.BLOOD_SIGN);
        addChant(Spells.ENTHRALL_UNDEAD, Signs.WICKED_SIGN, Signs.MIND_SIGN, Signs.MAGIC_SIGN, Signs.MAGIC_SIGN, Signs.MIND_SIGN);

        addChant(Spells.LIGHT_PRAYER, Signs.SACRED_SIGN, Signs.SACRED_SIGN, Signs.SACRED_SIGN);
        addChant(Spells.LIGHT_CHANT, Signs.SACRED_SIGN, Signs.FLAME_SIGN, Signs.SACRED_SIGN, Signs.FLAME_SIGN);
        addChant(Spells.HOLY_TOUCH, Signs.SACRED_SIGN, Signs.SOUL_SIGN, Signs.SACRED_SIGN, Signs.SOUL_SIGN);
        addChant(Spells.LAY_ON_HANDS, Signs.FLAME_SIGN, Signs.SOUL_SIGN, Signs.SACRED_SIGN, Signs.SOUL_SIGN, Signs.SACRED_SIGN);
        addChant(Spells.CURE_ZOMBIE_CHANT, Signs.SACRED_SIGN, Signs.SOUL_SIGN, Signs.MIND_SIGN, Signs.HARMONY_SIGN, Signs.FLAME_SIGN, Signs.SOUL_SIGN);
        addChant(Spells.SMITE_CHANT, Signs.FLAME_SIGN, Signs.MAGIC_SIGN, Signs.SACRED_SIGN, Signs.DEATH_SIGN, Signs.MAGIC_SIGN, Signs.SACRED_SIGN);

        addChant(Spells.SUNDER_ARMOR, Signs.FLAME_SIGN, Signs.MAGIC_SIGN, Signs.WICKED_SIGN, Signs.MAGIC_SIGN, Signs.FLAME_SIGN);
        addChant(Spells.BLESS_ARMOR, Signs.SACRED_SIGN, Signs.WARDING_SIGN, Signs.SACRED_SIGN, Signs.WARDING_SIGN, Signs.SACRED_SIGN);

        addChant(Spells.FROST_CHANT, Signs.WICKED_SIGN, Signs.WINTER_SIGN, Signs.BLOOD_SIGN, Signs.WINTER_SIGN, Signs.WICKED_SIGN);
        addChant(Spells.FIRE_CHANT, Signs.FLAME_SIGN, Signs.FLAME_SIGN, Signs.FLAME_SIGN);
        addChant(Spells.WATER_CHANT, Signs.WINTER_SIGN, Signs.WINTER_SIGN, Signs.FLAME_SIGN, Signs.FLAME_SIGN);
    }

    private void addChant(Spell spell, Sign... signs) {
        chants.add(new ChantRecipe(spell.getRegistryName(), List.of(signs)));
    }

    @Override
    public @NotNull String getName() {
        return "Eidolon Chants";
    }

    protected static Path getRecipePath(Path pathIn, Spell spell) {
        return getRecipePath(pathIn, spell.getRegistryName().getPath());
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/eidolon/recipes/" + str + ".json");
    }
}