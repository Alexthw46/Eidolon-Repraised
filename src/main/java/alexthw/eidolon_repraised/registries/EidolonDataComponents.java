package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.common.item.ResearchNotesItem;
import alexthw.eidolon_repraised.common.item.SummoningStaffItem;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static alexthw.eidolon_repraised.Eidolon.MODID;

public class EidolonDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CONSECRATED = DATA.register("consecrated",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> NECROTIC = DATA.register("necrotic",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COOLDOWN = DATA.register("cooldown",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SANGUINE_CHARGES = DATA.register("sanguine_charges",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TARGET_MODE = DATA.register("target_mode",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SummoningStaffItem.ThrallData>>
            THRALLS = DATA.register("thralls",
            () -> DataComponentType.<SummoningStaffItem.ThrallData>builder().persistent(SummoningStaffItem.ThrallData.CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Sign>>> SPELL = DATA.register("spell",
            () -> DataComponentType.<List<Sign>>builder().persistent(Codec.list(Sign.CODEC)).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResearchNotesItem.ResearchData>> RESEARCH = DATA.register("research",
            () -> DataComponentType.<ResearchNotesItem.ResearchData>builder().persistent(ResearchNotesItem.ResearchData.CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Sign>> SIGN = DATA.register("sign",
            () -> DataComponentType.<Sign>builder().persistent(Sign.CODEC).build());
}
