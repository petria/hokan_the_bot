package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.engine.command.HelpGroup;
import org.freakz.hokan_ng_springboot.bot.engine.command.annotation.HelpGroups;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Petri Airio on 12.4.2016.
 * -
 */
@Component
@Scope("prototype")

@HelpGroups(
        helpGroups = {HelpGroup.PROCESS, HelpGroup.SYSTEM}
)
public class LastPIDCmd extends Cmd {

    public LastPIDCmd() {
        setHelp("Shows last PID of internal thread execution.");
    }

    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        long lastPID = propertyService.getPropertyAsLong(PropertyName.PROP_SYS_PID_COUNTER, -1);
        response.addResponse("Last PID: %d", lastPID);
    }

}
