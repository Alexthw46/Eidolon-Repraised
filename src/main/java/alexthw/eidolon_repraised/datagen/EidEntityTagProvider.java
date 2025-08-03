package alexthw.eidolon_repraised.datagen;

import alexthw.eidolon_repraised.Eidolon;
import alexthw.eidolon_repraised.common.entity.SpellProjectileEntity;
import alexthw.eidolon_repraised.common.spell.ThrallSpell;
import alexthw.eidolon_repraised.registries.EidolonEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
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
        tag(EntityTypeTags.UNDEAD).add(EidolonEntities.WRAITH.get(), EidolonEntities.ZOMBIE_BRUTE.get(), EidolonEntities.GIANT_SKEL.get(), EidolonEntities.NECROMANCER.get());

    }

    @Override
    public @NotNull String getName() {
        return "Eidolon Entity Type Tags";
    }
}
