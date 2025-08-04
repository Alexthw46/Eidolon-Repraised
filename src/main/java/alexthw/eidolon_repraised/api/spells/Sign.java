package alexthw.eidolon_repraised.api.spells;

import alexthw.eidolon_repraised.registries.Signs;
import alexthw.eidolon_repraised.util.ColorUtil;
import alexthw.eidolon_repraised.util.RGBProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record Sign(ResourceLocation key, ResourceLocation sprite, int color) implements RGBProvider {

    public static Codec<Sign> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("sign").forGetter(Sign::getRegistryName)//,
//                    ResourceLocation.CODEC.fieldOf("sprite").forGetter(Sign::sprite),
//                    Codec.INT.fieldOf("color").forGetter(Sign::color)
            ).apply(instance, Signs::find)
    );

    public static StreamCodec<RegistryFriendlyByteBuf, Sign> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            Sign::key,
//            ResourceLocation.STREAM_CODEC,
//            Sign::sprite,
//            ByteBufCodecs.INT,
//            Sign::color,
            Signs::find
    );

    public ResourceLocation getRegistryName() {
        return key;
    }

    public float getRed() {
        return ColorUtil.getRed(color) / 255.0f;
    }

    public float getGreen() {
        return ColorUtil.getGreen(color) / 255.0f;
    }

    public float getBlue() {
        return ColorUtil.getBlue(color) / 255.0f;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Sign && ((Sign) other).key.equals(key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
