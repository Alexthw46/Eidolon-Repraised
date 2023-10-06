package elucent.eidolon.common.ritual;

import elucent.eidolon.api.ritual.Ritual;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ExecCommandRitual extends Ritual {

    String command;

    public String getCommand() {
        return command;
    }

    public ExecCommandRitual(ResourceLocation symbol, int color, String command) {
        super(symbol, color);
        this.command = command;
    }

    @Override
    public Ritual cloneRitual() {
        return new ExecCommandRitual(getSymbol(), getColor(), command);
    }

    @Override
    public RitualResult start(Level world, BlockPos pos) {

        if (world instanceof ServerLevel) {


            var server = world.getServer();
            if (server.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
                try {
                    world.getEntitiesOfClass(Entity.class, new AABB(pos).inflate(10)).stream().findFirst().ifPresent(commandSender -> {
                        CommandSourceStack commandSource = commandSender.createCommandSourceStack().withPermission(2).withSuppressedOutput();
                        server.getCommands().performPrefixedCommand(commandSource, this.command);
                    });
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.forThrowable(throwable, "Executing command ritual");
                    CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                    crashreportcategory.setDetail("Command", this::getCommand);
                    throw new ReportedException(crashreport);
                }
            }

        }
        return RitualResult.TERMINATE;
    }


}
