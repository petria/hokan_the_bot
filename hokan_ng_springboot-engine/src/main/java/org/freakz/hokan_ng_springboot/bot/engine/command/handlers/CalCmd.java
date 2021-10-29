package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.SystemScriptResult;
import org.freakz.hokan_ng_springboot.bot.common.service.SystemScript;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.jibble.pircbot.Colors;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by Petri Airio on 10.5.2016.
 * -
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.UTILITY}
)
public class CalCmd extends Cmd {

    public CalCmd() {
        setHelp("Shows the Calendar of current month.");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        SystemScriptResult calendar = scriptRunnerService.runAndGetResult(SystemScript.CAL_SCRIPT);
        String day = String.format(" %d", LocalDateTime.now().getDayOfMonth());
        StringBuilder sb = new StringBuilder();
        for (String line : calendar.getOriginalOutput()) {
            if (line.trim().length() > 0) {
                line = line.replaceFirst(day, Colors.BOLD + day + Colors.NORMAL);
                sb.append(line).append("\n");
            }
        }
        response.addResponse("%s", sb.toString());
    }

}
