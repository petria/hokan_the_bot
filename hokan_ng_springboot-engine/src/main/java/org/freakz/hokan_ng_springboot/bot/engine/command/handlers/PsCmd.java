package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;

/**
 * Created by Petri Airio on 18.5.2015.
 */
/*@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.PROCESS, HelpGroup.SYSTEM}
)
TODO FIX

*/
public class PsCmd extends Cmd {

    public PsCmd() {
        setHelp("Shows active processes running in Bot.");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

 /*       CommandRunner commandRunner = commandPool.getActiveRunners().get(0);

        List<CommandHistory> allRunning = new ArrayList<>();
        for (HokanModule module : HokanModule.values()) {
            long sessionId = propertyService.getPropertyAsLong(module.getModuleProperty(), -1);
            if (sessionId != -1) {
                List<CommandHistory> running = commandHistoryService.findByHokanModuleAndSessionIdAndCommandStatus(module.toString(), sessionId, CommandStatus.RUNNING);
               allRunning.addAll(running);
            }
        }
        if (allRunning.size() > 0) {
            Comparator<CommandHistory> comparator = (o1, o2) -> {
                Long pid1 = o1.getPid();
                Long pid2 = o2.getPid();
                return pid1.compareTo(pid2);
            };
            Collections.sort(allRunning, comparator);

            response.addResponse("%6s - %10s - %-13s - %s\n", "PID", "STARTED_BY", "MODULE", "CLASS");
            for (CommandHistory cmd : allRunning) {
                response.addResponse("%6d - %10s - %-13s - %s\n", cmd.getPid(), cmd.getStartedBy(), cmd.getHokanModule(),
                        cmd.getRunnable().replaceAll("class org.freakz.hokan_ng_springboot.bot.", ""));
            }
        }
*/
    }

}
