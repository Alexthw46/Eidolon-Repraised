package alexthw.eidolon_repraised.util;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ClientInfo {
    public static float partialTicks = 0.0f;

    public static float deltaTicks = 0;
    public static float totalTicks = 0;
    @OnlyIn(Dist.CLIENT)
    public static float clientTicks = 0;

    private static void calcDelta() {
        float oldTotal = totalTicks;
        totalTicks = totalTicks + partialTicks;
        deltaTicks = totalTicks - oldTotal;
    }

    public static void renderTickStart(float pt) {
        partialTicks = pt;
        //clientTicks++;
    }

    public static void renderTickEnd() {
        calcDelta();
    }

    @OnlyIn(Dist.CLIENT)
    public static float getClientPartialTicks() {
        return clientTicks;
    }
}
