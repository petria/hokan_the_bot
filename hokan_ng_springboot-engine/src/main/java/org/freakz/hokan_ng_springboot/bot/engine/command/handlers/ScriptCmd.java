package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.models.ScriptResult;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandArgs;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Petri Airio on 5.4.2016.
 * -
 */
@Component
@HelpGroups(
        helpGroups = {HelpGroup.SYSTEM}
)
@Scope("prototype")

public class ScriptCmd extends Cmd {

    public ScriptCmd() {
        setHelp("Executes JavaScript.");
        setNoWeb(true);
/*    UnflaggedOption opt = new UnflaggedOption(ARG_SCRIPT)
        .setRequired(true)
        .setGreedy(true);
    registerParameter(opt);*/

//    setAdminUserOnly(true);
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        CommandArgs args = new CommandArgs(request.getIrcEvent().getMessage());
        String script = args.getArgs();
        ServiceResponse serviceResponse = doServicesRequest(ServiceRequestType.SCRIPT_SERVICE_REQUEST, request.getIrcEvent(), script, "JavaScript");
        ScriptResult scriptResult = serviceResponse.getScriptResult();
        response.addResponse("%s", scriptResult.getScriptOutput());
    }
}
