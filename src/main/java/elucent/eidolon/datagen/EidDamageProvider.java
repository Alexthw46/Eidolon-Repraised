package elucent.eidolon.datagen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import elucent.eidolon.Eidolon;
import elucent.eidolon.util.DamageTypeData;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EidDamageProvider {

    public static class DamageTypeDataProvider extends DatapackBuiltinEntriesProvider {
        private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
                .add(Registries.DAMAGE_TYPE, DamageTypeDataProvider::bootstrap);


        public static void bootstrap(BootstapContext<DamageType> ctx) {
            DamageTypeData.allInNamespace(Eidolon.MODID).forEach(data -> data.register(ctx));
        }

        public DamageTypeDataProvider(PackOutput output, CompletableFuture<Provider> registries) {
            super(output, registries, BUILDER, Set.of(Eidolon.MODID));
        }

        public static DataProvider.Factory<DamageTypeDataProvider> makeFactory(CompletableFuture<Provider> registries) {
            return output -> new DamageTypeDataProvider(output, registries);
        }

        @Override
        @NotNull
        public String getName() {
            return "Eidolon's Damage Type Data";
        }
    }

    public static class DamageTypeTagGen extends TagsProvider<DamageType> {
        private final String namespace;

        public DamageTypeTagGen(String namespace, PackOutput pOutput,
                                CompletableFuture<Provider> pLookupProvider, ExistingFileHelper existingFileHelper) {
            super(pOutput, Registries.DAMAGE_TYPE, pLookupProvider, namespace, existingFileHelper);
            this.namespace = namespace;
        }

        public DamageTypeTagGen(PackOutput pOutput, CompletableFuture<Provider> pLookupProvider,
                                ExistingFileHelper existingFileHelper) {
            this(Eidolon.MODID, pOutput, pLookupProvider, existingFileHelper);
        }

        @Override
        protected void addTags(@NotNull Provider provider) {
            Multimap<TagKey<DamageType>, ResourceKey<DamageType>> tagsToTypes = HashMultimap.create();
            DamageTypeData.allInNamespace(namespace)
                    .forEach(data -> data.tags.forEach(tag -> tagsToTypes.put(tag, data.key)));
            tagsToTypes.asMap()
                    .forEach((tag, keys) -> {
                        TagAppender<DamageType> appender = tag(tag);
                        keys.forEach(key -> appender.addOptional(key.location()));
                    });
        }
    }
}
