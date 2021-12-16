package elucent.eidolon.ritual;

import elucent.eidolon.Eidolon;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.PrimaryLevelData;

public class DaylightRitual extends Ritual {
    public static final ResourceLocation SYMBOL = new ResourceLocation(Eidolon.MODID, "particle/daylight_ritual");

    public DaylightRitual() {
        super(SYMBOL, ColorUtil.packColor(255, 255, 245, 130));
    }

    @Override
    public RitualResult tick(Level world, BlockPos pos) {
        if (world.getDayTime() % 24000 < 1000 || world.getDayTime() % 24000 >= 12000) {
            if (!world.isClientSide) {
                ((PrimaryLevelData) world.getLevelData()).setDayTime(world.getDayTime() + 100);
                for (ServerPlayer player : ((ServerLevel) world).players()) {
                    player.connection.send(new ClientboundSetTimePacket(world.getGameTime(), world.getDayTime(), world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
                }
            }
            return RitualResult.PASS;
        }
        else return RitualResult.TERMINATE;
    }
}
