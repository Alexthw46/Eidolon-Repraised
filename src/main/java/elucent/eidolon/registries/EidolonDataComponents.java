package elucent.eidolon.registries;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static elucent.eidolon.Eidolon.MODID;

public class EidolonDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CONSACRATED = DATA.register("consecrated",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> NECROTIC = DATA.register("necrotic",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SOUL = DATA.register("soul",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<CompoundTag>>>
            THRALLS = DATA.register("thralls",
            () -> DataComponentType.<List<CompoundTag>>builder().persistent(Codec.list(CompoundTag.CODEC)).build());

}
