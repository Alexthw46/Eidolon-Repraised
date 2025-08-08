package alexthw.eidolon_repraised.api.altar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record AltarEntry(
        ResourceLocation key,
        double capacity,
        double power
) {
    public AltarEntry(ResourceLocation key, double power) {
        this(key, 0, 0);
    }

    public void apply(AltarInfo info) {
        info.attributes.computeIfAbsent(key, (k) -> new AltarInfo.AltarAttributes());
        if (capacity > 0) info.increaseCapacity(key, capacity);
        if (power > 0) info.increasePower(key, power);
        // if (callback != null) callback.accept(info);
    }

    public static final Codec<AltarEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("key").forGetter(AltarEntry::key),
            Codec.DOUBLE.optionalFieldOf("capacity", 0.0).forGetter(AltarEntry::capacity),
            Codec.DOUBLE.optionalFieldOf("power", 0.0).forGetter(AltarEntry::power)
    ).apply(instance, AltarEntry::new));
}
