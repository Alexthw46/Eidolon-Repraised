package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.common.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static elucent.eidolon.registries.Registry.ITEMS;

public class EidolonEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Eidolon.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<ZombieBruteEntity>>
            ZOMBIE_BRUTE = addEntity("zombie_brute", 7969893, 44975, 1.2f, 2.5f, ZombieBruteEntity::new, MobCategory.MONSTER);
    public static final DeferredHolder<EntityType<?>, EntityType<GiantSkeletonEntity>>
            GIANT_SKEL = addEntity("giant_skeleton", 0x706e6b, 0xadacbd, 1.2f, 2.75f, GiantSkeletonEntity::new, MobCategory.MONSTER);

    public static final DeferredHolder<EntityType<?>, EntityType<WraithEntity>>
            WRAITH = addEntity("wraith", 0x706e6b, 0xadacbd, 0.6f, 1.9f, WraithEntity::new, MobCategory.MONSTER);
    public static final DeferredHolder<EntityType<?>, EntityType<SoulfireProjectileEntity>>
            SOULFIRE_PROJECTILE = addEntity("soulfire_projectile", 0.4f, 0.4f, SoulfireProjectileEntity::new, MobCategory.MISC);
    public static final DeferredHolder<EntityType<?>, EntityType<BonechillProjectileEntity>>
            BONECHILL_PROJECTILE = addEntity("bonechill_projectile", 0.4f, 0.4f, BonechillProjectileEntity::new, MobCategory.MISC);
    public static final DeferredHolder<EntityType<?>, EntityType<NecromancerSpellEntity>>
            NECROMANCER_SPELL = addEntity("necromancer_spell", 0.4f, 0.4f, NecromancerSpellEntity::new, MobCategory.MISC);
    public static final DeferredHolder<EntityType<?>, EntityType<ChantCasterEntity>>
            CHANT_CASTER = addEntity("chant_caster", 0.1f, 0.1f, ChantCasterEntity::new, MobCategory.MISC);

    public static final DeferredHolder<EntityType<?>, EntityType<NecromancerEntity>>
            NECROMANCER = addEntity("necromancer", 0x69255e, 0x9ce8ff, 0.6f, 2.2f, NecromancerEntity::new, MobCategory.MONSTER);
    public static final DeferredHolder<EntityType<?>, EntityType<RavenEntity>>
            RAVEN = addEntity("raven", 0x1e1f24, 0x404f66, 0.375f, 0.5f, RavenEntity::new, MobCategory.CREATURE);
    public static final DeferredHolder<EntityType<?>, EntityType<SlimySlugEntity>>
            SLIMY_SLUG = addEntity("slimy_slug", 0xdbe388, 0x5f9e42, 0.5f, 0.25f, SlimySlugEntity::new, MobCategory.CREATURE);

    static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> addEntity(String name, float width, float height, EntityType.EntityFactory<T> factory, MobCategory kind) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, kind)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .sized(width, height)
                .build(Eidolon.MODID + ":" + name));
    }

    static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> addEntity(String name, int color1, int color2, float width, float height, EntityType.EntityFactory<T> factory, MobCategory kind) {
        var type = ENTITIES.register(name, () -> EntityType.Builder.of(factory, kind)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .sized(width, height)
                .build(Eidolon.MODID + ":" + name));
        ITEMS.register("spawn_" + name, () -> new DeferredSpawnEggItem(type, color1, color2, new Item.Properties()));
        return type;
    }
}
