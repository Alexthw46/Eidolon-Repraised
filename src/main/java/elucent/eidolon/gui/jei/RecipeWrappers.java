package elucent.eidolon.gui.jei;

import elucent.eidolon.api.ritual.ItemSacrifice;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.codex.RitualPage;

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
