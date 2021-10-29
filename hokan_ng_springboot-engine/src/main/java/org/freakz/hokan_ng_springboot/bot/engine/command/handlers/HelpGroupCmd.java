package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Petri Airio on 18.9.2015.
 */
@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.HELP}
)
public class HelpGroupCmd extends Cmd {

    public HelpGroupCmd() {
        super();
        setHelp("Shows command by HelpGroup they belong to.");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {

        Comparator<Cmd> comparator = (cmd1, cmd2) -> cmd1.getName().compareTo(cmd2.getName());

        StringBuilder sb = new StringBuilder();
        sb.append("== COMMANDS BY HELP GROUPS ==\n");

        for (HelpGroup helpGroup : HelpGroup.values()) {
            sb.append(String.format("%14s ::", helpGroup.getGroupName()));

            List<Cmd> groupCommands = getOtherCmdsInGroup(helpGroup);
            Collections.sort(groupCommands, comparator);

            for (Cmd cmd : groupCommands) {
                if (cmd.isChannelOpOnly() && !isChannelOp && !isAdminUser) {
                    continue;
                }

                if (cmd.isAdminUserOnly() && (!isAdminUser)) {
                    continue;
                }

                sb.append("  ");
                sb.append(cmd.getName());
                String flags = "";
                if (cmd.toBotOnly) {
                    flags += "B";
                }
                if (cmd.channelOpOnly) {
                    flags += "C";
                }
                if (cmd.loggedInOnly) {
                    flags += "L";
                }
                if (cmd.adminUserOnly) {
                    flags += "A";
                }
                if (cmd.privateOnly) {
                    flags += "P";
                }
                if (flags.length() > 0) {
                    sb.append("[").append(flags).append("]");
                }
            }
            sb.append("\n");
        }
        response.addResponse(sb.toString());
    }
}
