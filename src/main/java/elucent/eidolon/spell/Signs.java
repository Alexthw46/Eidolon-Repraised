package elucent.eidolon.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import elucent.eidolon.Eidolon;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.resources.ResourceLocation;

public class Signs {
    static final List<Sign> signs = new ArrayList<>();
    static final Map<ResourceLocation, Sign> signMap = new HashMap<>();

    public static Sign find(ResourceLocation loc) {
        return signMap.getOrDefault(loc, null);
    }

    public static Sign register(Sign sign) {
        signs.add(sign);
        signMap.put(sign.getRegistryName(), sign);
        return sign;
    }

    public static List<Sign> getSigns() {
        return signs;
    }

    public static final Sign
            WICKED_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "wicked"),
            new ResourceLocation(Eidolon.MODID, "particle/wicked_sign"),
            ColorUtil.packColor(255, 154, 77, 255)
    ));
    public static final Sign SACRED_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "sacred"),
            new ResourceLocation(Eidolon.MODID, "particle/sacred_sign"),
            ColorUtil.packColor(255, 255, 230, 117)
    ));
    public static final Sign BLOOD_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "blood"),
            new ResourceLocation(Eidolon.MODID, "particle/blood_sign"),
            ColorUtil.packColor(255, 255, 51, 85)
    ));
    public static final Sign SOUL_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "soul"),
            new ResourceLocation(Eidolon.MODID, "particle/soul_sign"),
            ColorUtil.packColor(255, 230, 138, 226)
    ));
    public static final Sign MIND_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "mind"),
            new ResourceLocation(Eidolon.MODID, "particle/mind_sign"),
            ColorUtil.packColor(255, 90, 121, 255)
    ));
    public static final Sign FLAME_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "flame"),
            new ResourceLocation(Eidolon.MODID, "particle/flame_sign"),
            ColorUtil.packColor(255, 255, 128, 64)
    ));
    public static final Sign WINTER_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "winter"),
            new ResourceLocation(Eidolon.MODID, "particle/winter_sign"),
            ColorUtil.packColor(255, 112, 149, 210)
    ));
    public static final Sign HARMONY_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "harmony"),
            new ResourceLocation(Eidolon.MODID, "particle/harmony_sign"),
            ColorUtil.packColor(255, 141, 141, 195)
    ));
    public static final Sign DEATH_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "death"),
            new ResourceLocation(Eidolon.MODID, "particle/death_sign"),
            ColorUtil.packColor(255, 123, 140, 70)
    ));
    public static final Sign WARDING_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "warding"),
            new ResourceLocation(Eidolon.MODID, "particle/warding_sign"),
            ColorUtil.packColor(255, 118, 204, 175)
    ));
    public static final Sign MAGIC_SIGN = register(new Sign(
            new ResourceLocation(Eidolon.MODID, "magic"),
            new ResourceLocation(Eidolon.MODID, "particle/magic_sign"),
            ColorUtil.packColor(255, 167, 85, 192)
    ));
}
