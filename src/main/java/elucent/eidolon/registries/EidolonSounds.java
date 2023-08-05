package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EidolonSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Eidolon.MODID);
    public static final RegistryObject<SoundEvent>
            CAST_SOULFIRE_EVENT = addSound("cast_soulfire");
    public static final RegistryObject<SoundEvent> CAST_BONECHILL_EVENT = addSound("cast_bonechill");
    public static final RegistryObject<SoundEvent> SPLASH_SOULFIRE_EVENT = addSound("splash_soulfire");
    public static final RegistryObject<SoundEvent> SPLASH_BONECHILL_EVENT = addSound("splash_bonechill");
    public static final RegistryObject<SoundEvent> SELECT_RUNE = addSound("select_rune");
    public static final RegistryObject<SoundEvent> CHANT_WORD = addSound("chant_word");
    public static final RegistryObject<SoundEvent> PAROUSIA = addSound("parousia");

    public static final RegistryObject<SoundEvent> WRAITH_DEATH = addSound("wraith_death"),
            WRAITH_AMBIENT = addSound("wraith_ambient"),
            WRAITH_HURT = addSound("wraith_hurt");

    static RegistryObject<SoundEvent> addSound(String name) {
        SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Eidolon.MODID, name));
        return SOUND_EVENTS.register(name, () -> event);
    }
}
