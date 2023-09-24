package elucent.eidolon.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;

import java.util.HashMap;
import java.util.Map;

public class WorktableRegistry {
    static final Map<ResourceLocation, WorktableRecipe> recipes = new HashMap<>();

    public static WorktableRecipe register(WorktableRecipe recipe) {
        ResourceLocation loc = recipe.getRegistryName();
        assert loc != null;
        recipes.put(loc, recipe);
        return recipe;
    }

    public static WorktableRecipe find(ResourceLocation loc) {
        return recipes.get(loc);
    }

    public static WorktableRecipe find(Container core, Container outer) {
        for (WorktableRecipe recipe : recipes.values()) if (recipe.matches(core, outer)) return recipe;
        return null;
    }
}
