package elucent.eidolon.gui.jei;

import elucent.eidolon.api.ritual.ItemSacrifice;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.codex.Page;
import elucent.eidolon.codex.RitualPage;
import elucent.eidolon.recipe.CrucibleRecipe;
import elucent.eidolon.recipe.CrucibleRegistry;
import elucent.eidolon.recipe.WorktableRecipe;
import elucent.eidolon.recipe.WorktableRegistry;

public class RecipeWrappers {
    public static class Crucible {
        final CrucibleRecipe recipe;
        Page page;

        public Crucible(CrucibleRecipe recipe, Page page) {
            this.recipe = recipe;
            this.page = page;
        }

        public Page getPage() {
            if (page == null) {
                page = CrucibleRegistry.getDefaultPage(recipe);
            }
            return page;
        }
    }

    public static class Worktable {
        final WorktableRecipe recipe;
        Page page;

        public Worktable(WorktableRecipe recipe, Page page) {
            this.recipe = recipe;
            this.page = page;
        }

        public Page getPage() {
            if (this.page == null) {
                this.page = WorktableRegistry.getDefaultPage(this.recipe);
            }
            return this.page;
        }
    }

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
