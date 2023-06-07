package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import elucent.eidolon.common.entity.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EidolonEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Eidolon.MODID);

    public static final RegistryObject<EntityType<ZombieBruteEntity>>
            ZOMBIE_BRUTE = addEntity("zombie_brute", 7969893, 44975, 1.2f, 2.5f, ZombieBruteEntity::new, MobCategory.MONSTER);
    public static final RegistryObject<EntityType<WraithEntity>>
            WRAITH = addEntity("wraith", 0x706e6b, 0xadacbd, 0.6f, 1.9f, WraithEntity::new, MobCategory.MONSTER);
    public static final RegistryObject<EntityType<SoulfireProjectileEntity>>
            SOULFIRE_PROJECTILE = addEntity("soulfire_projectile", 0.4f, 0.4f, SoulfireProjectileEntity::new, MobCategory.MISC);
    public static final RegistryObject<EntityType<BonechillProjectileEntity>>
            BONECHILL_PROJECTILE = addEntity("bonechill_projectile", 0.4f, 0.4f, BonechillProjectileEntity::new, MobCategory.MISC);
    public static final RegistryObject<EntityType<NecromancerSpellEntity>>
            NECROMANCER_SPELL = addEntity("necromancer_spell", 0.4f, 0.4f, NecromancerSpellEntity::new, MobCategory.MISC);
    public static final RegistryObject<EntityType<ChantCasterEntity>>
            CHANT_CASTER = addEntity("chant_caster", 0.1f, 0.1f, ChantCasterEntity::new, MobCategory.MISC);
    public static final RegistryObject<EntityType<AngelArrowEntity>>
            ANGEL_ARROW = addEntity("angel_arrow", 0.5f, 0.5f, AngelArrowEntity::new, MobCategory.MISC);
    public static final RegistryObject<EntityType<NecromancerEntity>>
            NECROMANCER = addEntity("necromancer", 0x69255e, 0x9ce8ff, 0.6f, 1.9f, NecromancerEntity::new, MobCategory.MONSTER);
    public static final RegistryObject<EntityType<RavenEntity>>
            RAVEN = addEntity("raven", 0x1e1f24, 0x404f66, 0.375f, 0.5f, RavenEntity::new, MobCategory.CREATURE);
    public static final RegistryObject<EntityType<SlimySlugEntity>>
            SLIMY_SLUG = addEntity("slimy_slug", 0xdbe388, 0x5f9e42, 0.5f, 0.25f, SlimySlugEntity::new, MobCategory.CREATURE);

    static <T extends Entity> RegistryObject<EntityType<T>> addEntity(String name, float width, float height, EntityType.EntityFactory<T> factory, MobCategory kind) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, kind)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .sized(width, height)
                .build(Eidolon.MODID + ":" + name));
    }

    static <T extends Mob> RegistryObject<EntityType<T>> addEntity(String name, int color1, int color2, float width, float height, EntityType.EntityFactory<T> factory, MobCategory kind) {
        //ITEMS.register("spawn_" + name, () -> new SpawnEggItem((EntityType<? extends T>) type, color1, color2, itemProps().tab(CreativeModeTab.TAB_MISC)));
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, kind)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .sized(width, height)
                .build(Eidolon.MODID + ":" + name));
    }
}
