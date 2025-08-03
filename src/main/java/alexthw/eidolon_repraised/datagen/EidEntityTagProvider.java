package alexthw.eidolon_repraised.datagen;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.common.entity.SpellProjectileEntity;
import alexthw.eidolon_repraised.common.spell.ThrallSpell;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EidEntityTagProvider extends EntityTypeTagsProvider {
    public EidEntityTagProvider(final DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> provider, @Nullable final ExistingFileHelper existingFileHelper) {
        super(dataGenerator.getPackOutput(), provider, Eidolon.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        // Create empty tags so people know they exist
        tag(SpellProjectileEntity.TRACKABLE);
        tag(SpellProjectileEntity.TRACKABLE_BLACKLIST);
        tag(ThrallSpell.ENTHRALL_BLACKLIST);
        tag(ThrallSpell.ENTHRALL_WHITELIST);
    }

    @Override
    public @NotNull String getName() {
        return "Eidolon Entity Type Tags";
    }
}
