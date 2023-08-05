package elucent.eidolon.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static elucent.eidolon.Eidolon.prefix;

/**
 * An extension of {@link DamageType} that contains extra data, functionality, and utility.
 *
 * @see DamageType
 * @see DamageSource
 * @see DamageSources
 */
public class DamageTypeData {
    private static final List<DamageTypeData> all = new ArrayList<>();

    public final ResourceKey<DamageType> key;
    public final ResourceLocation id;
    public final DamageType type;
    public final Collection<TagKey<DamageType>> tags;

    private Holder<DamageType> holder;

    private final WorldAttached<DamageSource> staticSources;

    protected DamageTypeData(ResourceKey<DamageType> key, DamageType type, Collection<TagKey<DamageType>> tags) {
        this.key = key;
        this.id = key.location();
        this.type = type;
        this.tags = tags;
        this.staticSources = new WorldAttached<>(level -> new DamageSource(getHolder(level)));
    }

    public DamageSource source(LevelAccessor level) {
        return staticSources.get(level);
    }

    public DamageSource source(LevelAccessor level, @Nullable Entity entity) {
        return new DamageSource(getHolder(level), entity);
    }

    public DamageSource source(LevelAccessor level, @Nullable Entity cause, @Nullable Entity direct) {
        return new DamageSource(getHolder(level), cause, direct);
    }

    private Holder<DamageType> getHolder(LevelAccessor level) {
        if (this.holder == null) {
            Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
            this.holder = registry.getHolderOrThrow(key);
        }
        return holder;
    }

    public boolean is(@Nullable DamageSource source) {
        return source != null && is(source.type());
    }

    public boolean is(DamageType type) {
        return this.type.equals(type);
    }

    public void register(BootstapContext<DamageType> ctx) {
        ctx.register(this.key, this.type);
    }

    public static Stream<DamageTypeData> allInNamespace(String namespace) {
        return all.stream().filter(data -> data.id.getNamespace().equals(namespace));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        // required
        private String msgId;
        private ResourceLocation location;
        // defaulted
        private float exhaustion = 0;
        private DamageScaling scaling = DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER;
        private DamageEffects effects = DamageEffects.HURT;
        private DeathMessageType deathMessageType = DeathMessageType.DEFAULT;
        private final List<TagKey<DamageType>> tags = new ArrayList<>();

        /**
         * Set the ResourceLocation or ID of this type. This is a required field.
         */
        public Builder location(ResourceLocation location) {
            this.location = location;
            return this;
        }

        public Builder location(String path) {
            return location(prefix(path));
        }

        /**
         * Set both the location and msgId of this type from one ResourceLocation.
         * The msgId is set to "namespace.path".
         */
        public Builder simpleId(ResourceLocation location) {
            location(location);
            return msgId(location.getNamespace() + '.' + location.getPath());
        }

        public Builder simpleId(String path) {
            return simpleId(prefix(path));
        }

        /**
         * Set the message ID. this is used for death message lang keys. This is a required field.
         *
         * @see #deathMessageType(DeathMessageType)
         */
        public Builder msgId(String msgId) {
            this.msgId = msgId;
            return this;
        }

        /**
         * Set the exhaustion of this type. This is the amount of hunger that will be consumed when an entity is damaged.
         */
        public Builder exhaustion(float exhaustion) {
            this.exhaustion = exhaustion;
            return this;
        }

        /**
         * Set the scaling of this type. This determines whether damage is increased based on difficulty or not.
         */
        public Builder scaling(DamageScaling scaling) {
            this.scaling = scaling;
            return this;
        }

        /**
         * Set the effects of this type. This determines the sound that plays when damaged.
         */
        public Builder effects(DamageEffects effects) {
            this.effects = effects;
            return this;
        }

        /**
         * Set the death message type of this damage type. This determines how a death message lang key is assembled.
         * <ul>
         *     <li>{@link DeathMessageType#DEFAULT}: {@link DamageSource#getLocalizedDeathMessage}</li>
         *     <li>{@link DeathMessageType#FALL_VARIANTS}: {@link CombatTracker#getFallMessage(CombatEntry, Entity)}</li>
         *     <li>{@link DeathMessageType#INTENTIONAL_GAME_DESIGN}: "death.attack." + msgId, wrapped in brackets, linking to MCPE-28723</li>
         * </ul>
         */
        @SuppressWarnings("JavadocReference")
        public Builder deathMessageType(DeathMessageType type) {
            this.deathMessageType = type;
            return this;
        }

        @SafeVarargs
        public final Builder tag(TagKey<DamageType>... tags) {
            Collections.addAll(this.tags, tags);
            return this;
        }

        public DamageTypeData build() {
            if (location == null) {
                throw new IllegalArgumentException("location is required");
            }
            if (msgId == null) {
                throw new IllegalArgumentException("msgId is required");
            }

            DamageTypeData data = new DamageTypeData(
                    ResourceKey.create(Registries.DAMAGE_TYPE, location),
                    new DamageType(msgId, scaling, exhaustion, effects, deathMessageType),
                    tags
            );
            all.add(data);
            return data;
        }
    }

    public static class WorldAttached<T> {

        // weak references to prevent leaking hashmaps when a WorldAttached is GC'd during runtime
        static List<WeakReference<Map<LevelAccessor, ?>>> allMaps = new ArrayList<>();
        private final Map<LevelAccessor, T> attached;
        private final Function<LevelAccessor, T> factory;

        public WorldAttached(Function<LevelAccessor, T> factory) {
            this.factory = factory;
            // Weak key hashmaps prevent worlds not existing anywhere else from leaking memory.
            // This is only a fallback in the event that unload events fail to fire for any reason.
            attached = new WeakHashMap<>();
            allMaps.add(new WeakReference<>(attached));
        }

        public static void invalidateWorld(LevelAccessor world) {
            var i = allMaps.iterator();
            while (i.hasNext()) {
                Map<LevelAccessor, ?> map = i.next()
                        .get();
                if (map == null) {
                    // If the map has been GC'd, remove the weak reference
                    i.remove();
                } else {
                    // Prevent leaks
                    map.remove(world);
                }
            }
        }

        @Nonnull
        public T get(LevelAccessor world) {
            T t = attached.get(world);
            if (t != null) return t;
            T entry = factory.apply(world);
            put(world, entry);
            return entry;
        }

        public void put(LevelAccessor world, T entry) {
            attached.put(world, entry);
        }

        /**
         * Replaces the entry with a new one from the factory and returns the new entry.
         */
        @Nonnull
        public T replace(LevelAccessor world) {
            attached.remove(world);

            return get(world);
        }

        /**
         * Replaces the entry with a new one from the factory and returns the new entry.
         */
        @Nonnull
        public T replace(LevelAccessor world, Consumer<T> finalizer) {
            T remove = attached.remove(world);

            if (remove != null)
                finalizer.accept(remove);

            return get(world);
        }

        /**
         * Deletes all entries after calling a function on them.
         *
         * @param finalizer Do something with all the world-value pairs
         */
        public void empty(BiConsumer<LevelAccessor, T> finalizer) {
            attached.forEach(finalizer);
            attached.clear();
        }

        /**
         * Deletes all entries after calling a function on them.
         *
         * @param finalizer Do something with all of the values
         */
        public void empty(Consumer<T> finalizer) {
            attached.values()
                    .forEach(finalizer);
            attached.clear();
        }
    }
}