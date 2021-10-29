package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.service.SystemScript;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@HelpGroups(
        helpGroups = {HelpGroup.SYSTEM}
)
public class VPenisCmd extends Cmd {

    public VPenisCmd() {
        setHelp("Executes system VPenis");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String[] strings = scriptRunnerService.runScript(SystemScript.VPENIS_SCRIPT);
        String sysVPenis = "<n/a> ";
        if (strings != null && strings.length > 0) {
            sysVPenis = strings[0];
        }
        response.addResponse(sysVPenis);

    }
}
