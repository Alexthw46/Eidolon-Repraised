package alexthw.eidolon_repraised.registries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.Supplier;

import static alexthw.eidolon_repraised.registries.EidolonEntities.NECROMANCER;

public class EidolonRaiderTypes {

    public static final EnumProxy<Raid.RaiderType> NECROMANCER_RAIDER = new EnumProxy<>(
            Raid.RaiderType.class, getRaider(NECROMANCER.get()), new int[]{0, 0, 0, 0, 0, 1, 0, 1}
    );

    public static Supplier<EntityType<?>> getRaider(EntityType<?> type) {
        return () -> type;
    }
}
