package elucent.eidolon.common.ritual;

import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.util.EidolonFakePlayer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import var;
import java.util.List;

public class ExecCommandRitual extends Ritual {

    List<String> commands;

    public List<String> getCommands() {
        return commands;
    }

    public ExecCommandRitual(ResourceLocation symbol, int color, List<String> command) {
        super(symbol, color);
        this.commands = command;
    }

    @Override
    public Ritual cloneRitual() {
        return new ExecCommandRitual(getSymbol(), getColor(), commands);
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {

        if (world instanceof ServerLevel serverWorld) {

            var server = world.getServer();
            for (var command : commands)
                if (server.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(command)) {
                    try {
                        EidolonFakePlayer fakePlayer = EidolonFakePlayer.getPlayer(serverWorld);
                        fakePlayer.setPos(pos.getCenter());
                        CommandSourceStack commandSource = fakePlayer.createCommandSourceStack().withPermission(2).withSuppressedOutput();
                        server.getCommands().performPrefixedCommand(commandSource, command);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.forThrowable(throwable, "Executing command ritual");
                        CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                        crashreportcategory.setDetail("Command", command);
                        throw new ReportedException(crashreport);
                    }
                }

        }
        return RitualResult.TERMINATE;
    }


}
