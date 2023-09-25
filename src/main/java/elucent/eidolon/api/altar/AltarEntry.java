package elucent.eidolon.api.altar;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class AltarEntry {
    double capacity = 0, power = 0;
    ResourceLocation key;
    Consumer<AltarInfo> callback = null;

    public AltarEntry(ResourceLocation key) {
        this.key = key;
    }

    public AltarEntry setCapacity(double capacity) {
        this.capacity = capacity;
        return this;
    }

    public AltarEntry setPower(double power) {
        this.power = power;
        return this;
    }

    public AltarEntry setCallback(Consumer<AltarInfo> callback) {
        this.callback = callback;
        return this;
    }

    public AltarEntry setKey(ResourceLocation key) {
        this.key = key;
        return this;
    }

    void apply(AltarInfo info) {
        info.attributes.computeIfAbsent(key, (k) -> new AltarInfo.AltarAttributes());
        if (capacity > 0) info.increaseCapacity(key, capacity);
        if (power > 0) info.increasePower(key, power);
        if (callback != null) callback.accept(info);
    }
}
