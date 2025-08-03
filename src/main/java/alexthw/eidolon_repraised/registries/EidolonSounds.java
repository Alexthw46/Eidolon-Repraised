package alexthw.eidolon_repraised.registries;

import alexthw.eidolon_repraised.Eidolon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EidolonSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Eidolon.MODID);
    public static final DeferredHolder<SoundEvent, SoundEvent>
            CAST_SOULFIRE_EVENT = addSound("cast_soulfire");
    public static final DeferredHolder<SoundEvent, SoundEvent> CAST_BONECHILL_EVENT = addSound("cast_bonechill");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPLASH_SOULFIRE_EVENT = addSound("splash_soulfire");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPLASH_BONECHILL_EVENT = addSound("splash_bonechill");
    public static final DeferredHolder<SoundEvent, SoundEvent> SELECT_RUNE = addSound("select_rune");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHANT_WORD = addSound("chant_word");
    public static final DeferredHolder<SoundEvent, SoundEvent> PAROUSIA = addSound("parousia");

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_DEATH = addSound("wraith_death"),
            WRAITH_AMBIENT = addSound("wraith_ambient"),
            WRAITH_HURT = addSound("wraith_hurt");

    static DeferredHolder<SoundEvent, SoundEvent> addSound(String name) {
        SoundEvent event = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Eidolon.MODID,name ));
        return SOUND_EVENTS.register(name, () -> event);
    }
}
