package elucent.eidolon.recipe;

import elucent.eidolon.codex.Page;
import elucent.eidolon.codex.WorktablePage;
import elucent.eidolon.util.RecipeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorktableRegistry {
    static final Map<ResourceLocation, WorktableRecipe> recipes = new HashMap<>();

    public static WorktableRecipe register(WorktableRecipe recipe) {
        ResourceLocation loc = recipe.getRegistryName();
        assert loc != null;
        recipes.put(loc, recipe);
        return recipe;
    }

    public static Page getDefaultPage(WorktableRecipe recipe) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Object o : recipe.core) stacks.add(RecipeUtil.stackFromObject(o));
        for (Object o : recipe.extras) stacks.add(RecipeUtil.stackFromObject(o));
        return new WorktablePage(recipe.result.copy(), stacks.toArray(new ItemStack[0]));
    }

    public static WorktableRecipe find(ResourceLocation loc) {
        return recipes.get(loc);
    }

    public static WorktableRecipe find(Container core, Container outer) {
        for (WorktableRecipe recipe : recipes.values()) if (recipe.matches(core, outer)) return recipe;
        return null;
    }
}
