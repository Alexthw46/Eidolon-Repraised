package elucent.eidolon.gui.jei;

import elucent.eidolon.codex.Page;
import elucent.eidolon.recipe.CrucibleRecipe;
import elucent.eidolon.recipe.WorktableRecipe;
import elucent.eidolon.ritual.Ritual;

public class RecipeWrappers {
    public static class Crucible {
        final CrucibleRecipe recipe;
        final Page page;

        public Crucible(CrucibleRecipe recipe, Page page) {
            this.recipe = recipe;
            this.page = page;
        }
    }

    public static class Worktable {
        final WorktableRecipe recipe;
        final Page page;

        public Worktable(WorktableRecipe recipe, Page page) {
            this.recipe = recipe;
            this.page = page;
        }
    }

    public static class RitualRecipe {
        final Ritual ritual;
        final Page page;
        final Object sacrifice;

        public RitualRecipe(Ritual ritual, Page page, Object sacrifice) {
            this.ritual = ritual;
            this.page = page;
            this.sacrifice = sacrifice;
        }
    }
}
