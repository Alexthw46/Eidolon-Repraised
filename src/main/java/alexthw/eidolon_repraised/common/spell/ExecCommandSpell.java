package alexthw.eidolon_repraised.common.spell;

import alexthw.eidolon_repraised.api.spells.Sign;
import alexthw.eidolon_repraised.util.EidolonFakePlayer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExecCommandSpell extends StaticSpell {

    private final List<String> commands;

    public ExecCommandSpell(ResourceLocation id, int manaCost, Sign[] signs, List<String> commands) {
        super(id, manaCost, signs);
        this.commands = commands;
    }

    @Override
    public boolean canCast(Level world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public void cast(Level world, BlockPos pos, Player player) {
        if (world instanceof ServerLevel serverWorld) {
            var server = world.getServer();
            for (var command : commands)
                if (server.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(command)) {
                    try {
                        if (player == null) {
                            EidolonFakePlayer fakePlayer = EidolonFakePlayer.getPlayer(serverWorld);
                            fakePlayer.setPos(pos.getCenter());
                            player = fakePlayer;
                        }
                        CommandSourceStack commandSource = player.createCommandSourceStack().withPermission(2).withSuppressedOutput();
                        server.getCommands().performPrefixedCommand(commandSource, command);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.forThrowable(throwable, "Executing command ritual");
                        CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                        crashreportcategory.setDetail("Command", command);
                        throw new ReportedException(crashreport);
                    }
                }

        }
    }
}