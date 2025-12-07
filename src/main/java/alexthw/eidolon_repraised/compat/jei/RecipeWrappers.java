package alexthw.eidolon_repraised.compat.jei;

import alexthw.eidolon_repraised.api.ritual.ItemSacrifice;
import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.codex.RitualPage;

public class RecipeWrappers {

    public static class RitualRecipe {
        final Ritual ritual;
        RitualPage page;
        final ItemSacrifice sacrifice;

        public RitualRecipe(Ritual ritual, RitualPage page, ItemSacrifice sacrifice) {
            this.ritual = ritual;
            this.page = page;
            this.sacrifice = sacrifice;
        }
    }

}
