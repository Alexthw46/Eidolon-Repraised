package elucent.eidolon.gui.jei;

/*
public class WorktableCategory implements IRecipeCategory<RecipeWrappers.Worktable> {
    static final ResourceLocation UID = new ResourceLocation(Eidolon.MODID, "worktable");
    private final IDrawable background, icon;

    public WorktableCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(new ResourceLocation(Eidolon.MODID, "textures/gui/jei_page_bg.png"), 0, 0, 138, 172);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Registry.WORKTABLE.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends RecipeWrappers.Worktable> getRecipeClass() {
        return RecipeWrappers.Worktable.class;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(I18n.get("jei." + Eidolon.MODID + ".worktable"));
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(RecipeWrappers.Worktable wrapper, IIngredients ingredients) {
        if (wrapper.page == null) wrapper.page = WorktableRegistry.getDefaultPage(wrapper.recipe);

        List<Ingredient> inputs = new ArrayList<>();
        for (Ingredient i : wrapper.recipe.getCore()) inputs.add(i);
        for (Ingredient i : wrapper.recipe.getOuter()) inputs.add(i);
        ingredients.setInputIngredients(inputs);
        ingredients.setOutput(VanillaTypes.ITEM, wrapper.recipe.getResult());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, RecipeWrappers.Worktable recipe, IIngredients ingredients) {
        IGuiItemStackGroup stacks = layout.getItemStacks();
        for (int i = 0; i < 3; i ++) {
            for (int j = 0; j < 3; j ++) {
                int index = i * 3 + j;
                stacks.init(index, true, 43 + j * 17, 36 + i * 17);
            }
        }

        stacks.init(9, true, 60, 14);
        stacks.init(10, true, 99, 53);
        stacks.init(11, true, 60, 92);
        stacks.init(12, true, 21, 53);
        stacks.init(13, false, 60, 133);
        stacks.set(ingredients);
    }

    @Override
    public void draw(RecipeWrappers.Worktable recipe, PoseStack mStack, double mouseX, double mouseY) {
        recipe.page.renderBackground(CodexGui.DUMMY, mStack, 5, 4, (int)mouseX, (int)mouseY);
        recipe.page.render(CodexGui.DUMMY, mStack, 5, 4, (int)mouseX, (int)mouseY);
    }
}
*/