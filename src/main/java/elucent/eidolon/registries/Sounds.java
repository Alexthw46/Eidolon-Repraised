package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Sounds {
    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Eidolon.MODID);
    public static RegistryObject<SoundEvent>
            CAST_SOULFIRE_EVENT = addSound("cast_soulfire");
    public static RegistryObject<SoundEvent> CAST_BONECHILL_EVENT = addSound("cast_bonechill");
    public static RegistryObject<SoundEvent> SPLASH_SOULFIRE_EVENT = addSound("splash_soulfire");
    public static RegistryObject<SoundEvent> SPLASH_BONECHILL_EVENT = addSound("splash_bonechill");
    public static RegistryObject<SoundEvent> SELECT_RUNE = addSound("select_rune");
    public static RegistryObject<SoundEvent> CHANT_WORD = addSound("chant_word");
    public static RegistryObject<SoundEvent> PAROUSIA = addSound("parousia");

    static RegistryObject<SoundEvent> addSound(String name) {
        SoundEvent event = new SoundEvent(new ResourceLocation(Eidolon.MODID, name));
        return SOUND_EVENTS.register(name, () -> event);
    }
}
