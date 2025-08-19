package alexthw.eidolon_repraised.codex;

import alexthw.eidolon_repraised.api.ritual.Ritual;
import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.api.spells.Spell;
import alexthw.eidolon_repraised.recipe.CrucibleRecipe;
import alexthw.eidolon_repraised.recipe.WorktableRecipe;
import alexthw.eidolon_repraised.util.RegistryUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static alexthw.eidolon_repraised.codex.Page.wrapTextToLines;

public class CodexBuilder {
    private String titleKey;
    private final List<Page> pages = new ArrayList<>();
    private @Nullable Level level;

    public CodexBuilder() {
    }

    public CodexBuilder(@NotNull Level level) {
        this.level = level;
    }

    public CodexBuilder addSupportedRecipePages(ItemLike item) {
        // Add pages for all supported recipes
        ResourceLocation key = RegistryUtil.getRegistryName(item);
        if (key == null) {
            return this; // Skip if item has no registry name
        }
        return addSupportedRecipePages(key);
    }

    public CodexBuilder addSupportedRecipePages(ResourceLocation recipeId) {
        if (level == null) {
            throw new IllegalStateException("Level is not initialized. Use the constructor with Level parameter if you want to use this.");
        }
        RecipeManager recipeManager = level.getRecipeManager();
        // Add recipe page if supported
        recipeManager.byKey(recipeId).ifPresent(holder -> {
            var recipe = holder.value();
            switch (recipe) {
                case CraftingRecipe craftingRecipe ->
                        pages.add(new CraftingPage(recipe.getResultItem(level.registryAccess()), holder.id()));
                case SmeltingRecipe smeltingRecipe ->
                        pages.add(new SmeltingPage(recipe.getResultItem(level.registryAccess()), smeltingRecipe.getIngredients().getFirst().getItems()[0], holder.id()));
                case WorktableRecipe worktableRecipe ->
                        pages.add(new WorktablePage(recipe.getResultItem(level.registryAccess())));
                case CrucibleRecipe crucibleRecipe ->
                        pages.add(new CruciblePage(recipe.getResultItem(level.registryAccess()), crucibleRecipe.getId()));
                default -> {
                }
            }
        });

        return this;
    }

    public CodexBuilder title(String titleKey) {
        this.titleKey = titleKey;
        return this;
    }

    public CodexBuilder titlePage(String textKey) {
        addTextPages(textKey, ItemStack.EMPTY, true);
        return this;
    }

    public CodexBuilder titlePage(String textKey, ItemStack reference) {
        addTextPages(textKey, reference, true);
        return this;
    }

    public CodexBuilder entityPage(EntityType<?> entity) {
        pages.add(new EntityPage(entity));
        return this;
    }

    public CodexBuilder craftingPage(ItemLike output) {
        pages.add(new CraftingPage(output.asItem().getDefaultInstance()));
        return this;
    }

    public CodexBuilder craftingPage(ItemStack output) {
        pages.add(new CraftingPage(output));
        return this;
    }

    public CodexBuilder craftingPage(ItemStack output, ResourceLocation recipeId) {
        pages.add(new CraftingPage(output, recipeId));
        return this;
    }

    public CodexBuilder smeltingPage(ItemStack output, ItemStack input) {
        pages.add(new SmeltingPage(output, input));
        return this;
    }

    public CodexBuilder smeltingPage(ItemStack output, ItemStack input, ResourceLocation recipeId) {
        pages.add(new SmeltingPage(output, input, recipeId));
        return this;
    }

    public CodexBuilder worktablePage(ItemLike output) {
        pages.add(new WorktablePage(output.asItem().getDefaultInstance()));
        return this;
    }

    public CodexBuilder worktablePage(ItemStack output) {
        pages.add(new WorktablePage(output));
        return this;
    }

    public CodexBuilder cruciblePage(ItemLike output) {
        pages.add(new CruciblePage(output.asItem().getDefaultInstance()));
        return this;
    }

    public CodexBuilder cruciblePage(ItemStack output) {
        pages.add(new CruciblePage(output));
        return this;
    }

    public CodexBuilder cruciblePage(ItemStack output, ResourceLocation recipeId) {
        pages.add(new CruciblePage(output, recipeId));
        return this;
    }

    public CodexBuilder chantPage(String textKey, Spell spell) {
        pages.add(new ChantPage(textKey, spell));
        return this;
    }

    public CodexBuilder titledRitualPage(String textKey, ResourceLocation ritualId) {
        pages.add(new TitledRitualPage(textKey, ritualId));
        return this;
    }

    public CodexBuilder titledRitualPage(String textKey, ItemStack ritualResult) {
        pages.add(new TitledRitualPage(textKey, ritualResult));
        return this;
    }

    public CodexBuilder titledRitualPage(String textKey, Ritual ritual) {
        pages.add(new TitledRitualPage(textKey, ritual));
        return this;
    }

    public CodexBuilder listPage(String textKey, ListPage.ListEntry... entries) {
        pages.add(new ListPage(textKey, entries));
        return this;
    }

    public CodexBuilder signPage(Sign sign) {
        pages.add(new SignPage(sign));
        return this;
    }

    public Chapter build() {
        if (titleKey == null) throw new IllegalStateException("Chapter must have a title.");
        return new Chapter(titleKey, pages.toArray(new Page[0]));
    }

    public CodexBuilder textPage(String translationKey) {
        addTextPages(translationKey, ItemStack.EMPTY, false);
        return this;
    }

    private void addTextPages(String translationKey, ItemStack icon, boolean isTitlePage) {
        Component text = Component.translatable(translationKey);
        String rawText = text.getString();
        String[] sentences = rawText.split("(?<=[.!?])\\s+");

        List<String> lines = new ArrayList<>();

        // Wrap sentences into lines
        for (String sentence : sentences) {
            List<String> sentenceLines = wrapTextToLines(sentence, 120); // wrap width
            lines.addAll(sentenceLines);
        }

        int linesPerPage = isTitlePage ? 12 : 13; // slightly fewer lines if there's a title
        int totalLines = lines.size();
        int pagesNeeded = (int) Math.ceil((double) totalLines / linesPerPage);

        for (int i = 0; i < pagesNeeded; i++) {
            int startLine = i * linesPerPage;
            int endLine = Math.min((i + 1) * linesPerPage, totalLines);
            List<String> pageLines = lines.subList(startLine, endLine);
            String pageText = String.join(" ", pageLines);

            if (i == 0 && isTitlePage) {
                // add only the first one as a title page
                pages.add(icon.isEmpty() ? new TitlePage(pageText, translationKey + ".title") : new TitlePage(pageText, icon));
            } else {
                pages.add(new TextPage(pageText));
            }
        }
    }

}
