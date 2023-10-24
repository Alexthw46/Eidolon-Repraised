package elucent.eidolon.datagen;

import elucent.eidolon.Eidolon;
import elucent.eidolon.common.entity.SpellProjectileEntity;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EidEntityTagProvider extends EntityTypeTagsProvider {
    public EidEntityTagProvider(final DataGenerator dataGenerator, @Nullable final ExistingFileHelper existingFileHelper) {
        super(dataGenerator, Eidolon.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // Create empty tags so people know they exist
        tag(SpellProjectileEntity.TRACKABLE);
        tag(SpellProjectileEntity.TRACKABLE_BLACKLIST);
    }

    @Override
    public @NotNull String getName() {
        return "Eidolon Entity Type Tags";
    }
}
